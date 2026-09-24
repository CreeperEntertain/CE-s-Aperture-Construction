package net.centertain.ceac.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MaterialShapeRotatable24Way extends MaterialShape {
    public static DirectionProperty FACING = BlockStateProperties.FACING;
    public static IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

    protected MaterialShapeRotatable24Way(
            @Nullable String category,
            @Nullable Double price,
            Properties properties
    ) {
        super(
                category,
                price,
                properties
        );
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.WEST)
                .setValue(ROTATION, 0)
        );
    }

    protected MaterialShapeRotatable24Way(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.WEST)
                .setValue(ROTATION, 0)
        );
    }

    @Override protected void createBlockStateDefinition(
            @NotNull StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING, ROTATION);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(
            @NotNull BlockPlaceContext context
    ) {
        return defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(ROTATION, 0);
    }


    @Override
    public Vec3 transformPointToLocal(
            BlockState state,
            Vec3 point
    ) {
        Basis basis = getBasis(state);
        Vec3 local = point.subtract(0.5, 0.5, 0.5);

        return new Vec3(
                local.dot(basis.x),
                local.dot(basis.y),
                local.dot(basis.z)
        ).add(0.5, 0.5, 0.5);
    }

    @Override
    public Vec3 transformDirectionToLocal(
            BlockState state,
            Vec3 direction
    ) {
        Basis basis = getBasis(state);

        return new Vec3(
                direction.dot(basis.x),
                direction.dot(basis.y),
                direction.dot(basis.z)
        );
    }

    @Override
    public Vec3 transformPointToWorld(
            BlockState state,
            Vec3 point
    ) {
        Basis basis = getBasis(state);
        Vec3 local = point.subtract(0.5, 0.5, 0.5);

        return new Vec3(0.5, 0.5, 0.5)
                .add(basis.x.scale(local.x))
                .add(basis.y.scale(local.y))
                .add(basis.z.scale(local.z));
    }

    @Override
    public boolean rotateFromViewDirection(
            Level level,
            BlockPos pos,
            Vec3 viewDirection,
            boolean counterclockwise
    ) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != this)
            return false;
        if (viewDirection.lengthSqr() < 1.0e-8)
            return false;

        Vec3 axis = viewDirection.normalize();
        double angle = counterclockwise ? -Math.PI / 2.0 : Math.PI / 2.0;

        Basis current = getBasis(state);

        Vec3 targetX = rotateVector(current.x, axis, angle);
        Vec3 targetY = rotateVector(current.y, axis, angle);
        Vec3 targetZ = rotateVector(current.z, axis, angle);

        BlockState bestState = state;
        double bestScore = -Double.MAX_VALUE;

        for (Direction facing : Direction.values())
            for (int rotation = 0; rotation < 4; rotation++) {
                BlockState candidate = state.setValue(FACING, facing).setValue(ROTATION, rotation);
                Basis basis = getBasis(candidate);

                double score =
                        targetX.dot(basis.x) +
                        targetY.dot(basis.y) +
                        targetZ.dot(basis.z);

                if (score > bestScore) {
                    bestScore = score;
                    bestState = candidate;
                }
            }

        if (bestState == state)
            return false;

        level.setBlock(pos, bestState, 3);
        return true;
    }

    private static Vec3 rotateVector(Vec3 vector, Vec3 axis, double angle) {
        return vector
                .scale(Math.cos(angle))
                .add(axis.cross(vector).scale(Math.sin(angle)))
                .add(axis.scale(axis.dot(vector) * (1.0 - Math.cos(angle))));
    }

    protected Basis getBasis(BlockState state) {
        Direction facing = state.getValue(FACING);
        int rotation = state.getValue(ROTATION);

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

        return new Basis(x, y, z);
    }

    protected record Basis(
            Vec3 x,
            Vec3 y,
            Vec3 z
    ) {}
}
