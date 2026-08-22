package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.centertain.ceac.decal.client.DecalShaders;
import net.centertain.ceac.decal.client.TranslucentCaptureState;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
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
    private void ceac$replaceTranslucentShader(CallbackInfo ci) {
        if (!TranslucentCaptureState.isActive())
            return;
        if (!((Object) this instanceof RenderStateShard.ShaderStateShard))
            return;
        ShaderInstance shader = DecalShaders.getTranslucentCapture();
        if (shader == null)
            return;
        //System.out.println("CEAC: replacing ShaderStateShard with " + shader.getName());
        RenderSystem.setShader(() -> shader);
        ci.cancel();
    }
}
