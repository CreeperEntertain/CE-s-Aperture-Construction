package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.FillableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FillableBlockItem extends BlockItem {
    public FillableBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext context) {
        BlockPlaceContext updated = updatePlacementContext(context);
        if (updated == null)
            return InteractionResult.FAIL;
        return super.place(updated);
    }

    @Override
    public @Nullable BlockPlaceContext updatePlacementContext(@NotNull BlockPlaceContext context) {
        if (!(getBlock() instanceof FillableBlock fillable))
            return context;

        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState clickedState = level.getBlockState(clickedPos);
        if (clickedState.getBlock() == getBlock()) // Do absolutely fucking nothing special here
            return context;

        Direction clickedFace = context.getClickedFace();
        Direction targetFace = clickedFace.getOpposite();

        BlockPos targetPos = clickedPos.relative(clickedFace);
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() != getBlock()) // Once again do absolutely nothing special
            return context;

        int fillIndex = fillable.getFillIndex(
                targetState,
                level,
                targetPos,
                CollisionContext.empty(),
                context.getClickLocation(),
                targetFace
        );
        if (fillIndex < 0)
            return context;
        if ((fillable.getFillMask(targetState) & (1 << fillIndex)) != 0)
            return context;

        return new FillablePlacementContext(context, targetPos, targetFace);
    }

    private static class FillablePlacementContext extends BlockPlaceContext {
        private FillablePlacementContext(
                BlockPlaceContext context,
                BlockPos targetPos,
                Direction targetFace
        ) {
            super(
                    context.getLevel(),
                    context.getPlayer(),
                    context.getHand(),
                    context.getItemInHand(),
                    new BlockHitResult(
                            context.getClickLocation(),
                            targetFace,
                            targetPos,
                            context.isInside()
                    )
            );

            replaceClicked = true;
        }
    }
}
