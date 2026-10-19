package net.centertain.ceac.client.mixin;

import net.centertain.ceac.prop.framework.common.PropHitResult;
import net.centertain.ceac.prop.framework.client.PropMiningClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(
            method = "startAttack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$startPropMining(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (!(minecraft.hitResult instanceof PropHitResult hit))
            return;
        PropMiningClient.start(hit.getProp());
        cir.setReturnValue(true);
    }

    @Inject(
            method = "continueAttack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$continuePropMining(
            boolean breaking,
            CallbackInfo ci
    ) {
        if (PropMiningClient.handleContinue(breaking))
            ci.cancel();
    }
}
