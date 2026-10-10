package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.types.combinable.CombinableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombinableBlockItem extends BlockItem {
    public CombinableBlockItem(
            Block block,
            Properties properties
    ) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (!(getBlock() instanceof CombinableBlock block))
            return super.useOn(context);

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (clickedState.getBlock() == block) {
            InteractionResult result = tryInsert(block, clickedState, clickedPos, clickedFace, context);
            if (result != null)
                return result;
        }

        BlockPos targetPos = clickedPos.relative(clickedFace);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.getBlock() == block) {
            InteractionResult result = tryInsert(block, targetState, targetPos, clickedFace.getOpposite(), context);
            if (result != null)
                return result;
        }

        return super.useOn(context);
    }

    private @Nullable InteractionResult tryInsert(
            CombinableBlock block,
            BlockState state,
            BlockPos pos,
            Direction clickedFace,
            UseOnContext context
    ) {
        BooleanProperty property = CombinableBlock.propertyForAxis(block.getInsertionAxis(state, clickedFace));
        if (state.getValue(property))
            return null;

        Level level = context.getLevel();
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(property, true), 3);

            Player player = context.getPlayer();
            if (player != null && !player.isCreative())
                context.getItemInHand().shrink(1);

            SoundType soundType = state.getSoundType(level, pos, player);

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
