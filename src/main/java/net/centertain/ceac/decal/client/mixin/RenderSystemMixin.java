package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.centertain.ceac.decal.client.DecalShaders;
import net.centertain.ceac.decal.client.TranslucentCaptureState;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {

    @Inject(
            method = "setShader",
            at = @At("HEAD")
    )
    private static void ceac$replaceTranslucentShader(
            Supplier<ShaderInstance> shader,
            CallbackInfo ci
    ) {
        if (!TranslucentCaptureState.isActive())
            return;
        ShaderInstance original = shader.get();
        if (original == null)
            return;
        if (!"rendertype_translucent".equals(original.getName()))
            return;
        ShaderInstance capture = DecalShaders.getTranslucentCapture();
        if (capture == null)
            return;
        //System.out.println("CEAC: replacing " + original.getName() + " with " + capture.getName());
        RenderSystem.setShader(() -> capture);
    }
}
