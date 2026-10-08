package net.centertain.ceac.material.shapes.utility;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.function.Supplier;

public final class MaterialShapeVoxelHelper24Way extends MaterialShapeVoxelHelper {
    private MaterialShapeVoxelHelper24Way() {}

    public static Map<Direction, VoxelShape[]> makeShapes(
            Supplier<BakedModel> source
    ) {
        return makeShapes(source, true);
    }

    public static Map<Direction, VoxelShape[]> makeShapes(
            Supplier<BakedModel> source,
            boolean greedyMeshing
    ) {
        return lazyMap(source, sourceModel -> makeShapes(sourceModel, greedyMeshing));
    }

    private static Map<Direction, VoxelShape[]> makeShapes(
            BakedModel source
    ) {
        return makeShapes(source, true);
    }

    private static Map<Direction, VoxelShape[]> makeShapes(
            BakedModel source,
            boolean greedyMeshing
    ) {
        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);
        boolean[][][] canonical = voxelize(source, greedyMeshing);

        for (Direction facing : Direction.values()) {
            VoxelShape[] rotations = new VoxelShape[4];
            for (int rotation = 0; rotation < 4; rotation++)
                rotations[rotation] = compact(transformVoxels(
                        canonical,
                        facing,
                        rotation
                ));
            shapes.put(facing, rotations);
        }

        return Map.copyOf(shapes);
    }

    private static boolean[][][] transformVoxels(
            boolean[][][] voxels,
            Direction facing,
            int rotation
    ) {
        boolean[][][] transformed = new boolean[SIZE][SIZE][SIZE];

        Vec3 forward = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );
        Vec3 x = forward.scale(-1.0);
        Vec3 y = switch (facing) {
            case UP -> new Vec3(0.0, 0.0, -1.0);
            case DOWN -> new Vec3(0.0, 0.0, 1.0);
            default -> new Vec3(0.0, 1.0, 0.0);
        };

        for (int i = 0; i < rotation; i++)
            y = y.cross(forward).add(forward.scale(y.dot(forward)));

        Vec3 z = y.cross(forward).normalize();

        int xx = (int) Math.round(x.x);
        int xy = (int) Math.round(x.y);
        int xz = (int) Math.round(x.z);

        int yx = (int) Math.round(y.x);
        int yy = (int) Math.round(y.y);
        int yz = (int) Math.round(y.z);

        int zx = (int) Math.round(z.x);
        int zy = (int) Math.round(z.y);
        int zz = (int) Math.round(z.z);

        for (int localX = 0; localX < SIZE; localX++)
            for (int localY = 0; localY < SIZE; localY++)
                for (int localZ = 0; localZ < SIZE; localZ++) {
                    if (!voxels[localX][localY][localZ])
                        continue;

                    int centerX = localX * 2 - (SIZE - 1);
                    int centerY = localY * 2 - (SIZE - 1);
                    int centerZ = localZ * 2 - (SIZE - 1);

                    int worldX = xx * centerX + yx * centerY + zx * centerZ;
                    int worldY = xy * centerX + yy * centerY + zy * centerZ;
                    int worldZ = xz * centerX + yz * centerY + zz * centerZ;

                    int targetX = (worldX + SIZE - 1) / 2;
                    int targetY = (worldY + SIZE - 1) / 2;
                    int targetZ = (worldZ + SIZE - 1) / 2;

                    transformed[targetX][targetY][targetZ] = true;
                }

        return transformed;
    }


    public static Map<Direction, VoxelShape[]> makeCuboidShapes(Supplier<BakedModel> source) {
        return lazyMap(source, MaterialShapeVoxelHelper24Way::makeCuboidShapes);
    }

    private static Map<Direction, VoxelShape[]> makeCuboidShapes(BakedModel source) {
        VoxelShape canonical = fromBounds(source);
        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);

        for (Direction facing : Direction.values()) {
            VoxelShape[] rotations = new VoxelShape[4];
            for (int rotation = 0; rotation < 4; rotation++)
                rotations[rotation] = transformCuboid(canonical, facing, rotation);
            shapes.put(facing, rotations);
        }

        return Map.copyOf(shapes);
    }

    private static VoxelShape transformCuboid(
            VoxelShape shape,
            Direction facing,
            int rotation
    ) {
        Vec3 forward = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );

        Vec3 x = forward.scale(-1.0);

        Vec3 y = switch (facing) {
            case UP -> new Vec3(0.0, 0.0, -1.0);
            case DOWN -> new Vec3(0.0, 0.0, 1.0);
            default -> new Vec3(0.0, 1.0, 0.0);
        };

        for (int i = 0; i < rotation; i++)
            y = y.cross(forward).add(forward.scale(y.dot(forward)));

        Vec3 z = y.cross(forward).normalize();

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (AABB bounds : shape.toAabbs()) {
            double[] xs = {bounds.minX, bounds.maxX};
            double[] ys = {bounds.minY, bounds.maxY};
            double[] zs = {bounds.minZ, bounds.maxZ};

            for (double localX : xs)
                for (double localY : ys)
                    for (double localZ : zs) {
                        Vec3 local = new Vec3(
                                localX - 0.5,
                                localY - 0.5,
                                localZ - 0.5
                        );

                        Vec3 world = new Vec3(0.5, 0.5, 0.5)
                                .add(x.scale(local.x))
                                .add(y.scale(local.y))
                                .add(z.scale(local.z));

                        minX = Math.min(minX, world.x);
                        minY = Math.min(minY, world.y);
                        minZ = Math.min(minZ, world.z);
                        maxX = Math.max(maxX, world.x);
                        maxY = Math.max(maxY, world.y);
                        maxZ = Math.max(maxZ, world.z);
                    }
        }

        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
