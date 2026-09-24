package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties
                .durability(250)
        );
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @Nullable Level level,
            @NotNull List<Component> tooltipComponents,
            @NotNull TooltipFlag isAdvanced
    ) {
        tooltipComponents.add(Component.translatable("tooltip.ceac.wrench.desc"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);
        if (!(player instanceof ServerPlayer serverPlayer))
            return InteractionResultHolder.pass(stack);
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;
        assert hitResult != null;
        if (hitResult.getType() != HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(stack);
        BlockPos pos = BlockPos.containing(hitResult.getLocation());

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof MaterialShape shape))
            return InteractionResultHolder.pass(stack);

        Vec3 lookVector = serverPlayer.getViewVector(1.0f);

        boolean success = shape.rotateFromViewDirection(
                level,
                pos,
                lookVector,
                !serverPlayer.isCrouching()
        );
        if (success) {
            stack.hurtAndBreak(1, serverPlayer, p -> p.broadcastBreakEvent(hand));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
