package net.centertain.ceac.block.custom.material_shapes;

import net.centertain.ceac.block.custom.MaterialShapeRotatable24Way;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class MaterialShapeHalfSlopeBottom extends MaterialShapeRotatable24Way {
    private static final Map<Direction, VoxelShape[]> SHAPES = makeShapes();

    public MaterialShapeHalfSlopeBottom(Properties properties) {
        super(properties);
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
        Vec3 forward = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );

        Vec3 x = forward.scale(-1.0);
        Vec3 y = switch (facing) {
            case UP -> new Vec3(0, 0, -1);
            case DOWN -> new Vec3(0, 0, 1);
            default -> new Vec3(0, 1, 0);
        };

        for (int i = 0; i < rotation; i++)
            y = y.cross(forward).add(forward.scale(y.dot(forward)));

        Vec3 z = y.cross(forward).normalize();

        VoxelShape shape = Shapes.empty();

        for (int xSlice = 0; xSlice < 16; xSlice++)
            shape = getShape(xSlice, x, y, z, shape);

        return shape;
    }

    private static @NotNull VoxelShape getShape(int xSlice, Vec3 x, Vec3 y, Vec3 z, VoxelShape shape) {
        double height = (xSlice + 1) / 2.0;

        Vec3[] corners = new Vec3[8];
        int index = 0;

        for (int dx = 0; dx <= 1; dx++)
            for (int dy = 0; dy <= 1; dy++)
                for (int dz = 0; dz <= 1; dz++) {
                    Vec3 local = new Vec3(
                            (xSlice + dx) / 16.0,
                            (dy * height) / 16.0,
                            dz
                    ).subtract(0.5, 0.5, 0.5);

                    corners[index++] = new Vec3(0.5, 0.5, 0.5)
                            .add(x.scale(local.x))
                            .add(y.scale(local.y))
                            .add(z.scale(local.z));
                }

        double minX = 1.0;
        double minY = 1.0;
        double minZ = 1.0;
        double maxX = 0.0;
        double maxY = 0.0;
        double maxZ = 0.0;

        for (Vec3 corner : corners) {
            minX = Math.min(minX, corner.x);
            minY = Math.min(minY, corner.y);
            minZ = Math.min(minZ, corner.z);
            maxX = Math.max(maxX, corner.x);
            maxY = Math.max(maxY, corner.y);
            maxZ = Math.max(maxZ, corner.z);
        }

        shape = Shapes.or(
                shape,
                Block.box(
                        minX * 16.0,
                        minY * 16.0,
                        minZ * 16.0,
                        maxX * 16.0,
                        maxY * 16.0,
                        maxZ * 16.0
                )
        );
        return shape;
    }
}
