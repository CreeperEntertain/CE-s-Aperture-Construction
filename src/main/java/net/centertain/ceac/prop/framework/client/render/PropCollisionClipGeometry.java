package net.centertain.ceac.prop.framework.client.render;

import net.centertain.ceac.prop.framework.PropGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.*;

final class PropCollisionClipGeometry {
    private static final double CLIP_EPSILON = 1.0e-4;

    private PropCollisionClipGeometry() {}


    static List<PropGeometry.Triangle> build(
            PropGeometry previewGeometry,
            Matrix4f previewTransform,
            Level level
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

        return List.copyOf(result);
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
        return new Vec3(
                transform.m00() * point.x + transform.m10() * point.y + transform.m20() * point.z + transform.m30(),
                transform.m01() * point.x + transform.m11() * point.y + transform.m21() * point.z + transform.m31(),
                transform.m02() * point.x + transform.m12() * point.y + transform.m22() * point.z + transform.m32()
        );
    }
}