package net.centertain.ceac.block.custom.types.combinable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CombinableBlock extends Block {
    public static final BooleanProperty X_FILLED = BooleanProperty.create("x_filled");
    public static final BooleanProperty Y_FILLED = BooleanProperty.create("y_filled");
    public static final BooleanProperty Z_FILLED = BooleanProperty.create("z_filled");

    private final VoxelShape xShape;
    private final VoxelShape yShape;
    private final VoxelShape zShape;

    public CombinableBlock(
            Properties properties,
            VoxelShape xShape,
            VoxelShape yShape,
            VoxelShape zShape
    ) {
        super(properties);

        this.xShape = xShape;
        this.yShape = yShape;
        this.zShape = zShape;

        registerDefaultState(stateDefinition.any()
                .setValue(X_FILLED, false)
                .setValue(Y_FILLED, false)
                .setValue(Z_FILLED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    public static BooleanProperty propertyForAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> X_FILLED;
            case Y -> Y_FILLED;
            case Z -> Z_FILLED;
        };
    }

    public int getPieceCount(BlockState state) {
        int count = 0;
        if (state.getValue(X_FILLED)) count++;
        if (state.getValue(Y_FILLED)) count++;
        if (state.getValue(Z_FILLED)) count++;
        return count;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BooleanProperty property = propertyForAxis(context.getClickedFace().getAxis());
        return defaultBlockState().setValue(property, true);
    }

    private VoxelShape getOccupiedShape(BlockState state) {
        VoxelShape result = Shapes.empty();

        if (state.getValue(X_FILLED))
            result = Shapes.or(result, xShape);
        if (state.getValue(Y_FILLED))
            result = Shapes.or(result, yShape);
        if (state.getValue(Z_FILLED))
            result = Shapes.or(result, zShape);

        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return super.getShape(state, level, pos, context);
    }
}
