package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.centertain.ceac.decal.client.render.DecalShaders;
import net.centertain.ceac.decal.client.render.OpaqueLightmapCaptureState;
import net.centertain.ceac.decal.client.render.TranslucentCaptureState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    @Redirect(
            method = "setShader",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;"
            )
    )
    private static Object ceac$replaceShader(
            Supplier<ShaderInstance> supplier
    ) {
        ShaderInstance shader = supplier.get();
        if (TranslucentCaptureState.isActive())
            if (shader == GameRenderer.getRendertypeTranslucentShader())
                return DecalShaders.getTranslucentCapture();
        if (OpaqueLightmapCaptureState.isActive())
            if (
                    shader == GameRenderer.getRendertypeSolidShader() ||
                    shader == GameRenderer.getRendertypeCutoutMippedShader() ||
                    shader == GameRenderer.getRendertypeCutoutShader()
            )
                return DecalShaders.getOpaqueLightmapCapture();
        return shader;
    }
}
