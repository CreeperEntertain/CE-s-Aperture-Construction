package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.FillableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

        if (clickedState.getBlock() == getBlock()) {
            InteractionResult result = tryFill(
                    fillable,
                    clickedState,
                    clickedPos,
                    clickedFace,
                    context
            );
            if (result != null)
                return result;
        }

        BlockPos targetPos = clickedPos.relative(clickedFace);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.getBlock() == getBlock()) {
            InteractionResult result = tryFill(
                    fillable,
                    targetState,
                    targetPos,
                    clickedFace.getOpposite(),
                    context
            );
            if (result != null)
                return result;
        }

        return super.useOn(context);
    }

    private @Nullable InteractionResult tryFill(
            FillableBlock fillable,
            BlockState state,
            BlockPos pos,
            Direction face,
            UseOnContext context
    ) {
        Level level = context.getLevel();
        int fillIndex = fillable.getFillIndex(
                state,
                level,
                pos,
                CollisionContext.empty(),
                context.getClickLocation(),
                face
        );
        if (fillIndex < 0)
            return null;

        int mask = fillable.getFillMask(state);
        int bit = 1 << fillIndex;
        if ((mask & bit) != 0)
            return null;

        BlockState newState = fillable.setFillMask(state, mask | bit);

        if (!level.isClientSide) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            Player player = context.getPlayer();

            if (player != null && !player.isCreative())
                context.getItemInHand().shrink(1);

            SoundType soundType = newState.getSoundType(level, pos, player);

            level.playSound(
                    null,
                    pos,
                    soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0f) / 2.0f,
                    soundType.getPitch() * 0.8f
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
