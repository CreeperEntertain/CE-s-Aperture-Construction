package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.decal.client.TranslucentCapture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public final class LevelRendererMixin {
    @Inject(
            method = "renderChunkLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;setupRenderState()V",
                    shift = At.Shift.AFTER
            )
    )
    private void ceac$captureTranslucent(
            RenderType renderType,
            PoseStack poseStack,
            double camX,
            double camY,
            double camZ,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (!TranslucentCapture.isCapturing())
            return;
        if (renderType != RenderType.translucent())
            return;
        TranslucentCapture.prepareShader();
    }
}
