package net.centertain.ceac.client.mixin;

import net.centertain.ceac.prop.client.PropMiningClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
}
