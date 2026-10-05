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

public abstract class MaterialShapeRotatable6Way extends MaterialShape implements MaterialShapeRotatable {
    public static DirectionProperty FACING = BlockStateProperties.FACING;

    protected MaterialShapeRotatable6Way(
            Properties properties,
            @Nullable String category,
            String subcategory,
            @Nullable Double price
    ) {
        super(
                properties,
                category,
                subcategory,
                price
        );
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.WEST)
        );
    }

    protected MaterialShapeRotatable6Way(
            Properties properties,
            String subcategory
    ) {
        super(properties, subcategory);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.WEST)
        );
    }

    public @NotNull DirectionProperty getFacing() {
        return FACING;
    }
    public @Nullable Direction.Axis getAxis() {
        return null;
    }
    public @Nullable IntegerProperty getRotation() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(
            @NotNull StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(
            @NotNull BlockPlaceContext context
    ) {
        return defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite());
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
    public Vec3 transformDirectionToWorld(
            BlockState state,
            Vec3 direction
    ) {
        Basis basis = getBasis(state);

        return basis.x.scale(direction.x)
                .add(basis.y.scale(direction.y))
                .add(basis.z.scale(direction.z));
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

        Direction facing = state.getValue(FACING);

        Vec3 axis = viewDirection.normalize();

        Vec3 currentFacing = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );

        double angle = counterclockwise
                ? -Math.PI / 2.0
                : Math.PI / 2.0;

        Vec3 rotated = rotateVector(
                currentFacing,
                axis,
                angle
        );

        Direction newFacing = Direction.getNearest(
                rotated.x,
                rotated.y,
                rotated.z
        );
        if (newFacing == facing)
            return false;

        level.setBlock(pos, state.setValue(FACING, newFacing), 3);
        return true;
    }

    private static Vec3 rotateVector(
            Vec3 vector,
            Vec3 axis,
            double angle
    ) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return vector
                .scale(cos)
                .add(axis.cross(vector).scale(sin))
                .add(axis.scale(axis.dot(vector) * (1.0 - cos)));
    }

    protected Basis getBasis(BlockState state) {
        Direction facing = state.getValue(FACING);

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

        return new Basis(x, y, z);
    }

    protected record Basis(
            Vec3 x,
            Vec3 y,
            Vec3 z
    ) {}
}