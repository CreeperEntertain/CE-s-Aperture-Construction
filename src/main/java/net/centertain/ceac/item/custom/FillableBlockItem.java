package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.types.fillable.FillableBlock;
import net.centertain.ceac.material.shapes.MaterialShapeBlockEntity;
import net.centertain.ceac.material.shapes.utility.FillableBlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

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

    @Override
    protected boolean placeBlock(
            @NotNull BlockPlaceContext context,
            @NotNull BlockState state
    ) {
        if (!super.placeBlock(context, state))
            return false;
        if (!(context instanceof FillableBlockPlaceContext fillableContext))
            return true;
        if (!(getBlock() instanceof FillableBlock))
            return true;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return true;

        int fillIndex = fillableContext.getInitialFillIndex();

        BitSet mask = new BitSet();
        mask.set(Math.max(fillIndex, 0));

        blockEntity.setFillMask(mask);

        return true;
    }

    @Override
    public @Nullable BlockPlaceContext updatePlacementContext(@NotNull BlockPlaceContext context) {
        BlockPlaceContext updated = super.updatePlacementContext(context);
        if (updated == null)
            return null;
        return new FillableBlockPlaceContext(updated);
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

        BitSet mask = fillable.getFillMask(level, pos);
        if (mask.get(fillIndex))
            return null;

        mask.set(fillIndex);
        fillable.setFillMask(level, pos, mask);

        if (!level.isClientSide) {
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
