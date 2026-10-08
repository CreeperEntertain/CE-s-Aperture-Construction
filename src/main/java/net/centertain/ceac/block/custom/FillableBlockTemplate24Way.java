package net.centertain.ceac.block.custom;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FillableBlockTemplate24Way extends MaterialShapeRotatable24Way implements FillableBlock {
    private IntegerProperty fill;

    protected FillableBlockTemplate24Way(
            Properties properties,
            String subcategory
    ) {
        super(properties, subcategory);
        registerDefaultState(defaultBlockState().setValue(fill, 1));
    }

    @Override
    protected void createBlockStateDefinition(
            @NotNull StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        fill = IntegerProperty.create("fill", 0, (1 << getMaxFillCount()) - 1);
        builder.add(fill);
    }

    @Override
    public IntegerProperty getFillProperty() {
        return fill;
    }

    @Override
    public @NotNull BakedModel getFillModel(BlockState state) {
        return getBakedModel();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(
            @NotNull BlockPlaceContext context
    ) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clicked = context.getLevel().getBlockState(clickedPos);
        if (clicked.getBlock() == this) {
            int index = getFillIndex(
                    clicked,
                    context.getLevel(),
                    clickedPos,
                    CollisionContext.empty(),
                    context.getClickLocation(),
                    context.getClickedFace()
            );
            if (index >= 0 && (getFillMask(clicked) & (1 << index)) == 0)
                return setFillMask(clicked, getFillMask(clicked) | (1 << index));
        }

        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;

        int index = getFillIndex(
                state,
                level,
                clickedPos,
                CollisionContext.empty(),
                context.getClickLocation(),
                context.getClickedFace().getOpposite()
        );
        if (index >= 0)
            return setFillMask(state, 1 << index);

        return setFillMask(state, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(
            @NotNull BlockState state,
            @NotNull BlockPlaceContext context
    ) {
        if (!context.getItemInHand().is(asItem()))
            return super.canBeReplaced(state, context);
        if (!context.replacingClickedOnBlock())
            return false;

        int index = getFillIndex(
                state,
                context.getLevel(),
                context.getClickedPos(),
                CollisionContext.empty(),
                context.getClickLocation(),
                context.getClickedFace()
        );

        return index >= 0 && (getFillMask(state) & (1 << index)) == 0;
    }
}
