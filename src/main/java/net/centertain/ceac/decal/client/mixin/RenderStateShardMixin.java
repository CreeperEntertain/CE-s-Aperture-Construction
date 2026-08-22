package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.centertain.ceac.decal.client.DecalShaders;
import net.centertain.ceac.decal.client.TranslucentCaptureState;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderStateShard.class)
public abstract class RenderStateShardMixin {

    @Inject(
            method = "setupRenderState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$overrideShader(CallbackInfo ci) {
        if (!TranslucentCaptureState.isActive())
            return;
        RenderSystem.setShader(DecalShaders::getTranslucentCapture);
        ci.cancel();
    }

    @Inject(
            method = "clearRenderState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$preventShaderReset(CallbackInfo ci) {
        if (!TranslucentCaptureState.isActive())
            return;
        ci.cancel();
    }
}
