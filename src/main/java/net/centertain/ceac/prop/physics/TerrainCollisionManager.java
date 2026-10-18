package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import com.mojang.logging.LogUtils;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bytedeco.javacpp.IntPointer;
import org.slf4j.Logger;

import java.util.*;

import static cc.modlabs.box3d.global.Box3D.*;

public final class TerrainCollisionManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final double TERRAIN_BOUNDS_EPSILON = 1.0e-4;
    private static final int TERRAIN_VALIDATION_INTERVAL = 20;
    private static final int TERRAIN_VALIDATION_BUDGET_PER_TICK = 128;
    private static final int TERRAIN_MESH_HALO = 2;
    private static final double TERRAIN_FACE_EPSILON = 1.0e-7;
    private static final float TERRAIN_MESH_WELD_TOLERANCE = 1.0e-4f;

    private static final long CATEGORY_WORLD = 1L;
    private static final long CATEGORY_SOLID_PROP = 1L << 1;
    private static final long CATEGORY_PASSIVE_PROP = 1L << 2;
    private static final long CATEGORY_ENTITY = 1L << 3;

    private static final long MASK_WORLD =
            CATEGORY_SOLID_PROP |
            CATEGORY_PASSIVE_PROP |
            CATEGORY_ENTITY;

    private final PropPhysManager manager;

    private final List<TerrainPatch> terrainPatches = new ArrayList<>();
    final Set<PropPhysManager.BodyKey> terrainPatchBodies = new HashSet<>();
    private final Set<Long> terrainPatchCellKeys = new HashSet<>();
    private final Map<Long, BlockState> terrainPatchStates = new HashMap<>();
    private boolean terrainPatchesDirty = true;

    private final Set<UUID> coveredPropIds = new HashSet<>();
    private final Map<UUID, TerrainCoverage> terrainCoverageByProp = new HashMap<>();
    private final Map<Long, Integer> terrainReferenceCounts = new HashMap<>();
    private final ArrayDeque<Long> terrainValidationQueue = new ArrayDeque<>();

    private int terrainValidationTicks;

    TerrainCollisionManager(PropPhysManager manager) {
        this.manager = manager;
    }


    void markTerrainDirtyAround(BlockPos position) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                for (int dz = -1; dz <= 1; dz++) {
                    long key = position.offset(dx, dy, dz).asLong();
                    if (terrainPatchCellKeys.contains(key) || terrainReferenceCounts.containsKey(key)) {
                        terrainPatchesDirty = true;
                        return;
                    }
                }
    }

    void onChunkLoaded() {
        terrainPatchesDirty = true;
    }

    void onChunkUnloaded() {
        destroyTerrainPatches();
        terrainPatchesDirty = true;
    }

    void synchronizeTerrain() {
        coveredPropIds.clear();

        for (Prop prop : manager.activeProps()) {
            if (prop.getCollisionMode() == PropCollisionMode.NONE)
                continue;

            PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            PropPhysManager.GeometryData geometry = manager.getGeometry(definition.getModelId());
            if (geometry == null)
                continue;

            UUID propId = prop.getId();
            long transformRevision = prop.getTransformRevision();
            TerrainCoverage previous = terrainCoverageByProp.get(propId);
            if (
                    previous != null &&
                            previous.definitionId().equals(prop.getDefinitionId()) &&
                            previous.transformRevision() == transformRevision
            ) {
                coveredPropIds.add(propId);
                continue;
            }

            AABB bounds = PropPhysManager.worldBounds(geometry, prop.getTransform());
            if (
                    !Double.isFinite(bounds.minX)
                            || !Double.isFinite(bounds.minY)
                            || !Double.isFinite(bounds.minZ)
                            || !Double.isFinite(bounds.maxX)
                            || !Double.isFinite(bounds.maxY)
                            || !Double.isFinite(bounds.maxZ)
                            || bounds.minX > bounds.maxX
                            || bounds.minY > bounds.maxY
                            || bounds.minZ > bounds.maxZ
            )
                continue;

            int minX = PropPhysManager.floor(bounds.minX - TERRAIN_BOUNDS_EPSILON);
            int minY = Math.max(manager.level.getMinBuildHeight(), PropPhysManager.floor(bounds.minY - TERRAIN_BOUNDS_EPSILON));
            int minZ = PropPhysManager.floor(bounds.minZ - TERRAIN_BOUNDS_EPSILON);
            int maxX = PropPhysManager.floor(bounds.maxX + TERRAIN_BOUNDS_EPSILON);
            int maxY = Math.min(manager.level.getMaxBuildHeight() - 1, PropPhysManager.floor(bounds.maxY + TERRAIN_BOUNDS_EPSILON));
            int maxZ = PropPhysManager.floor(bounds.maxZ + TERRAIN_BOUNDS_EPSILON);
            if (minY > maxY)
                continue;

            coveredPropIds.add(propId);
            if (previous != null && previous.matches(minX, minY, minZ, maxX, maxY, maxZ)) {
                terrainCoverageByProp.put(propId, new TerrainCoverage(
                        minX, minY, minZ,
                        maxX, maxY, maxZ,
                        prop.getDefinitionId(),
                        transformRevision,
                        previous.keys()
                ));
                continue;
            }

            Set<Long> nextKeys = new HashSet<>();
            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    for (int z = minZ; z <= maxZ; z++)
                        nextKeys.add(new BlockPos(x, y, z).asLong());
            if (previous != null)
                for (Long key : previous.keys())
                    if (!nextKeys.contains(key))
                        releaseTerrainKey(key);
            for (Long key : nextKeys)
                if (previous == null || !previous.keys().contains(key))
                    retainTerrainKey(key);

            terrainCoverageByProp.put(propId, new TerrainCoverage(
                    minX, minY, minZ,
                    maxX, maxY, maxZ,
                    prop.getDefinitionId(),
                    transformRevision,
                    nextKeys
            ));
        }

        Iterator<Map.Entry<UUID, TerrainCoverage>> coverageIterator = terrainCoverageByProp.entrySet().iterator();

        while (coverageIterator.hasNext()) {
            Map.Entry<UUID, TerrainCoverage> entry = coverageIterator.next();
            if (coveredPropIds.contains(entry.getKey()))
                continue;
            for (Long key : entry.getValue().keys())
                releaseTerrainKey(key);
            coverageIterator.remove();
        }

        validateTerrainPatchesIncrementally();
        if (terrainPatchesDirty)
            rebuildTerrainPatches();
    }

    private void retainTerrainKey(long key) {
        int references = terrainReferenceCounts.getOrDefault(key, 0);
        terrainReferenceCounts.put(key, references + 1);
        if (references == 0)
            terrainPatchesDirty = true;
    }

    private void releaseTerrainKey(long key) {
        Integer references = terrainReferenceCounts.get(key);
        if (references == null)
            return;
        if (references > 1) {
            terrainReferenceCounts.put(key, references - 1);
            return;
        }
        terrainReferenceCounts.remove(key);
        terrainPatchesDirty = true;
    }

    @SuppressWarnings({"ExtractMethodRecommender", "deprecation"})
    private void rebuildTerrainPatches() {
        destroyTerrainPatches();

        terrainPatchCellKeys.clear();
        terrainPatchStates.clear();
        terrainValidationQueue.clear();
        terrainValidationTicks = 0;

        if (terrainReferenceCounts.isEmpty()) {
            terrainPatchesDirty = false;
            return;
        }

        Set<Long> expandedCells = new HashSet<>();
        for (long key : terrainReferenceCounts.keySet()) {
            BlockPos position = BlockPos.of(key);
            for (int dx = -TERRAIN_MESH_HALO; dx <= TERRAIN_MESH_HALO; dx++)
                for (int dy = -TERRAIN_MESH_HALO; dy <= TERRAIN_MESH_HALO; dy++)
                    for (int dz = -TERRAIN_MESH_HALO; dz <= TERRAIN_MESH_HALO; dz++) {
                        int y = position.getY() + dy;
                        if (y < manager.level.getMinBuildHeight() || y >= manager.level.getMaxBuildHeight())
                            continue;
                        expandedCells.add(new BlockPos(
                                position.getX() + dx,
                                y,
                                position.getZ() + dz
                        ).asLong());
                    }
        }

        terrainPatchCellKeys.addAll(expandedCells);
        Map<Long, List<TerrainBox>> boxesByCell = new HashMap<>();

        for (long key : expandedCells) {
            BlockPos position = BlockPos.of(key);

            if (!manager.level.hasChunkAt(position))
                continue;

            BlockState state = manager.level.getBlockState(position);
            terrainPatchStates.put(key, state);

            VoxelShape shape = state.getCollisionShape(manager.level, position);
            if (shape.isEmpty())
                continue;

            List<TerrainBox> cellBoxes = new ArrayList<>();
            for (AABB box : shape.toAabbs()) {
                TerrainBox terrainBox = new TerrainBox(
                        position.getX() + box.minX,
                        position.getY() + box.minY,
                        position.getZ() + box.minZ,
                        position.getX() + box.maxX,
                        position.getY() + box.maxY,
                        position.getZ() + box.maxZ
                );
                if (
                        terrainBox.maxX() - terrainBox.minX() <= TERRAIN_FACE_EPSILON ||
                                terrainBox.maxY() - terrainBox.minY() <= TERRAIN_FACE_EPSILON ||
                                terrainBox.maxZ() - terrainBox.minZ() <= TERRAIN_FACE_EPSILON
                )
                    continue;

                cellBoxes.add(terrainBox);
            }

            if (!cellBoxes.isEmpty())
                boxesByCell.put(key, cellBoxes);
        }

        Set<Long> remaining = new HashSet<>(expandedCells);
        while (!remaining.isEmpty()) {
            long seed = remaining.iterator().next();

            Set<Long> component = new HashSet<>();
            ArrayDeque<Long> queue = new ArrayDeque<>();

            remaining.remove(seed);
            queue.addLast(seed);

            while (!queue.isEmpty()) {
                long currentKey = queue.removeFirst();
                component.add(currentKey);
                BlockPos position = BlockPos.of(currentKey);

                for (int dx = -1; dx <= 1; dx++)
                    for (int dy = -1; dy <= 1; dy++)
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0)
                                continue;
                            long neighborKey = new BlockPos(
                                    position.getX() + dx,
                                    position.getY() + dy,
                                    position.getZ() + dz
                            ).asLong();
                            if (remaining.remove(neighborKey))
                                queue.addLast(neighborKey);
                        }
            }

            List<TerrainBox> boxes = new ArrayList<>();
            for (long key : component) {
                List<TerrainBox> cellBoxes = boxesByCell.get(key);
                if (cellBoxes != null)
                    boxes.addAll(cellBoxes);
            }

            TerrainPatch patch = createTerrainPatch(component, boxes);
            if (patch != null) {
                terrainPatches.add(patch);
                terrainPatchBodies.add(PropPhysManager.BodyKey.of(patch.body()));
            }
        }

        terrainPatchesDirty = false;
    }

    @SuppressWarnings("deprecation")
    private void validateTerrainPatchesIncrementally() {
        if (terrainValidationQueue.isEmpty()) {
            if (++terrainValidationTicks < TERRAIN_VALIDATION_INTERVAL)
                return;
            terrainValidationTicks = 0;
            terrainValidationQueue.addAll(terrainPatchStates.keySet());
        }

        int processed = 0;
        while (processed < TERRAIN_VALIDATION_BUDGET_PER_TICK && !terrainValidationQueue.isEmpty()) {
            long key = terrainValidationQueue.removeFirst();
            processed++;
            BlockState cached = terrainPatchStates.get(key);
            if (cached == null)
                continue;

            BlockPos position = BlockPos.of(key);
            if (!manager.level.hasChunkAt(position)) {
                terrainPatchesDirty = true;
                continue;
            }

            BlockState current = manager.level.getBlockState(position);
            if (!cached.equals(current))
                terrainPatchesDirty = true;
        }
    }

    @SuppressWarnings("resource")
    private TerrainPatch createTerrainPatch(
            Set<Long> cells,
            List<TerrainBox> boxes
    ) {
        if (boxes.isEmpty())
            return null;

        int originX = Integer.MAX_VALUE;
        int originY = Integer.MAX_VALUE;
        int originZ = Integer.MAX_VALUE;
        for (long key : cells) {
            BlockPos position = BlockPos.of(key);
            originX = Math.min(originX, position.getX());
            originY = Math.min(originY, position.getY());
            originZ = Math.min(originZ, position.getZ());
        }

        TerrainFaceIndex faceIndex = new TerrainFaceIndex();
        for (TerrainBox box : boxes)
            faceIndex.add(box);

        List<PropPhysManager.FloatPoint> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        Map<TerrainPointKey, Integer> vertexIndices = new HashMap<>();

        for (TerrainBox box : boxes) {
            for (int axis = 0; axis < 3; axis++) {
                appendTerrainFace(
                        box, axis, false, faceIndex,
                        vertices, indices, vertexIndices,
                        originX, originY, originZ
                );
                appendTerrainFace(
                        box, axis, true, faceIndex,
                        vertices, indices, vertexIndices,
                        originX, originY, originZ
                );
            }
        }
        if (vertices.size() < 3 || indices.size() < 3)
            return null;

        b3MeshData mesh;
        try (
                b3Vec3 nativeVertices = new b3Vec3(vertices.size());
                IntPointer nativeIndices = new IntPointer(indices.size());
                b3MeshDef meshDefinition = new b3MeshDef()
        ) {
            for (int i = 0; i < vertices.size(); i++) {
                PropPhysManager.FloatPoint point = vertices.get(i);

                nativeVertices.position(i)
                        .x(point.x())
                        .y(point.y())
                        .z(point.z());
            }
            for (int i = 0; i < indices.size(); i++)
                nativeIndices.put(i, indices.get(i));

            nativeVertices.position(0);
            nativeIndices.position(0);

            meshDefinition.vertices(nativeVertices);
            meshDefinition.stride(0);
            meshDefinition.indices(nativeIndices);
            meshDefinition.materialIndices(null);

            meshDefinition.vertexCount(vertices.size());
            meshDefinition.triangleCount(indices.size() / 3);

            meshDefinition.weldVertices(true);
            meshDefinition.weldTolerance(TERRAIN_MESH_WELD_TOLERANCE);
            meshDefinition.identifyEdges(true);
            meshDefinition.useMedianSplit(true);
            meshDefinition.clockWiseWinding(false);

            mesh = b3CreateMesh(meshDefinition, (IntPointer) null, 0);
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to build welded terrain mesh", exception);
            return null;
        }
        if (mesh == null || mesh.isNull())
            return null;

        b3BodyId body = null;

        try {
            b3BodyDef bodyDefinition = b3DefaultBodyDef();
            bodyDefinition.type(b3_staticBody);
            bodyDefinition._position(new b3Pos()
                    .x(originX)
                    .y(originY)
                    .z(originZ)
            );
            bodyDefinition.rotation(PropPhysManager.identityQuat());

            body = b3CreateBody(manager.world, bodyDefinition);

            b3ShapeDef shapeDefinition = PropPhysManager.makeShapeDefinition(
                    CATEGORY_WORLD,
                    MASK_WORLD,
                    false,
                    false,
                    false,
                    0.0f
            );

            b3SurfaceMaterial material = b3DefaultSurfaceMaterial();
            material.friction(0.2f);
            shapeDefinition.baseMaterial(material);

            try (b3Vec3 scale = new b3Vec3().x(1.0f).y(1.0f).z(1.0f)) {
                b3CreateMeshShape(body, shapeDefinition, mesh, scale);
            }
            return new TerrainPatch(body, mesh);
        } catch (RuntimeException exception) {
            if (body != null)
                b3DestroyBody(body);
            b3DestroyMesh(mesh);
            LOGGER.error("Failed to attach welded terrain mesh", exception);
            return null;
        }
    }

    private static void appendTerrainFace(
            TerrainBox box,
            int axis,
            boolean positive,
            TerrainFaceIndex faceIndex,
            List<PropPhysManager.FloatPoint> vertices,
            List<Integer> indices,
            Map<TerrainPointKey, Integer> vertexIndices,
            int originX,
            int originY,
            int originZ
    ) {
        double plane = positive ? box.max(axis) : box.min(axis);

        int uAxis = axis == 0 ? 1 : 0;
        int vAxis = axis == 2 ? 1 : 2;

        double minU = box.min(uAxis);
        double maxU = box.max(uAxis);
        double minV = box.min(vAxis);
        double maxV = box.max(vAxis);

        TreeSet<Double> uCuts = new TreeSet<>();
        TreeSet<Double> vCuts = new TreeSet<>();

        uCuts.add(minU);
        uCuts.add(maxU);
        vCuts.add(minV);
        vCuts.add(maxV);

        List<TerrainBox> overlappingNeighbors = new ArrayList<>();

        for (TerrainBox neighbor : faceIndex.touching(axis, plane, positive)) {
            if (neighbor == box)
                continue;

            double overlapMinU = Math.max(minU, neighbor.min(uAxis));
            double overlapMaxU = Math.min(maxU, neighbor.max(uAxis));
            double overlapMinV = Math.max(minV, neighbor.min(vAxis));
            double overlapMaxV = Math.min(maxV, neighbor.max(vAxis));
            if (
                    overlapMaxU - overlapMinU <= TERRAIN_FACE_EPSILON ||
                    overlapMaxV - overlapMinV <= TERRAIN_FACE_EPSILON
            )
                continue;

            overlappingNeighbors.add(neighbor);

            uCuts.add(overlapMinU);
            uCuts.add(overlapMaxU);
            vCuts.add(overlapMinV);
            vCuts.add(overlapMaxV);
        }

        List<Double> us = new ArrayList<>(uCuts);
        List<Double> vs = new ArrayList<>(vCuts);

        for (int ui = 0; ui + 1 < us.size(); ui++) {
            double u0 = us.get(ui);
            double u1 = us.get(ui + 1);
            if (u1 - u0 <= TERRAIN_FACE_EPSILON)
                continue;

            for (int vi = 0; vi + 1 < vs.size(); vi++) {
                double v0 = vs.get(vi);
                double v1 = vs.get(vi + 1);
                if (v1 - v0 <= TERRAIN_FACE_EPSILON)
                    continue;

                double middleU = (u0 + u1) * 0.5;
                double middleV = (v0 + v1) * 0.5;
                boolean hidden = false;

                for (TerrainBox neighbor : overlappingNeighbors)
                    if (
                            middleU > neighbor.min(uAxis) &&
                            middleU < neighbor.max(uAxis) &&
                            middleV > neighbor.min(vAxis) &&
                            middleV < neighbor.max(vAxis)
                    ) {
                        hidden = true;
                        break;
                    }
                if (hidden)
                    continue;

                appendTerrainQuad(
                        axis, positive, plane,
                        u0, u1, v0, v1,
                        vertices, indices, vertexIndices,
                        originX, originY, originZ
                );
            }
        }
    }

    private static void appendTerrainQuad(
            int axis,
            boolean positive,
            double plane,
            double u0,
            double u1,
            double v0,
            double v1,
            List<PropPhysManager.FloatPoint> vertices,
            List<Integer> indices,
            Map<TerrainPointKey, Integer> vertexIndices,
            int originX,
            int originY,
            int originZ
    ) {
        double[][] points;

        if (axis == 0)
            // X-facing surface
            // u=Y, v=Z
            points = positive ? new double[][] {
                    { plane, u0, v0 },
                    { plane, u1, v0 },
                    { plane, u1, v1 },
                    { plane, u0, v1 }
            } : new double[][] {
                    { plane, u0, v0 },
                    { plane, u0, v1 },
                    { plane, u1, v1 },
                    { plane, u1, v0 }
            };
        else if (axis == 1)
            // Y-facing surface
            // u=X, v=Z
            points = positive ? new double[][] {
                    { u0, plane, v0 },
                    { u0, plane, v1 },
                    { u1, plane, v1 },
                    { u1, plane, v0 }
            } : new double[][] {
                    { u0, plane, v0 },
                    { u1, plane, v0 },
                    { u1, plane, v1 },
                    { u0, plane, v1 }
            };
        else
            // Z-facing surface
            // u=X, v=Y
            points = positive ? new double[][] {
                    { u0, v0, plane },
                    { u1, v0, plane },
                    { u1, v1, plane },
                    { u0, v1, plane }
            } : new double[][] {
                    { u0, v0, plane },
                    { u0, v1, plane },
                    { u1, v1, plane },
                    { u1, v0, plane }
            };

        int first = terrainVertexIndex(points[0], vertices, vertexIndices, originX, originY, originZ);
        int second = terrainVertexIndex(points[1], vertices, vertexIndices, originX, originY, originZ);
        int third = terrainVertexIndex(points[2], vertices, vertexIndices, originX, originY, originZ);
        int fourth = terrainVertexIndex(points[3], vertices, vertexIndices, originX, originY, originZ);

        indices.add(first);
        indices.add(second);
        indices.add(third);

        indices.add(first);
        indices.add(third);
        indices.add(fourth);
    }

    private static int terrainVertexIndex(
            double[] worldPoint,
            List<PropPhysManager.FloatPoint> vertices,
            Map<TerrainPointKey, Integer> vertexIndices,
            int originX,
            int originY,
            int originZ
    ) {
        float x = (float) (worldPoint[0] - originX);
        float y = (float) (worldPoint[1] - originY);
        float z = (float) (worldPoint[2] - originZ);
        TerrainPointKey key = new TerrainPointKey(
                Float.floatToIntBits(x),
                Float.floatToIntBits(y),
                Float.floatToIntBits(z)
        );
        Integer existing = vertexIndices.get(key);
        if (existing != null)
            return existing;

        int index = vertices.size();
        vertices.add(new PropPhysManager.FloatPoint(x, y, z));
        vertexIndices.put(key, index);

        return index;
    }

    void destroyTerrainPatches() {
        for (TerrainPatch patch : terrainPatches) {
            terrainPatchBodies.remove(PropPhysManager.BodyKey.of(patch.body()));
            b3DestroyBody(patch.body());
            b3DestroyMesh(patch.mesh());
        }
        terrainPatches.clear();
        terrainPatchBodies.clear();
    }

    void clear() {
        destroyTerrainPatches();

        coveredPropIds.clear();
        terrainCoverageByProp.clear();
        terrainReferenceCounts.clear();
        terrainValidationQueue.clear();
        terrainPatchCellKeys.clear();
        terrainPatchStates.clear();
        terrainValidationTicks = 0;
        terrainPatchesDirty = true;
    }

    private static long terrainPlaneKey(double coordinate) {
        return Math.round(coordinate * 1_000_000.0);
    }


    private static final class TerrainFaceIndex {
        private final Map<Long, List<TerrainBox>>[] minPlanes = newPlaneMaps();
        private final Map<Long, List<TerrainBox>>[] maxPlanes = newPlaneMaps();

        @SuppressWarnings("unchecked")
        private static Map<Long, List<TerrainBox>>[] newPlaneMaps() {
            return new Map[] {
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>()
            };
        }

        private void add(TerrainBox box) {
            addPlane(minPlanes[0], box.minX(), box);
            addPlane(minPlanes[1], box.minY(), box);
            addPlane(minPlanes[2], box.minZ(), box);

            addPlane(maxPlanes[0], box.maxX(), box);
            addPlane(maxPlanes[1], box.maxY(), box);
            addPlane(maxPlanes[2], box.maxZ(), box);
        }

        private static void addPlane(
                Map<Long, List<TerrainBox>> planes,
                double coordinate,
                TerrainBox box
        ) {
            planes.computeIfAbsent(
                    terrainPlaneKey(coordinate),
                    ignored -> new ArrayList<>()
            ).add(box);
        }

        private List<TerrainBox> touching(
                int axis,
                double plane,
                boolean positive
        ) {
            Map<Long, List<TerrainBox>> planes = positive ? minPlanes[axis] : maxPlanes[axis];
            return planes.getOrDefault(terrainPlaneKey(plane), List.of());
        }
    }


    private record TerrainPatch(b3BodyId body, b3MeshData mesh) {}
    private record TerrainBox(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ
    ) {
        private double min(int axis) {
            return switch (axis) {
                case 0 -> minX;
                case 1 -> minY;
                default -> minZ;
            };
        }
        private double max(int axis) {
            return switch (axis) {
                case 0 -> maxX;
                case 1 -> maxY;
                default -> maxZ;
            };
        }
    }
    private record TerrainPointKey(int x, int y, int z) {}
    private record TerrainCoverage(
            int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ,
            net.minecraft.resources.ResourceLocation definitionId,
            long transformRevision,
            Set<Long> keys
    ) {
        private boolean matches(
                int otherMinX, int otherMinY, int otherMinZ,
                int otherMaxX, int otherMaxY, int otherMaxZ
        ) {
            return
                    minX == otherMinX &&
                    minY == otherMinY &&
                    minZ == otherMinZ &&
                    maxX == otherMaxX &&
                    maxY == otherMaxY &&
                    maxZ == otherMaxZ;
        }
    }
}
