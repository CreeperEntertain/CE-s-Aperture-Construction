package net.centertain.ceac.prop.framework.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.PropDefinition;
import net.centertain.ceac.prop.framework.PropGeometry;
import net.centertain.ceac.prop.framework.client.ClientProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PropCollisionClipGeometry {
    private static final double CLIP_EPSILON = 1.0e-4;
    private static final double SURFACE_CONTACT_EPSILON_SQUARED = 1.0e-8;
    private static final double RAY_EPSILON = 1.0e-7;
    private static final double MIN_VOLUME = 1.0e-12;

    private static final int MAX_PROP_CLIP_SUBDIVISION = 5;

    private static final Vec3 INSIDE_RAY_DIRECTION = new Vec3(
            1.0,
            0.3713906763541037,
            0.6947465906068658
    ).normalize();

    private PropCollisionClipGeometry() {}


    static List<PropGeometry.Triangle> build(
            PropGeometry previewGeometry,
            Matrix4f previewTransform,
            Level level,
            Vec3 camera,
            long renderTimeNanos
    ) {
        List<PropGeometry.Triangle> previewTriangles = transformTriangles(previewGeometry.getTriangles(), previewTransform);
        List<PropGeometry.Triangle> result = new ArrayList<>();

        Map<BlockPos, List<AABB>> collisionBoxes = new HashMap<>();

        for (PropGeometry.Triangle triangle : previewTriangles) {
            Vec3 a = triangle.a();
            Vec3 b = triangle.b();
            Vec3 c = triangle.c();

            int minX = (int) Math.floor(Math.min(a.x, Math.min(b.x, c.x)));
            int minY = (int) Math.floor(Math.min(a.y, Math.min(b.y, c.y)));
            int minZ = (int) Math.floor(Math.min(a.z, Math.min(b.z, c.z)));
            int maxX = (int) Math.floor(Math.max(a.x, Math.max(b.x, c.x)));
            int maxY = (int) Math.floor(Math.max(a.y, Math.max(b.y, c.y)));
            int maxZ = (int) Math.floor(Math.max(a.z, Math.max(b.z, c.z)));

            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockPos position = new BlockPos(x, y, z);
                        List<AABB> boxes = collisionBoxes.computeIfAbsent(
                                position, key -> getCollisionBoxes(level, key)
                        );
                        for (AABB box : boxes) {
                            List<Vec3> polygon = clipTriangle(a, b, c, box);
                            appendPolygonTriangles(polygon, result);
                        }
                    }
        }

        appendPlacedPropClips(previewTriangles, level, camera, renderTimeNanos, result);

        return List.copyOf(result);
    }


    private static void appendPlacedPropClips(
            List<PropGeometry.Triangle> previewTriangles,
            Level level,
            Vec3 camera,
            long renderTimeNanos,
            List<PropGeometry.Triangle> output
    ) {
        if (!level.isClientSide || previewTriangles.isEmpty())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;

        AABB previewBounds = boundsOf(previewTriangles);

        BlockPos cameraBlock = BlockPos.containing(camera);
        int centerChunkX = cameraBlock.getX() >> 4;
        int centerChunkZ = cameraBlock.getZ() >> 4;
        int chunkRadius = minecraft.options.renderDistance().get() + 1;

        for (int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++)
            for (int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++)
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ)) {
                    PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);
                    if (definition == null)
                        continue;

                    Matrix4f propTransform = new Matrix4f();
                    ClientProps.copyRenderTransformTo(prop, propTransform, renderTimeNanos);
                    if (Math.abs(propTransform.determinant()) < MIN_VOLUME)
                        continue;

                    BakedModel model = minecraft.getModelManager().getModel(definition.getModelId());
                    List<PropGeometry.Triangle> localTriangles = getModelTriangles(
                            model, prop.getId().getMostSignificantBits()
                    );

                    PropMesh propMesh = createPropMesh(
                            localTriangles,
                            propTransform
                    );
                    if (propMesh == null || !previewBounds.intersects(propMesh.bounds))
                        continue;

                    for (PropGeometry.Triangle previewTriangle : previewTriangles) {
                        if (!triangleBounds(previewTriangle).intersects(propMesh.bounds))
                            continue;

                        List<Vec3> polygon = clipTriangle(
                                previewTriangle.a(),
                                previewTriangle.b(),
                                previewTriangle.c(),
                                propMesh.bounds
                        );
                        if (polygon.size() < 3)
                            continue;

                        Vec3 first = polygon.get(0);

                        for (int i = 1; i < polygon.size() - 1; i++) {
                            PropGeometry.Triangle candidate = new PropGeometry.Triangle(
                                    first, polygon.get(i), polygon.get(i + 1)
                            );
                            appendInteriorPortion(candidate, propMesh, 0, output);
                        }
                    }
                }
    }


    private static List<PropGeometry.Triangle> getModelTriangles(
            BakedModel model,
            long seed
    ) {
        List<PropGeometry.Triangle> triangles = new ArrayList<>();

        var state = Blocks.AIR.defaultBlockState();
        RandomSource random = RandomSource.create();

        for (Direction face : Direction.values()) {
            random.setSeed(seed);
            appendQuads(model.getQuads(
                    state,
                    face,
                    random,
                    ModelData.EMPTY,
                    RenderType.solid()
            ), triangles);
        }

        random.setSeed(seed);

        appendQuads(model.getQuads(
                state,
                null,
                random,
                ModelData.EMPTY,
                RenderType.solid()
        ), triangles);

        return List.copyOf(triangles);
    }


    private static void appendQuads(
            List<BakedQuad> quads,
            List<PropGeometry.Triangle> output
    ) {
        int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
        int positionOffset = DefaultVertexFormat.BLOCK.getOffset(0) / Integer.BYTES;

        for (BakedQuad quad : quads) {
            int[] data = quad.getVertices();
            Vec3[] vertices = new Vec3[4];
            for (int i = 0; i < 4; i++) {
                int index = i * stride + positionOffset;
                vertices[i] = new Vec3(
                        Float.intBitsToFloat(data[index]),
                        Float.intBitsToFloat(data[index + 1]),
                        Float.intBitsToFloat(data[index + 2])
                );
            }
            output.add(new PropGeometry.Triangle(
                    vertices[0],
                    vertices[1],
                    vertices[2]
            ));
            output.add(new PropGeometry.Triangle(
                    vertices[0],
                    vertices[2],
                    vertices[3]
            ));
        }
    }


    private static PropMesh createPropMesh(
            List<PropGeometry.Triangle> localTriangles,
            Matrix4f transform
    ) {
        if (localTriangles.isEmpty())
            return null;

        double signedVolume = 0.0;
        for (PropGeometry.Triangle triangle : localTriangles)
            signedVolume += triangle.a().dot(triangle.b().cross(triangle.c())) / 6.0;
        if (Math.abs(signedVolume) <= MIN_VOLUME)
            return null;

        Matrix4f inverseTransform = new Matrix4f(transform);
        if (Math.abs(inverseTransform.determinant()) < MIN_VOLUME)
            return null;

        inverseTransform.invert();

        List<PropGeometry.Triangle> worldTriangles = transformTriangles(localTriangles, transform);

        return new PropMesh(
                localTriangles,
                worldTriangles,
                inverseTransform,
                boundsOf(worldTriangles)
        );
    }


    private static void appendInteriorPortion(
            PropGeometry.Triangle triangle,
            PropMesh mesh,
            int depth,
            List<PropGeometry.Triangle> output
    ) {
        Vec3 a = triangle.a();
        Vec3 b = triangle.b();
        Vec3 c = triangle.c();

        double maxEdgeSquared = Math.max(a.distanceToSqr(b), Math.max(b.distanceToSqr(c), c.distanceToSqr(a)));
        if (maxEdgeSquared <= 1.0e-16)
            return;

        Vec3 center = a.add(b).add(c).scale(1.0 / 3.0);
        if (
                depth == 0 &&
                isWithinSurfaceDistance(
                        a,
                        mesh.worldTriangles,
                        SURFACE_CONTACT_EPSILON_SQUARED
                ) && isWithinSurfaceDistance(
                        b,
                        mesh.worldTriangles,
                        SURFACE_CONTACT_EPSILON_SQUARED
                ) && isWithinSurfaceDistance(
                        c,
                        mesh.worldTriangles,
                        SURFACE_CONTACT_EPSILON_SQUARED
                ) && isWithinSurfaceDistance(
                        center,
                        mesh.worldTriangles,
                        SURFACE_CONTACT_EPSILON_SQUARED
                )
        )
            return;

        boolean nearSurface =
                isWithinSurfaceDistance(a, mesh.worldTriangles, maxEdgeSquared) ||
                isWithinSurfaceDistance(b, mesh.worldTriangles, maxEdgeSquared) ||
                isWithinSurfaceDistance(c, mesh.worldTriangles, maxEdgeSquared);

        if (!nearSurface) {
            if (containsWorldPoint(mesh, center))
                output.add(triangle);
            return;
        }
        if (depth >= MAX_PROP_CLIP_SUBDIVISION) {
            if (containsWorldPoint(mesh, center))
                output.add(triangle);
            return;
        }

        Vec3 ab = a.lerp(b, 0.5);
        Vec3 bc = b.lerp(c, 0.5);
        Vec3 ca = c.lerp(a, 0.5);

        appendInteriorPortion(
                new PropGeometry.Triangle(a, ab, ca),
                mesh, depth + 1, output
        );
        appendInteriorPortion(
                new PropGeometry.Triangle(ab, b, bc),
                mesh, depth + 1, output
        );
        appendInteriorPortion(
                new PropGeometry.Triangle(ca, bc, c),
                mesh, depth + 1, output
        );
        appendInteriorPortion(
                new PropGeometry.Triangle(ab, bc, ca),
                mesh, depth + 1, output
        );
    }


    private static boolean containsWorldPoint(
            PropMesh mesh,
            Vec3 worldPoint
    ) {
        Vec3 localPoint = transformPoint(worldPoint, mesh.inverseTransform);
        return containsPoint(localPoint, mesh.localTriangles);
    }


    private static boolean containsPoint(
            Vec3 point,
            List<PropGeometry.Triangle> triangles
    ) {
        List<Double> hitDistances = new ArrayList<>();
        for (PropGeometry.Triangle triangle : triangles) {
            double distance = rayTriangleDistance(
                    point,
                    INSIDE_RAY_DIRECTION,
                    triangle.a(),
                    triangle.b(),
                    triangle.c()
            );
            if (Double.isFinite(distance))
                hitDistances.add(distance);
        }

        hitDistances.sort(Double::compare);

        int uniqueHits = 0;
        double previousDistance = Double.NEGATIVE_INFINITY;

        for (double distance : hitDistances) {
            double tolerance = Math.max(1.0e-6, Math.abs(distance) * 1.0e-6);
            if (uniqueHits == 0 || Math.abs(distance - previousDistance) > tolerance) {
                uniqueHits++;
                previousDistance = distance;
            }
        }

        return (uniqueHits & 1) != 0;
    }


    private static double rayTriangleDistance(
            Vec3 origin,
            Vec3 direction,
            Vec3 a,
            Vec3 b,
            Vec3 c
    ) {
        Vec3 edge1 = b.subtract(a);
        Vec3 edge2 = c.subtract(a);
        Vec3 p = direction.cross(edge2);

        double determinant = edge1.dot(p);
        if (Math.abs(determinant) < RAY_EPSILON)
            return Double.NaN;

        double inverseDeterminant = 1.0 / determinant;
        Vec3 fromA = origin.subtract(a);

        double u = fromA.dot(p) * inverseDeterminant;
        if (u < -RAY_EPSILON || u > 1.0 + RAY_EPSILON)
            return Double.NaN;

        Vec3 q = fromA.cross(edge1);
        double v = direction.dot(q) * inverseDeterminant;

        if (v < -RAY_EPSILON || u + v > 1.0 + RAY_EPSILON)
            return Double.NaN;

        double distance = edge2.dot(q) * inverseDeterminant;

        return distance > RAY_EPSILON ? distance : Double.NaN;
    }


    private static boolean isWithinSurfaceDistance(
            Vec3 point,
            List<PropGeometry.Triangle> triangles,
            double maxDistanceSquared
    ) {
        for (PropGeometry.Triangle triangle : triangles)
            if (pointTriangleDistanceSquared(point, triangle.a(), triangle.b(), triangle.c()) <= maxDistanceSquared)
                return true;
        return false;
    }


    private static double pointTriangleDistanceSquared(
            Vec3 point,
            Vec3 a,
            Vec3 b,
            Vec3 c
    ) {
        Vec3 ab = b.subtract(a);
        Vec3 ac = c.subtract(a);
        Vec3 normal = ab.cross(ac);

        double normalLengthSquared = normal.lengthSqr();

        if (normalLengthSquared > 1.0e-20) {
            Vec3 projected = point.subtract(normal.scale(point.subtract(a).dot(normal) / normalLengthSquared));
            Vec3 ap = projected.subtract(a);

            double d00 = ab.dot(ab);
            double d01 = ab.dot(ac);
            double d11 = ac.dot(ac);
            double d20 = ap.dot(ab);
            double d21 = ap.dot(ac);

            double denominator = d00 * d11 - d01 * d01;

            if (Math.abs(denominator) > 1.0e-20) {
                double u = (d11 * d20 - d01 * d21) / denominator;
                double v = (d00 * d21 - d01 * d20) / denominator;
                if (u >= 0.0 && v >= 0.0 && u + v <= 1.0)
                    return point.distanceToSqr(projected);
            }
        }

        return Math.min(
                pointSegmentDistanceSquared(point, a, b),
                Math.min(pointSegmentDistanceSquared(point, b, c), pointSegmentDistanceSquared(point, c, a))
        );
    }


    private static double pointSegmentDistanceSquared(
            Vec3 point,
            Vec3 a,
            Vec3 b
    ) {
        Vec3 ab = b.subtract(a);
        double lengthSquared = ab.lengthSqr();
        if (lengthSquared <= 1.0e-20)
            return point.distanceToSqr(a);

        double t = point.subtract(a).dot(ab) / lengthSquared;
        t = Math.max(0.0, Math.min(1.0, t));

        return point.distanceToSqr(a.add(ab.scale(t)));
    }


    private static List<PropGeometry.Triangle> transformTriangles(
            List<PropGeometry.Triangle> triangles,
            Matrix4f transform
    ) {
        List<PropGeometry.Triangle> result = new ArrayList<>(triangles.size());
        for (PropGeometry.Triangle triangle : triangles)
            result.add(new PropGeometry.Triangle(
                    transformPoint(triangle.a(), transform),
                    transformPoint(triangle.b(), transform),
                    transformPoint(triangle.c(), transform),
                    triangle.smoothEdgeMask()
            ));
        return List.copyOf(result);
    }


    private static AABB boundsOf(
            List<PropGeometry.Triangle> triangles
    ) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (PropGeometry.Triangle triangle : triangles)
            for (Vec3 point : List.of(triangle.a(), triangle.b(), triangle.c())) {
                minX = Math.min(minX, point.x);
                minY = Math.min(minY, point.y);
                minZ = Math.min(minZ, point.z);

                maxX = Math.max(maxX, point.x);
                maxY = Math.max(maxY, point.y);
                maxZ = Math.max(maxZ, point.z);
            }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }


    private static AABB triangleBounds(
            PropGeometry.Triangle triangle
    ) {
        Vec3 a = triangle.a();
        Vec3 b = triangle.b();
        Vec3 c = triangle.c();

        return new AABB(
                Math.min(a.x, Math.min(b.x, c.x)),
                Math.min(a.y, Math.min(b.y, c.y)),
                Math.min(a.z, Math.min(b.z, c.z)),
                Math.max(a.x, Math.max(b.x, c.x)),
                Math.max(a.y, Math.max(b.y, c.y)),
                Math.max(a.z, Math.max(b.z, c.z))
        );
    }


    private static List<AABB> getCollisionBoxes(Level level, BlockPos position) {
        var state = level.getBlockState(position);
        var shape = state.getCollisionShape(level, position, net.minecraft.world.phys.shapes.CollisionContext.empty());
        if (shape.isEmpty())
            return List.of();

        List<AABB> result = new ArrayList<>();
        for (AABB localBox : shape.toAabbs()) {
            double minX = position.getX() + localBox.minX + CLIP_EPSILON;
            double minY = position.getY() + localBox.minY + CLIP_EPSILON;
            double minZ = position.getZ() + localBox.minZ + CLIP_EPSILON;
            double maxX = position.getX() + localBox.maxX - CLIP_EPSILON;
            double maxY = position.getY() + localBox.maxY - CLIP_EPSILON;
            double maxZ = position.getZ() + localBox.maxZ - CLIP_EPSILON;
            if (minX >= maxX || minY >= maxY || minZ >= maxZ)
                continue;

            result.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
        }

        return List.copyOf(result);
    }

    private static List<Vec3> clipTriangle(Vec3 a, Vec3 b, Vec3 c, AABB box) {
        List<Vec3> polygon = new ArrayList<>(List.of(a, b, c));

        polygon = clipPolygon(polygon, 0, box.minX, true);
        polygon = clipPolygon(polygon, 0, box.maxX, false);
        polygon = clipPolygon(polygon, 1, box.minY, true);
        polygon = clipPolygon(polygon, 1, box.maxY, false);
        polygon = clipPolygon(polygon, 2, box.minZ, true);
        polygon = clipPolygon(polygon, 2, box.maxZ, false);

        return polygon;
    }

    private static List<Vec3> clipPolygon(
            List<Vec3> polygon,
            int axis,
            double boundary,
            boolean keepGreater
    ) {
        if (polygon.isEmpty())
            return polygon;

        List<Vec3> result = new ArrayList<>();
        Vec3 previous = polygon.get(polygon.size() - 1);
        double previousCoordinate = coordinate(previous, axis);
        boolean previousInside = keepGreater
                ? previousCoordinate >= boundary
                : previousCoordinate <= boundary;

        for (Vec3 current : polygon) {
            double currentCoordinate = coordinate(current, axis);
            boolean currentInside = keepGreater
                    ? currentCoordinate >= boundary
                    : currentCoordinate <= boundary;
            if (currentInside != previousInside) {
                double denominator = currentCoordinate - previousCoordinate;
                if (Math.abs(denominator) > 1.0e-12) {
                    double amount = (boundary - previousCoordinate) / denominator;
                    result.add(previous.lerp(current, amount));
                }
            }
            if (currentInside)
                result.add(current);

            previous = current;
            previousCoordinate = currentCoordinate;
            previousInside = currentInside;
        }

        return result;
    }


    private static void appendPolygonTriangles(
            List<Vec3> polygon,
            List<PropGeometry.Triangle> output
    ) {
        if (polygon.size() < 3)
            return;

        Vec3 first = polygon.get(0);

        for (int i = 1; i < polygon.size() - 1; i++) {
            Vec3 second = polygon.get(i);
            Vec3 third = polygon.get(i + 1);
            if (second.subtract(first).cross(third.subtract(first)).lengthSqr() <= 1.0e-12)
                continue;

            output.add(new PropGeometry.Triangle(first, second, third));
        }
    }


    private static double coordinate(Vec3 point, int axis) {
        return switch (axis) {
            case 0 -> point.x;
            case 1 -> point.y;
            default -> point.z;
        };
    }

    private static Vec3 transformPoint(Vec3 point, Matrix4f transform) {
        Vector3f result = new Vector3f(
                (float) point.x,
                (float) point.y,
                (float) point.z
        );
        result.mulPosition(transform);
        return new Vec3(result.x, result.y, result.z);
    }


    private record PropMesh(
            List<PropGeometry.Triangle> localTriangles,
            List<PropGeometry.Triangle> worldTriangles,
            Matrix4f inverseTransform,
            AABB bounds
    ) {}
}