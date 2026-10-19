package net.centertain.ceac.prop.framework.client.render;

import net.centertain.ceac.prop.framework.PropGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PropCollisionClipGeometry {
    private static final double CLIP_EPSILON = 1.0e-4;

    private PropCollisionClipGeometry() {}


    static List<PropGeometry.Triangle> build(PropGeometry geometry, Matrix4f transform, Level level) {
        Map<BlockPos, List<AABB>> collisionBoxes = new HashMap<>();
        List<PropGeometry.Triangle> result = new ArrayList<>();

        for (PropGeometry.Triangle triangle : geometry.getTriangles()) {
            Vec3 a = transformPoint(triangle.a(), transform);
            Vec3 b = transformPoint(triangle.b(), transform);
            Vec3 c = transformPoint(triangle.c(), transform);

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
                            if (polygon.size() < 3)
                                continue;

                            Vec3 first = polygon.get(0);

                            for (int i = 1; i < polygon.size() - 1; i++) {
                                Vec3 second = polygon.get(i);
                                Vec3 third = polygon.get(i + 1);
                                if (second.subtract(first).cross(third.subtract(first)).lengthSqr() <= 1.0e-12)
                                    continue;

                                result.add(new PropGeometry.Triangle(first, second, third));
                            }
                        }
                    }
        }

        return List.copyOf(result);
    }

    private static List<AABB> getCollisionBoxes(Level level, BlockPos position) {
        BlockState state = level.getBlockState(position);
        VoxelShape shape = state.getCollisionShape(level, position, CollisionContext.empty());
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
                double amount = (boundary - previousCoordinate) / (currentCoordinate - previousCoordinate);
                result.add(previous.lerp(current, amount));
            }
            if (currentInside)
                result.add(current);

            previous = current;
            previousCoordinate = currentCoordinate;
            previousInside = currentInside;
        }

        return result;
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
}
