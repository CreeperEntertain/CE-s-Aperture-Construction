package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.types.material_shapes.MaterialShape;
import net.centertain.ceac.constants.PriceConstants;
import net.centertain.ceac.item.custom.types.BasicItem;
import net.centertain.ceac.material.shapes.MaterialFaceKey;
import net.centertain.ceac.material.shapes.MaterialShapeBlockEntity;
import net.centertain.ceac.material.shapes.MaterialShapeFaceInstance;
import net.centertain.ceac.sound.ModSounds;
import net.centertain.ceac.utility.Mathworks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrowbarItem extends BasicItem {
    public CrowbarItem(Properties properties) {
        super(
                properties.durability(250),
                PriceConstants.DEFAULT_TOOL
        );
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @Nullable Level level,
            @NotNull List<Component> tooltipComponents,
            @NotNull TooltipFlag isAdvanced
    ) {
        tooltipComponents.add(Component.translatable("tooltip.ceac.crowbar.desc"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof MaterialShape shape))
            return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return InteractionResult.PASS;

        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        Vec3 localHit = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        localHit = shape.transformPointToLocal(state, localHit);
        MaterialShapeFaceInstance instance = shape.getNearestFaceInstance(state, level, pos, localHit);
        if (instance == null)
            return InteractionResult.PASS;

        MaterialFaceKey key = instance.key();
        if (!context.getItemInHand().isDamageableItem())
            return InteractionResult.PASS;
        // Skip on empty face
        if (!player.isShiftKeyDown() && blockEntity.getMaterial(key) == null)
            return InteractionResult.PASS;
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        boolean success;
        if (player.isShiftKeyDown())
            success = blockEntity.removeMaterials(instance.pieceIndex());
        else {
            blockEntity.removeMaterial(key);
            success = true;
        }
        if (!success)
            return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
        level.playSound(
                null,
                pos,
                ModSounds.CROWBAR_CLANK.get(),
                SoundSource.BLOCKS,
                Mathworks.randomBetween(0.9f, 1.1f),
                Mathworks.randomBetween(0.9f, 1.1f)
        );

        return InteractionResult.SUCCESS;
    }
}
