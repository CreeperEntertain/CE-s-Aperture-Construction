package net.centertain.ceac.material.shapes.utility;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class MaterialShapeVoxelHelper6Way extends MaterialShapeVoxelHelper {
    private MaterialShapeVoxelHelper6Way() {}

    public static Map<Direction, VoxelShape> makeShapes(
            Supplier<BakedModel> source
    ) {
        return makeShapes(source, true);
    }

    public static Map<Direction, VoxelShape> makeShapes(
            Supplier<BakedModel> source,
            boolean greedyMeshing
    ) {
        return lazyMap(source, sourceModel -> makeShapes(sourceModel, greedyMeshing));
    }

    private static Map<Direction, VoxelShape> makeShapes(
            BakedModel source
    ) {
        return makeShapes(source, true);
    }

    private static Map<Direction, VoxelShape> makeShapes(
            BakedModel source,
            boolean greedyMeshing
    ) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        boolean[][][] canonical = voxelize(source, greedyMeshing);

        for (Direction facing : Direction.values())
            shapes.put(facing, compact(transformVoxels(canonical, facing)));

        return Map.copyOf(shapes);
    }

    private static boolean[][][] transformVoxels(
            boolean[][][] voxels,
            Direction facing
    ) {
        boolean[][][] transformed = new boolean[SIZE][SIZE][SIZE];

        Vec3 y = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );
        Vec3 x = switch (facing) {
            case UP, DOWN -> new Vec3(1.0, 0.0, 0.0);
            default -> new Vec3(0.0, 1.0, 0.0);
        };
        Vec3 z = x.cross(y).normalize();

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
}
