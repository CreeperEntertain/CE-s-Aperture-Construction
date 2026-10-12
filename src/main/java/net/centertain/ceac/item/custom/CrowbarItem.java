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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
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

    @SuppressWarnings("resource")
    @Override
    public boolean hurtEnemy(
            @NotNull ItemStack stack,
            @NotNull LivingEntity target,
            @NotNull LivingEntity attacker
    ) {
        if (!(attacker instanceof Player player) || attacker.level().isClientSide)
            return false;
        target.hurt(player.damageSources().playerAttack(player), 4.0f);
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(
                player.getMainHandItem() == stack ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
        ));
        return false;
    }

    @Override
    public boolean mineBlock(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull BlockState state,
            @NotNull BlockPos pos,
            @NotNull LivingEntity entity
    ) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0f)
            stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(
                    entity.getMainHandItem() == stack ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
            ));
        return false;
    }

    @SuppressWarnings("resource")
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (!(entity instanceof Player player) || player.level().isClientSide)
            return false;

        Level level = player.level();
        double reach = player.getBlockReach();

        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0f).scale(reach));

        BlockHitResult blockHit = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
        double blockDistanceSquared = blockHit.getType() == HitResult.Type.MISS
                ? reach * reach
                : start.distanceToSqr(blockHit.getLocation());
        AABB searchArea = player.getBoundingBox()
                .expandTowards(end.subtract(start))
                .inflate(1.0);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player,
                start,
                end,
                searchArea,
                target -> !target.isSpectator() && target.isPickable(),
                reach * reach
        );

        if (entityHit != null && start.distanceToSqr(entityHit.getLocation()) < blockDistanceSquared)
            playSound(level, ModSounds.CROWBAR_CLANK.get(), entityHit.getLocation());
        else if (blockHit.getType() != HitResult.Type.MISS)
            playSound(level, ModSounds.CROWBAR_CLANK.get(), blockHit.getLocation());
        else
            playSound(level, ModSounds.CROWBAR_SWING.get(), player.getEyePosition());

        return false;
    }

    private void playSound(
            Level level,
            SoundEvent sound,
            Vec3 coordinate
    ) {
        level.playSound(
                null,
                coordinate.x,
                coordinate.y,
                coordinate.z,
                sound,
                SoundSource.BLOCKS,
                Mathworks.randomBetween(0.9f, 1.1f),
                Mathworks.randomBetween(0.9f, 1.1f)
        );
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
