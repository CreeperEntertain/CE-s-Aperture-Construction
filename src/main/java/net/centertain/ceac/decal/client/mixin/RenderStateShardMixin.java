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
    private void ceac$setupRenderState(CallbackInfo ci) {
        RenderStateShard shard = (RenderStateShard) (Object) this;

        if (shard == RenderStateShardAccessor.ceac$getTranslucentShader()
                && TranslucentCaptureState.isCapturing()) {

            RenderSystem.setShader(DecalShaders::getTranslucentCaptureShader);
            ci.cancel();
        }
    }
}
