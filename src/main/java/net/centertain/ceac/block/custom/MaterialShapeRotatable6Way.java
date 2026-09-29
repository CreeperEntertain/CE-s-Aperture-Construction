package net.centertain.ceac.block.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MaterialShapeRotatable6Way extends MaterialShape {
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


    protected Basis getBasis(BlockState state) {
        Direction facing = state.getValue(FACING);

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
        Vec3 z = y.cross(forward).normalize();

        return new Basis(x, y, z);
    }

    protected record Basis(
            Vec3 x,
            Vec3 y,
            Vec3 z
    ) {}
}
