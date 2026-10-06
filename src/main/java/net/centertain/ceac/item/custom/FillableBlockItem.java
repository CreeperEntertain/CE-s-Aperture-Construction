package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.FillableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FillableBlockItem extends BlockItem {
    public FillableBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
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

        Direction face = context.getClickedFace();
        BlockPos targetPos = clickedPos.relative(face);
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() != getBlock()) // Once again do absolutely nothing special
            return context;

        int fillIndex = fillable.getFillIndex(
                targetState,
                level,
                targetPos,
                CollisionContext.empty(),
                context.getClickLocation(),
                face
        );
        if (fillIndex < 0)
            return context;
        if ((fillable.getFillMask(targetState) & (1 << fillIndex)) != 0)
            return context;

        return BlockPlaceContext.at(context, targetPos, face);
    }
}
