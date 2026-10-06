package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.FillableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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
    public @NotNull InteractionResult useOn(
            @NotNull UseOnContext context
    ) {
        if (!(getBlock() instanceof FillableBlock fillable))
            return super.useOn(context);

        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Level level = context.getLevel();

        BlockState clickedState = level.getBlockState(clickedPos);
        if (clickedState.getBlock() == getBlock())
            return super.useOn(context);

        BlockPos targetPos = clickedPos.relative(clickedFace);
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() != getBlock())
            return super.useOn(context);

        Direction targetFace = clickedFace.getOpposite();

        int fillIndex = fillable.getFillIndex(
                targetState,
                level,
                targetPos,
                CollisionContext.empty(),
                context.getClickLocation(),
                targetFace
        );
        if (fillIndex < 0)
            return super.useOn(context);

        int mask = fillable.getFillMask(targetState);
        int bit = 1 << fillIndex;
        if ((mask & bit) != 0)
            return super.useOn(context);

        BlockState newState = fillable.setFillMask(
                targetState,
                mask | bit
        );

        if (!level.isClientSide) {
            level.setBlock(targetPos, newState, Block.UPDATE_ALL);

            ItemStack stack = context.getItemInHand();
            assert context.getPlayer() != null;
            if (!context.getPlayer().isCreative())
                stack.shrink(1);

            SoundType soundType = newState.getSoundType(level, targetPos, context.getPlayer());

            level.playSound(
                    null,
                    targetPos,
                    soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0f) / 2.0f,
                    soundType.getPitch() * 0.8f
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
