package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import com.mojang.logging.LogUtils;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bytedeco.javacpp.IntPointer;
import org.joml.Matrix4f;
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

    private static final int TERRAIN_TILE_SIZE = 16;
    private static final int TERRAIN_TILE_GUARD = 2;

    private static final long CATEGORY_WORLD = 1L;
    private static final long CATEGORY_SOLID_PROP = 1L << 1;
    private static final long CATEGORY_PASSIVE_PROP = 1L << 2;
    private static final long CATEGORY_ENTITY = 1L << 3;

    private static final long MASK_WORLD =
            CATEGORY_SOLID_PROP |
            CATEGORY_PASSIVE_PROP |
            CATEGORY_ENTITY;

    private final PropPhysManager manager;

    private final Map<TerrainTileKey, TerrainPatch> terrainPatches = new HashMap<>();
    final Set<PropPhysManager.BodyKey> terrainPatchBodies = new HashSet<>();
    private final Map<Long, BlockState> terrainPatchStates = new HashMap<>();
    private final Map<TerrainTileKey, Set<Long>> terrainStateKeysByTile = new HashMap<>();
    private final Set<TerrainTileKey> dirtyTerrainTiles = new HashSet<>();

    private final Set<UUID> coveredPropIds = new HashSet<>();
    private final Map<UUID, TerrainCoverage> terrainCoverageByProp = new HashMap<>();
    private final Map<Long, Integer> terrainReferenceCounts = new HashMap<>();
    private final Map<Long, Integer> terrainMeshReferenceCounts = new HashMap<>();
    private final ArrayDeque<Long> terrainValidationQueue = new ArrayDeque<>();

    private int terrainValidationTicks;

    TerrainCollisionManager(PropPhysManager manager) {
        this.manager = manager;
    }


    void markTerrainDirtyAround(BlockPos position) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos affected = position.offset(dx, dy, dz);
                    long key = affected.asLong();
                    if (
                            terrainMeshReferenceCounts.containsKey(key) ||
                            terrainPatchStates.containsKey(key)
                    )
                        markTerrainTileAroundCell(affected);
                }
            }
        }
    }

    void onChunkLoaded(ChunkPos chunkPos) {
        markTerrainTilesAroundChunk(chunkPos);
    }

    void onChunkUnloaded(ChunkPos chunkPos) {
        markTerrainTilesAroundChunk(chunkPos);
        for (TerrainTileKey tile : new ArrayList<>(dirtyTerrainTiles))
            if (terrainPatches.containsKey(tile))
                destroyTerrainPatch(tile);
    }

    void synchronizeTerrain() {
        coveredPropIds.clear();

        for (Prop prop : manager.activeProps()) {
            if (prop.getCollisionMode() == PropCollisionMode.NONE)
                continue;

            PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            PropPhysGeometry.GeometryData geometry = manager.getGeometry(definition.getModelId());
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

            Matrix4f transform = prop.getTransform();
            AABB bounds = PropPhysGeometry.worldBounds(geometry, transform);
            if (
                    !Double.isFinite(bounds.minX) ||
                    !Double.isFinite(bounds.minY) ||
                    !Double.isFinite(bounds.minZ) ||
                    !Double.isFinite(bounds.maxX) ||
                    !Double.isFinite(bounds.maxY) ||
                    !Double.isFinite(bounds.maxZ) ||
                    bounds.minX > bounds.maxX ||
                    bounds.minY > bounds.maxY ||
                    bounds.minZ > bounds.maxZ
            )
                continue;

            int minX = PropPhysGeometry.floor(bounds.minX - TERRAIN_BOUNDS_EPSILON);
            int minY = Math.max(
                    manager.level.getMinBuildHeight(),
                    PropPhysGeometry.floor(bounds.minY - TERRAIN_BOUNDS_EPSILON)
            );
            int minZ = PropPhysGeometry.floor(bounds.minZ - TERRAIN_BOUNDS_EPSILON);

            int maxX = PropPhysGeometry.floor(bounds.maxX + TERRAIN_BOUNDS_EPSILON);
            int maxY = Math.min(
                    manager.level.getMaxBuildHeight() - 1,
                    PropPhysGeometry.floor(bounds.maxY + TERRAIN_BOUNDS_EPSILON)
            );
            int maxZ = PropPhysGeometry.floor(bounds.maxZ + TERRAIN_BOUNDS_EPSILON);
            if (minY > maxY)
                continue;

            coveredPropIds.add(propId);

            Set<Long> nextKeys = geometryCoverageKeys(
                    geometry.geometry,
                    transform,
                    minY,
                    maxY
            );
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
        rebuildTerrainPatches();
    }

    private Set<Long> geometryCoverageKeys(
            PropGeometry geometry,
            Matrix4f transform,
            int minBuildY,
            int maxBuildY
    ) {
        Set<Long> keys = new HashSet<>();

        for (PropGeometry.Triangle triangle : geometry.getTriangles()) {
            Vec3 a = triangle.a();
            Vec3 b = triangle.b();
            Vec3 c = triangle.c();

            double ax = transform.m00() * a.x + transform.m10() * a.y + transform.m20() * a.z + transform.m30();
            double ay = transform.m01() * a.x + transform.m11() * a.y + transform.m21() * a.z + transform.m31();
            double az = transform.m02() * a.x + transform.m12() * a.y + transform.m22() * a.z + transform.m32();

            double bx = transform.m00() * b.x + transform.m10() * b.y + transform.m20() * b.z + transform.m30();
            double by = transform.m01() * b.x + transform.m11() * b.y + transform.m21() * b.z + transform.m31();
            double bz = transform.m02() * b.x + transform.m12() * b.y + transform.m22() * b.z + transform.m32();

            double cx = transform.m00() * c.x + transform.m10() * c.y + transform.m20() * c.z + transform.m30();
            double cy = transform.m01() * c.x + transform.m11() * c.y + transform.m21() * c.z + transform.m31();
            double cz = transform.m02() * c.x + transform.m12() * c.y + transform.m22() * c.z + transform.m32();

            if (
                    !Double.isFinite(ax) || !Double.isFinite(ay) || !Double.isFinite(az) ||
                    !Double.isFinite(bx) || !Double.isFinite(by) || !Double.isFinite(bz) ||
                    !Double.isFinite(cx) || !Double.isFinite(cy) || !Double.isFinite(cz)
            )
                continue;

            int x0 = PropPhysGeometry.floor(Math.min(ax, Math.min(bx, cx)) - TERRAIN_BOUNDS_EPSILON);
            int x1 = PropPhysGeometry.floor(Math.max(ax, Math.max(bx, cx)) + TERRAIN_BOUNDS_EPSILON);

            int y0 = Math.max(minBuildY, PropPhysGeometry.floor(Math.min(ay, Math.min(by, cy)) - TERRAIN_BOUNDS_EPSILON));
            int y1 = Math.min(maxBuildY, PropPhysGeometry.floor(Math.max(ay, Math.max(by, cy)) + TERRAIN_BOUNDS_EPSILON));

            int z0 = PropPhysGeometry.floor(Math.min(az, Math.min(bz, cz)) - TERRAIN_BOUNDS_EPSILON);
            int z1 = PropPhysGeometry.floor(Math.max(az, Math.max(bz, cz)) + TERRAIN_BOUNDS_EPSILON);

            if (y0 > y1)
                continue;

            for (int x = x0; x <= x1; x++)
                for (int y = y0; y <= y1; y++)
                    for (int z = z0; z <= z1; z++)
                        if (triangleIntersectsUnitCell(
                                ax, ay, az,
                                bx, by, bz,
                                cx, cy, cz,
                                x, y, z
                        ))
                            keys.add(BlockPos.asLong(x, y, z));
        }

        return keys;
    }

    private static boolean triangleIntersectsUnitCell(
            double ax, double ay, double az,
            double bx, double by, double bz,
            double cx, double cy, double cz,
            int cellX, int cellY, int cellZ
    ) {
        double centerX = cellX + 0.5;
        double centerY = cellY + 0.5;
        double centerZ = cellZ + 0.5;

        ax -= centerX;
        ay -= centerY;
        az -= centerZ;

        bx -= centerX;
        by -= centerY;
        bz -= centerZ;

        cx -= centerX;
        cy -= centerY;
        cz -= centerZ;

        double minX = Math.min(ax, Math.min(bx, cx));
        double maxX = Math.max(ax, Math.max(bx, cx));
        double minY = Math.min(ay, Math.min(by, cy));
        double maxY = Math.max(ay, Math.max(by, cy));
        double minZ = Math.min(az, Math.min(bz, cz));
        double maxZ = Math.max(az, Math.max(bz, cz));

        double epsilon = TERRAIN_BOUNDS_EPSILON;
        if (
                minX > 0.5 + epsilon || maxX < -0.5 - epsilon ||
                minY > 0.5 + epsilon || maxY < -0.5 - epsilon ||
                minZ > 0.5 + epsilon || maxZ < -0.5 - epsilon
        )
            return false;

        double abx = bx - ax;
        double aby = by - ay;
        double abz = bz - az;

        double bcx = cx - bx;
        double bcy = cy - by;
        double bcz = cz - bz;

        double cax = ax - cx;
        double cay = ay - cy;
        double caz = az - cz;

        double acx = cx - ax;
        double acy = cy - ay;
        double acz = cz - az;

        double nx = aby * acz - abz * acy;
        double ny = abz * acx - abx * acz;
        double nz = abx * acy - aby * acx;
        if (!axisOverlapsUnitBox(
                nx, ny, nz,
                ax, ay, az,
                bx, by, bz,
                cx, cy, cz
        ))
            return false;

        return
                edgeAxesOverlap(abx, aby, abz, ax, ay, az, bx, by, bz, cx, cy, cz) &&
                edgeAxesOverlap(bcx, bcy, bcz, ax, ay, az, bx, by, bz, cx, cy, cz) &&
                edgeAxesOverlap(cax, cay, caz, ax, ay, az, bx, by, bz, cx, cy, cz);
    }

    private static boolean edgeAxesOverlap(
            double ex, double ey, double ez,
            double ax, double ay, double az,
            double bx, double by, double bz,
            double cx, double cy, double cz
    ) {
        return axisOverlapsUnitBox(
                0.0, -ez, ey,
                ax, ay, az, bx, by, bz, cx, cy, cz
        ) && axisOverlapsUnitBox(
                ez, 0.0, -ex,
                ax, ay, az, bx, by, bz, cx, cy, cz
        ) && axisOverlapsUnitBox(
                -ey, ex, 0.0,
                ax, ay, az, bx, by, bz, cx, cy, cz
        );
    }

    private static boolean axisOverlapsUnitBox(
            double axisX, double axisY, double axisZ,
            double ax, double ay, double az,
            double bx, double by, double bz,
            double cx, double cy, double cz
    ) {
        double projectionA = axisX * ax + axisY * ay + axisZ * az;
        double projectionB = axisX * bx + axisY * by + axisZ * bz;
        double projectionC = axisX * cx + axisY * cy + axisZ * cz;

        double minProjection = Math.min(projectionA, Math.min(projectionB, projectionC));
        double maxProjection = Math.max(projectionA, Math.max(projectionB, projectionC));

        double radius = 0.5 * (Math.abs(axisX) + Math.abs(axisY) + Math.abs(axisZ)) + TERRAIN_BOUNDS_EPSILON;

        return minProjection <= radius && maxProjection >= -radius;
    }

    private void retainTerrainKey(long key) {
        terrainReferenceCounts.merge(key, 1, Integer::sum);

        BlockPos position = BlockPos.of(key);

        for (int dx = -TERRAIN_MESH_HALO; dx <= TERRAIN_MESH_HALO; dx++)
            for (int dy = -TERRAIN_MESH_HALO; dy <= TERRAIN_MESH_HALO; dy++)
                for (int dz = -TERRAIN_MESH_HALO; dz <= TERRAIN_MESH_HALO; dz++) {
                    int y = position.getY() + dy;
                    if (
                            y < manager.level.getMinBuildHeight() ||
                            y >= manager.level.getMaxBuildHeight()
                    )
                        continue;

                    BlockPos meshPosition = new BlockPos(
                            position.getX() + dx,
                            y,
                            position.getZ() + dz
                    );

                    long meshKey = meshPosition.asLong();
                    int references = terrainMeshReferenceCounts.getOrDefault(meshKey, 0);

                    terrainMeshReferenceCounts.put(meshKey, references + 1);
                    if (references == 0)
                        markTerrainTileAroundCell(meshPosition);
                }
    }

    private void releaseTerrainKey(long key) {
        Integer references = terrainReferenceCounts.get(key);
        if (references == null)
            return;
        if (references > 1)
            terrainReferenceCounts.put(key, references - 1);
        else
            terrainReferenceCounts.remove(key);

        BlockPos position = BlockPos.of(key);

        for (int dx = -TERRAIN_MESH_HALO; dx <= TERRAIN_MESH_HALO; dx++)
            for (int dy = -TERRAIN_MESH_HALO; dy <= TERRAIN_MESH_HALO; dy++)
                for (int dz = -TERRAIN_MESH_HALO; dz <= TERRAIN_MESH_HALO; dz++) {
                    int y = position.getY() + dy;
                    if (
                            y < manager.level.getMinBuildHeight() ||
                            y >= manager.level.getMaxBuildHeight()
                    )
                        continue;

                    BlockPos meshPosition = new BlockPos(
                            position.getX() + dx,
                            y,
                            position.getZ() + dz
                    );

                    long meshKey = meshPosition.asLong();
                    Integer meshReferences = terrainMeshReferenceCounts.get(meshKey);
                    if (meshReferences == null)
                        continue;

                    if (meshReferences > 1) {
                        terrainMeshReferenceCounts.put(meshKey, meshReferences - 1);
                    } else {
                        terrainMeshReferenceCounts.remove(meshKey);
                        removeTerrainState(meshKey);
                        markTerrainTileAroundCell(meshPosition);
                    }
                }
    }

    private void rebuildTerrainPatches() {
        if (dirtyTerrainTiles.isEmpty())
            return;

        List<TerrainTileKey> dirty = new ArrayList<>(dirtyTerrainTiles);
        dirtyTerrainTiles.clear();

        for (TerrainTileKey tile : dirty)
            rebuildTerrainPatch(tile);
    }

    @SuppressWarnings("deprecation")
    private void rebuildTerrainPatch(TerrainTileKey tile) {
        destroyTerrainPatch(tile);

        int tileMinX = tile.x() * TERRAIN_TILE_SIZE;
        int tileMinY = tile.y() * TERRAIN_TILE_SIZE;
        int tileMinZ = tile.z() * TERRAIN_TILE_SIZE;

        int tileMaxX = tileMinX + TERRAIN_TILE_SIZE - 1;
        int tileMaxY = tileMinY + TERRAIN_TILE_SIZE - 1;
        int tileMaxZ = tileMinZ + TERRAIN_TILE_SIZE - 1;

        int coreMinY = Math.max(manager.level.getMinBuildHeight(), tileMinY);
        int coreMaxY = Math.min(manager.level.getMaxBuildHeight() - 1, tileMaxY);
        if (coreMinY > coreMaxY)
            return;

        Set<Long> cells = new HashSet<>();

        for (int x = tileMinX; x <= tileMaxX; x++)
            for (int y = coreMinY; y <= coreMaxY; y++)
                for (int z = tileMinZ; z <= tileMaxZ; z++) {
                    long key = new BlockPos(x, y, z).asLong();
                    if (terrainMeshReferenceCounts.containsKey(key))
                        cells.add(key);
                }
        if (cells.isEmpty())
            return;

        int contextMinX = tileMinX - TERRAIN_TILE_GUARD;
        int contextMaxX = tileMaxX + TERRAIN_TILE_GUARD;
        int contextMinY = Math.max(manager.level.getMinBuildHeight(), tileMinY - TERRAIN_TILE_GUARD);
        int contextMaxY = Math.min(manager.level.getMaxBuildHeight() - 1, tileMaxY + TERRAIN_TILE_GUARD);
        int contextMinZ = tileMinZ - TERRAIN_TILE_GUARD;
        int contextMaxZ = tileMaxZ + TERRAIN_TILE_GUARD;

        List<TerrainBox> boxes = new ArrayList<>();
        Map<Long, BlockState> nextStates = new HashMap<>();
        boolean hasOwnedCollisionBox = false;

        for (int x = contextMinX; x <= contextMaxX; x++)
            for (int y = contextMinY; y <= contextMaxY; y++)
                for (int z = contextMinZ; z <= contextMaxZ; z++) {
                    long key = new BlockPos(x, y, z).asLong();
                    if (!terrainMeshReferenceCounts.containsKey(key))
                        continue;

                    BlockPos position = BlockPos.of(key);
                    if (
                            !manager.level.hasChunkAt(position) ||
                            !manager.loadedChunks.containsKey(new ChunkPos(position).toLong())
                    )
                        continue;

                    BlockState state = manager.level.getBlockState(position);

                    boolean ownedCell =
                            x >= tileMinX && x <= tileMaxX &&
                            y >= coreMinY && y <= coreMaxY &&
                            z >= tileMinZ && z <= tileMaxZ;
                    if (ownedCell)
                        nextStates.put(key, state);

                    VoxelShape shape = state.getCollisionShape(manager.level, position);
                    if (shape.isEmpty())
                        continue;

                    List<TerrainBox> cellBoxes = new ArrayList<>();

                    for (AABB box : shape.toAabbs()) {
                        TerrainBox terrainBox = new TerrainBox(
                                x + box.minX,
                                y + box.minY,
                                z + box.minZ,
                                x + box.maxX,
                                y + box.maxY,
                                z + box.maxZ
                        );
                        if (
                                terrainBox.maxX() - terrainBox.minX() <= TERRAIN_FACE_EPSILON ||
                                terrainBox.maxY() - terrainBox.minY() <= TERRAIN_FACE_EPSILON ||
                                terrainBox.maxZ() - terrainBox.minZ() <= TERRAIN_FACE_EPSILON
                        )
                            continue;

                        cellBoxes.add(terrainBox);
                    }
                    if (cellBoxes.isEmpty())
                        continue;

                    boxes.addAll(cellBoxes);
                    if (ownedCell)
                        hasOwnedCollisionBox = true;
                }

        replaceTerrainStatesForTile(tile, nextStates);
        if (!hasOwnedCollisionBox)
            return;

        TerrainPatch patch = createTerrainPatch(cells, boxes);
        if (patch != null) {
            terrainPatches.put(tile, patch);
            terrainPatchBodies.add(PropPhysManager.BodyKey.of(patch.body()));
        }
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

        while (
                processed < TERRAIN_VALIDATION_BUDGET_PER_TICK &&
                !terrainValidationQueue.isEmpty()
        ) {
            long key = terrainValidationQueue.removeFirst();
            processed++;
            BlockState cached = terrainPatchStates.get(key);
            if (cached == null)
                continue;

            BlockPos position = BlockPos.of(key);
            if (
                    !manager.level.hasChunkAt(position) ||
                    !manager.loadedChunks.containsKey(new ChunkPos(position).toLong())
            ) {
                markTerrainTileAroundCell(position);
                continue;
            }

            BlockState current = manager.level.getBlockState(position);
            if (!cached.equals(current))
                markTerrainTileAroundCell(position);
        }
    }

    private static TerrainTileKey terrainTileKey(BlockPos position) {
        return new TerrainTileKey(
                Math.floorDiv(position.getX(), TERRAIN_TILE_SIZE),
                Math.floorDiv(position.getY(), TERRAIN_TILE_SIZE),
                Math.floorDiv(position.getZ(), TERRAIN_TILE_SIZE)
        );
    }

    private void markTerrainTileAroundCell(BlockPos position) {
        int minTileX = Math.floorDiv(position.getX() - 1, TERRAIN_TILE_SIZE);
        int maxTileX = Math.floorDiv(position.getX() + 1, TERRAIN_TILE_SIZE);

        int minTileY = Math.floorDiv(position.getY() - 1, TERRAIN_TILE_SIZE);
        int maxTileY = Math.floorDiv(position.getY() + 1, TERRAIN_TILE_SIZE);

        int minTileZ = Math.floorDiv(position.getZ() - 1, TERRAIN_TILE_SIZE);
        int maxTileZ = Math.floorDiv(position.getZ() + 1, TERRAIN_TILE_SIZE);

        for (int x = minTileX; x <= maxTileX; x++)
            for (int y = minTileY; y <= maxTileY; y++)
                for (int z = minTileZ; z <= maxTileZ; z++)
                    dirtyTerrainTiles.add(new TerrainTileKey(x, y, z));
    }

    private void markTerrainTilesAroundChunk(ChunkPos chunkPos) {
        int minX = chunkPos.getMinBlockX() - TERRAIN_TILE_GUARD - 1;
        int maxX = chunkPos.getMaxBlockX() + TERRAIN_TILE_GUARD + 1;
        int minZ = chunkPos.getMinBlockZ() - TERRAIN_TILE_GUARD - 1;
        int maxZ = chunkPos.getMaxBlockZ() + TERRAIN_TILE_GUARD + 1;

        for (long key : terrainMeshReferenceCounts.keySet()) {
            BlockPos position = BlockPos.of(key);
            if (
                    position.getX() < minX ||
                    position.getX() > maxX ||
                    position.getZ() < minZ ||
                    position.getZ() > maxZ
            )
                continue;

            markTerrainTileAroundCell(position);
        }
    }

    private void replaceTerrainStatesForTile(
            TerrainTileKey tile,
            Map<Long, BlockState> nextStates
    ) {
        removeTerrainStatesForTile(tile);
        if (nextStates.isEmpty())
            return;

        Set<Long> keys = new HashSet<>(nextStates.keySet());

        terrainPatchStates.putAll(nextStates);
        terrainStateKeysByTile.put(tile, keys);
    }

    private void removeTerrainStatesForTile(TerrainTileKey tile) {
        Set<Long> keys = terrainStateKeysByTile.remove(tile);
        if (keys == null)
            return;
        for (long key : keys)
            terrainPatchStates.remove(key);
    }

    private void removeTerrainState(long key) {
        terrainPatchStates.remove(key);
        TerrainTileKey tile = terrainTileKey(BlockPos.of(key));
        Set<Long> keys = terrainStateKeysByTile.get(tile);
        if (keys == null)
            return;

        keys.remove(key);
        if (keys.isEmpty())
            terrainStateKeysByTile.remove(tile);
    }

    private void destroyTerrainPatch(TerrainTileKey tile) {
        TerrainPatch patch = terrainPatches.remove(tile);

        if (patch != null) {
            terrainPatchBodies.remove(PropPhysManager.BodyKey.of(patch.body()));

            b3DestroyBody(patch.body());
            b3DestroyMesh(patch.mesh());
        }

        removeTerrainStatesForTile(tile);
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

        List<TerrainQuad> quads = new ArrayList<>();
        for (TerrainBox box : boxes)
            for (int axis = 0; axis < 3; axis++) {
                appendTerrainFace(box, axis, false, faceIndex, quads);
                appendTerrainFace(box, axis, true, faceIndex, quads);
            }
        if (quads.isEmpty())
            return null;
        quads = mergeTerrainQuads(quads);

        List<PropPhysGeometry.FloatPoint> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        Map<TerrainPointKey, Integer> vertexIndices = new HashMap<>();

        for (TerrainQuad quad : quads)
            emitTerrainQuad(
                    quad.axis(),
                    quad.positive(),
                    quad.plane(),
                    quad.minU(),
                    quad.maxU(),
                    quad.minV(),
                    quad.maxV(),
                    vertices,
                    indices,
                    vertexIndices,
                    originX,
                    originY,
                    originZ
            );
        if (vertices.size() < 3 || indices.size() < 3)
            return null;

        b3MeshData mesh;
        try (
                b3Vec3 nativeVertices = new b3Vec3(vertices.size());
                IntPointer nativeIndices = new IntPointer(indices.size());
                b3MeshDef meshDefinition = new b3MeshDef()
        ) {
            for (int i = 0; i < vertices.size(); i++) {
                PropPhysGeometry.FloatPoint point = vertices.get(i);

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
            bodyDefinition.rotation(PropPhysGeometry.identityQuat());

            body = b3CreateBody(manager.world, bodyDefinition);

            b3ShapeDef shapeDefinition = PropPhysGeometry.makeShapeDefinition(
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
            List<TerrainQuad> quads
    ) {
        double plane = positive ? box.max(axis) : box.min(axis);

        int uAxis = axis == 0 ? 1 : 0;
        int vAxis = axis == 2 ? 1 : 2;

        double minU = box.min(uAxis);
        double maxU = box.max(uAxis);
        double minV = box.min(vAxis);
        double maxV = box.max(vAxis);

        List<TerrainBox> candidates = faceIndex.touching(box, axis, plane, positive);
        List<TerrainBox> overlappingNeighbors = null;

        for (TerrainBox neighbor : candidates) {
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
            if (overlappingNeighbors == null)
                overlappingNeighbors = new ArrayList<>();

            overlappingNeighbors.add(neighbor);
        }
        if (overlappingNeighbors == null) {
            quads.add(new TerrainQuad(axis, positive, plane, minU, maxU, minV, maxV));
            return;
        }

        int cutCapacity = 2 + overlappingNeighbors.size() * 2;
        double[] uCuts = new double[cutCapacity];
        double[] vCuts = new double[cutCapacity];

        uCuts[0] = minU;
        uCuts[1] = maxU;
        vCuts[0] = minV;
        vCuts[1] = maxV;

        int cutCount = 2;

        for (TerrainBox neighbor : overlappingNeighbors) {
            uCuts[cutCount] = Math.max(minU, neighbor.min(uAxis));
            uCuts[cutCount + 1] = Math.min(maxU, neighbor.max(uAxis));

            vCuts[cutCount] = Math.max(minV, neighbor.min(vAxis));
            vCuts[cutCount + 1] = Math.min(maxV, neighbor.max(vAxis));

            cutCount += 2;
        }

        int uCount = sortUnique(uCuts, cutCount);
        int vCount = sortUnique(vCuts, cutCount);

        int uSegments = uCount - 1;
        int vSegments = vCount - 1;
        if (uSegments <= 0 || vSegments <= 0)
            return;

        byte[] faceState = new byte[uSegments * vSegments];

        for (TerrainBox neighbor : overlappingNeighbors) {
            double overlapMinU = Math.max(minU, neighbor.min(uAxis));
            double overlapMaxU = Math.min(maxU, neighbor.max(uAxis));
            double overlapMinV = Math.max(minV, neighbor.min(vAxis));
            double overlapMaxV = Math.min(maxV, neighbor.max(vAxis));

            int u0 = Arrays.binarySearch(uCuts, 0, uCount, overlapMinU);
            int u1 = Arrays.binarySearch(uCuts, 0, uCount, overlapMaxU);
            int v0 = Arrays.binarySearch(vCuts, 0, vCount, overlapMinV);
            int v1 = Arrays.binarySearch(vCuts, 0, vCount, overlapMaxV);
            if (u0 < 0 || u1 < 0 || v0 < 0 || v1 < 0)
                continue;

            for (int ui = u0; ui < u1; ui++) {
                int rowStart = ui * vSegments;
                for (int vi = v0; vi < v1; vi++)
                    faceState[rowStart + vi] = 1;
            }
        }

        for (int ui = 0; ui < uSegments; ui++)
            for (int vi = 0; vi < vSegments; vi++) {
                int index = ui * vSegments + vi;
                if (faceState[index] != 0)
                    continue;

                int vEnd = vi + 1;
                while (vEnd < vSegments && faceState[ui * vSegments + vEnd] == 0)
                    vEnd++;

                int uEnd = ui + 1;
                while (uEnd < uSegments) {
                    boolean canExtend = true;
                    int nextRow = uEnd * vSegments;
                    for (int v = vi; v < vEnd; v++)
                        if (faceState[nextRow + v] != 0) {
                            canExtend = false;
                            break;
                        }
                    if (!canExtend)
                        break;
                    uEnd++;
                }
                for (int u = ui; u < uEnd; u++) {
                    int rowStart = u * vSegments;
                    for (int v = vi; v < vEnd; v++)
                        faceState[rowStart + v] = 2;
                }

                quads.add(new TerrainQuad(
                        axis,
                        positive,
                        plane,
                        uCuts[ui],
                        uCuts[uEnd],
                        vCuts[vi],
                        vCuts[vEnd]
                ));
            }
    }

    private static int sortUnique(double[] values, int length) {
        Arrays.sort(values, 0, length);
        int uniqueCount = 0;

        for (int i = 0; i < length; i++) {
            double value = values[i];
            if (
                    uniqueCount == 0 ||
                    Double.compare(values[uniqueCount - 1], value) != 0
            )
                values[uniqueCount++] = value;
        }

        return uniqueCount;
    }

    private static List<TerrainQuad> mergeTerrainQuads(List<TerrainQuad> source) {
        List<TerrainQuad> merged = source;

        for (int pass = 0; pass < 2; pass++) {
            int previousSize = merged.size();

            merged = mergeTerrainQuadsAlongU(merged);
            merged = mergeTerrainQuadsAlongV(merged);
            if (merged.size() == previousSize)
                break;
        }

        return merged;
    }

    private static List<TerrainQuad> mergeTerrainQuadsAlongU(List<TerrainQuad> source) {
        List<TerrainQuad> sorted = new ArrayList<>(source);

        sorted.sort((a, b) -> {
            int c = compareTerrainQuadPlane(a, b);
            if (c != 0) return c;
            c = Double.compare(a.minV, b.minV);
            if (c != 0) return c;
            c = Double.compare(a.maxV, b.maxV);
            if (c != 0) return c;
            c = Double.compare(a.minU, b.minU);
            if (c != 0) return c;
            return Double.compare(a.maxU, b.maxU);
        });

        List<TerrainQuad> merged = new ArrayList<>(sorted.size());

        for (TerrainQuad quad : sorted) {
            if (!merged.isEmpty()) {
                int lastIndex = merged.size() - 1;
                TerrainQuad previous = merged.get(lastIndex);

                if (
                        sameTerrainQuadPlane(previous, quad) &&
                        Double.compare(previous.minV, quad.minV) == 0 &&
                        Double.compare(previous.maxV, quad.maxV) == 0 &&
                        Double.compare(previous.maxU, quad.minU) == 0
                ) {
                    merged.set(lastIndex, new TerrainQuad(
                            previous.axis,
                            previous.positive,
                            previous.plane,
                            previous.minU,
                            quad.maxU,
                            previous.minV,
                            previous.maxV
                    ));
                    continue;
                }
            }

            merged.add(quad);
        }

        return merged;
    }

    private static List<TerrainQuad> mergeTerrainQuadsAlongV(List<TerrainQuad> source) {
        List<TerrainQuad> sorted = new ArrayList<>(source);

        sorted.sort((a, b) -> {
            int c = compareTerrainQuadPlane(a, b);
            if (c != 0) return c;
            c = Double.compare(a.minU, b.minU);
            if (c != 0) return c;
            c = Double.compare(a.maxU, b.maxU);
            if (c != 0) return c;
            c = Double.compare(a.minV, b.minV);
            if (c != 0) return c;
            return Double.compare(a.maxV, b.maxV);
        });

        List<TerrainQuad> merged = new ArrayList<>(sorted.size());

        for (TerrainQuad quad : sorted) {
            if (!merged.isEmpty()) {
                int lastIndex = merged.size() - 1;
                TerrainQuad previous = merged.get(lastIndex);

                if (
                        sameTerrainQuadPlane(previous, quad) &&
                        Double.compare(previous.minU, quad.minU) == 0 &&
                        Double.compare(previous.maxU, quad.maxU) == 0 &&
                        Double.compare(previous.maxV, quad.minV) == 0
                ) {
                    merged.set(lastIndex, new TerrainQuad(
                            previous.axis,
                            previous.positive,
                            previous.plane,
                            previous.minU,
                            previous.maxU,
                            previous.minV,
                            quad.maxV
                    ));
                    continue;
                }
            }

            merged.add(quad);
        }

        return merged;
    }

    private static int compareTerrainQuadPlane(TerrainQuad a, TerrainQuad b) {
        int c = Integer.compare(a.axis, b.axis);
        if (c != 0) return c;
        c = Boolean.compare(a.positive, b.positive);
        if (c != 0) return c;
        return Double.compare(a.plane, b.plane);
    }

    private static boolean sameTerrainQuadPlane(TerrainQuad a, TerrainQuad b) {
        return
                a.axis == b.axis &&
                a.positive == b.positive &&
                Double.compare(a.plane, b.plane) == 0;
    }

    private static void emitTerrainQuad(
            int axis,
            boolean positive,
            double plane,
            double u0,
            double u1,
            double v0,
            double v1,
            List<PropPhysGeometry.FloatPoint> vertices,
            List<Integer> indices,
            Map<TerrainPointKey, Integer> vertexIndices,
            int originX,
            int originY,
            int originZ
    ) {
        int first;
        int second;
        int third;
        int fourth;

        if (axis == 0) {
            // X-facing surface
            // u=Y, v=Z
            if (positive) {
                first = terrainVertexIndex(plane, u0, v0, vertices, vertexIndices, originX, originY, originZ);
                second = terrainVertexIndex(plane, u1, v0, vertices, vertexIndices, originX, originY, originZ);
                third = terrainVertexIndex(plane, u1, v1, vertices, vertexIndices, originX, originY, originZ);
                fourth = terrainVertexIndex(plane, u0, v1, vertices, vertexIndices, originX, originY, originZ);
            } else {
                first = terrainVertexIndex(plane, u0, v0, vertices, vertexIndices, originX, originY, originZ);
                second = terrainVertexIndex(plane, u0, v1, vertices, vertexIndices, originX, originY, originZ);
                third = terrainVertexIndex(plane, u1, v1, vertices, vertexIndices, originX, originY, originZ);
                fourth = terrainVertexIndex(plane, u1, v0, vertices, vertexIndices, originX, originY, originZ);
            }
        } else if (axis == 1) {
            // Y-facing surface
            // u=X, v=Z
            if (positive) {
                first = terrainVertexIndex(u0, plane, v0, vertices, vertexIndices, originX, originY, originZ);
                second = terrainVertexIndex(u0, plane, v1, vertices, vertexIndices, originX, originY, originZ);
                third = terrainVertexIndex(u1, plane, v1, vertices, vertexIndices, originX, originY, originZ);
                fourth = terrainVertexIndex(u1, plane, v0, vertices, vertexIndices, originX, originY, originZ);
            } else {
                first = terrainVertexIndex(u0, plane, v0, vertices, vertexIndices, originX, originY, originZ);
                second = terrainVertexIndex(u1, plane, v0, vertices, vertexIndices, originX, originY, originZ);
                third = terrainVertexIndex(u1, plane, v1, vertices, vertexIndices, originX, originY, originZ);
                fourth = terrainVertexIndex(u0, plane, v1, vertices, vertexIndices, originX, originY, originZ);
            }
        } else {
            // Z-facing surface
            // u=X, v=Y
            if (positive) {
                first = terrainVertexIndex(u0, v0, plane, vertices, vertexIndices, originX, originY, originZ);
                second = terrainVertexIndex(u1, v0, plane, vertices, vertexIndices, originX, originY, originZ);
                third = terrainVertexIndex(u1, v1, plane, vertices, vertexIndices, originX, originY, originZ);
                fourth = terrainVertexIndex(u0, v1, plane, vertices, vertexIndices, originX, originY, originZ);
            } else {
                first = terrainVertexIndex(u0, v0, plane, vertices, vertexIndices, originX, originY, originZ);
                second = terrainVertexIndex(u0, v1, plane, vertices, vertexIndices, originX, originY, originZ);
                third = terrainVertexIndex(u1, v1, plane, vertices, vertexIndices, originX, originY, originZ);
                fourth = terrainVertexIndex(u1, v0, plane, vertices, vertexIndices, originX, originY, originZ);
            }
        }

        indices.add(first);
        indices.add(second);
        indices.add(third);

        indices.add(first);
        indices.add(third);
        indices.add(fourth);
    }

    private static int terrainVertexIndex(
            double x,
            double y,
            double z,
            List<PropPhysGeometry.FloatPoint> vertices,
            Map<TerrainPointKey, Integer> vertexIndices,
            int originX,
            int originY,
            int originZ
    ) {
        float localX = (float) (x - originX);
        float localY = (float) (y - originY);
        float localZ = (float) (z - originZ);

        TerrainPointKey key = new TerrainPointKey(
                Float.floatToIntBits(localX),
                Float.floatToIntBits(localY),
                Float.floatToIntBits(localZ)
        );

        Integer existing = vertexIndices.get(key);
        if (existing != null)
            return existing;

        int index = vertices.size();
        vertices.add(new PropPhysGeometry.FloatPoint(localX, localY, localZ));
        vertexIndices.put(key, index);

        return index;
    }

    void destroyTerrainPatches() {
        for (TerrainPatch patch : terrainPatches.values()) {
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
        terrainMeshReferenceCounts.clear();

        terrainValidationQueue.clear();
        terrainPatchStates.clear();
        terrainStateKeysByTile.clear();
        dirtyTerrainTiles.clear();

        terrainValidationTicks = 0;
    }

    private static long terrainPlaneKey(double coordinate) {
        return Math.round(coordinate * 1_000_000.0);
    }


    private static final class TerrainFaceIndex {
        private final Map<TerrainFaceBucketKey, List<TerrainBox>>[] minPlanes =
                newPlaneMaps();

        private final Map<TerrainFaceBucketKey, List<TerrainBox>>[] maxPlanes =
                newPlaneMaps();

        @SuppressWarnings("unchecked")
        private static Map<TerrainFaceBucketKey, List<TerrainBox>>[] newPlaneMaps() {
            return new Map[] {
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>()
            };
        }

        private void add(TerrainBox box) {
            for (int axis = 0; axis < 3; axis++) {
                int uAxis = axis == 0 ? 1 : 0;
                int vAxis = axis == 2 ? 1 : 2;

                addFace(
                        minPlanes[axis],
                        box.min(axis),
                        box,
                        uAxis,
                        vAxis
                );

                addFace(
                        maxPlanes[axis],
                        box.max(axis),
                        box,
                        uAxis,
                        vAxis
                );
            }
        }

        private static void addFace(
                Map<TerrainFaceBucketKey, List<TerrainBox>> index,
                double plane,
                TerrainBox box,
                int uAxis,
                int vAxis
        ) {
            long planeKey = terrainPlaneKey(plane);

            int firstU = firstBucket(box.min(uAxis));
            int lastU = lastBucket(box.max(uAxis));
            int firstV = firstBucket(box.min(vAxis));
            int lastV = lastBucket(box.max(vAxis));

            for (int u = firstU; u <= lastU; u++) {
                for (int v = firstV; v <= lastV; v++) {
                    TerrainFaceBucketKey key =
                            new TerrainFaceBucketKey(planeKey, u, v);

                    index.computeIfAbsent(
                            key,
                            ignored -> new ArrayList<>()
                    ).add(box);
                }
            }
        }

        private List<TerrainBox> touching(
                TerrainBox box,
                int axis,
                double plane,
                boolean positive
        ) {
            Map<TerrainFaceBucketKey, List<TerrainBox>> index =
                    positive ? minPlanes[axis] : maxPlanes[axis];

            int uAxis = axis == 0 ? 1 : 0;
            int vAxis = axis == 2 ? 1 : 2;

            int firstU = firstBucket(box.min(uAxis));
            int lastU = lastBucket(box.max(uAxis));
            int firstV = firstBucket(box.min(vAxis));
            int lastV = lastBucket(box.max(vAxis));

            long planeKey = terrainPlaneKey(plane);

            if (firstU > lastU || firstV > lastV)
                return List.of();

            if (firstU == lastU && firstV == lastV) {
                return index.getOrDefault(
                        new TerrainFaceBucketKey(planeKey, firstU, firstV),
                        List.of()
                );
            }

            // A box can span multiple buckets. Deduplicate candidates that
            // were indexed in more than one bucket.
            Set<TerrainBox> seen = new HashSet<>();
            List<TerrainBox> result = new ArrayList<>();

            for (int u = firstU; u <= lastU; u++) {
                for (int v = firstV; v <= lastV; v++) {
                    List<TerrainBox> bucket = index.get(
                            new TerrainFaceBucketKey(planeKey, u, v)
                    );

                    if (bucket == null)
                        continue;

                    for (TerrainBox candidate : bucket)
                        if (seen.add(candidate))
                            result.add(candidate);
                }
            }

            return result.isEmpty() ? List.of() : result;
        }

        private static int firstBucket(double min) {
            return (int) Math.floor(min + TERRAIN_FACE_EPSILON);
        }

        private static int lastBucket(double max) {
            return (int) Math.floor(max - TERRAIN_FACE_EPSILON);
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
    private record TerrainQuad(
            int axis,
            boolean positive,
            double plane,
            double minU,
            double maxU,
            double minV,
            double maxV
    ) {}
    private record TerrainFaceBucketKey(long planeKey, int uCell, int vCell) {}
    private record TerrainTileKey(int x, int y, int z) {}
}
