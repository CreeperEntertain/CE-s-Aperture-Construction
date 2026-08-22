package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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
            at = @At("HEAD")
    )
    private void ceac$debugShaderState(CallbackInfo ci) {
        if (!TranslucentCaptureState.isActive())
            return;

        if ((Object) this instanceof RenderStateShard.DepthTestStateShard) {
            RenderSystem.disableDepthTest();
        }

        if ((Object) this instanceof RenderStateShard.CullStateShard) {
            RenderSystem.disableCull();
        }

        //System.out.println("CEAC RenderStateShard.setupRenderState: " + this);
    }
}
