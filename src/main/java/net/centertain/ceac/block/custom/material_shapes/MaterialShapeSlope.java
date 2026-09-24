package net.centertain.ceac.block.custom.material_shapes;

import net.centertain.ceac.block.custom.MaterialShapeRotatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class MaterialShapeSlope extends MaterialShapeRotatable {
    private static final Map<Direction, VoxelShape[]> SHAPES = makeShapes();

    public MaterialShapeSlope(Properties properties) {
        super(
                null,
                null,
                properties
        );
    }

    @SuppressWarnings("deprecation") // Literally what the docs told me to use. Why would you deprecate something that's
    @Override                        // the only real way to do the thing? No, really. Enlighten me.
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(ROTATION)];
    }

    private static Map<Direction, VoxelShape[]> makeShapes() {
        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);

        for (Direction facing : Direction.values()) {
            VoxelShape[] rotations = new VoxelShape[4];

            for (int rotation = 0; rotation < 4; rotation++)
                rotations[rotation] = makeShape(facing, rotation);

            shapes.put(facing, rotations);
        }

        return Map.copyOf(shapes);
    }

    private static VoxelShape makeShape(
            Direction facing,
            int rotation
    ) {
        int[] forward = {
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        };
        int[] xAxis = {
                -forward[0],
                -forward[1],
                -forward[2]
        };
        int[] up = switch (facing) {
            case UP -> new int[]{0, 0, -1};
            case DOWN -> new int[]{0, 0, 1};
            default -> new int[]{0, 1, 0};
        };

        for (int i = 0; i < rotation; i++)
            up = rotate90(up, forward);

        int[] zAxis = cross(up, forward);

        VoxelShape shape = Shapes.empty();

        for (int x = 0; x < 16; x++) {
            int minX = 16;
            int minY = 16;
            int minZ = 16;
            int maxX = 0;
            int maxY = 0;
            int maxZ = 0;

            int height = x + 1;

            Result result = getResult(x, height, xAxis, up, zAxis, minX, minY, minZ, maxX, maxY, maxZ);

            shape = Shapes.or(shape, Block.box(
                    result.minX,
                    result.minY,
                    result.minZ,
                    result.maxX,
                    result.maxY,
                    result.maxZ
            ));
        }

        return shape;
    }

    private static @NotNull Result getResult(
            int x, int height, int[] xAxis, int[] up, int[] zAxis,
            int minX, int minY, int minZ, int maxX, int maxY, int maxZ
    ) {
        for (int dx = 0; dx <= 1; dx++)
            for (int dy = 0; dy <= 1; dy++)
                for (int dz = 0; dz <= 1; dz++) {
                    int localX = x + dx;
                    int localY = dy * height;
                    int localZ = dz * 16;

                    int relativeX = localX - 8;
                    int relativeY = localY - 8;
                    int relativeZ = localZ - 8;

                    int worldX = 8
                            + xAxis[0] * relativeX
                            + up[0] * relativeY
                            + zAxis[0] * relativeZ;
                    int worldY = 8
                            + xAxis[1] * relativeX
                            + up[1] * relativeY
                            + zAxis[1] * relativeZ;
                    int worldZ = 8
                            + xAxis[2] * relativeX
                            + up[2] * relativeY
                            + zAxis[2] * relativeZ;

                    minX = Math.min(minX, worldX);
                    minY = Math.min(minY, worldY);
                    minZ = Math.min(minZ, worldZ);
                    maxX = Math.max(maxX, worldX);
                    maxY = Math.max(maxY, worldY);
                    maxZ = Math.max(maxZ, worldZ);
                }

        return new Result(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private record Result(
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ
    ) {}

    private static int[] rotate90(
            int[] vector,
            int[] axis
    ) {
        int[] cross = cross(axis, vector);
        int dot = dot(vector, axis);

        return new int[]{
                cross[0] + axis[0] * dot,
                cross[1] + axis[1] * dot,
                cross[2] + axis[2] * dot
        };
    }

    private static int[] cross(
            int[] a,
            int[] b
    ) {
        return new int[]{
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    private static int dot(
            int[] a,
            int[] b
    ) {
        return
                a[0] * b[0] +
                a[1] * b[1] +
                a[2] * b[2];
    }
}
