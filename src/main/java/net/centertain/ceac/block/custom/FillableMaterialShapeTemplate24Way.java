package net.centertain.ceac.block.custom;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class FillableMaterialShapeTemplate24Way extends MaterialShapeRotatable24Way implements FillableBlock {
    protected final Map<Direction, VoxelShape[]> SHAPES;

    protected FillableMaterialShapeTemplate24Way(
            Properties properties,
            String subcategory,
            Map<Direction, VoxelShape[]> shapes
    ) {
        super(properties, subcategory);
        SHAPES = shapes;
    }

    @Override
    public @NotNull BakedModel getFillModel(BlockState state) {
        return getBakedModel();
    }

    @Override
    public VoxelShape getFillPieceShape(
            @NotNull BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(ROTATION)];
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getFilledShape(state, level, pos, context);
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

        return index >= 0 && (getFillMask(context.getLevel(), context.getClickedPos()) & (1 << index)) == 0;
    }
}
