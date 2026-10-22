package net.centertain.ceac.client.mixin;

import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.framework.client.PropMiningClient;
import net.centertain.ceac.prop.framework.common.PropHitResult;
import net.centertain.ceac.prop.framework.network.PropUsePacket;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(
            method = "startDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$consumeVanillaBlockStart(
            BlockPos pos,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (PropMiningClient.isAttackConsumed())
            cir.setReturnValue(false);
    }

    @Inject(
            method = "continueDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$consumeVanillaBlockContinue(
            BlockPos pos,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (PropMiningClient.isAttackConsumed())
            cir.setReturnValue(false);
    }

    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    public void ceac$useOnProp(
            LocalPlayer player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!(hitResult instanceof PropHitResult propHit))
            return;

        ModNetworking.CHANNEL.sendToServer(
                new PropUsePacket(propHit.getProp().getId(), hand)
        );

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
