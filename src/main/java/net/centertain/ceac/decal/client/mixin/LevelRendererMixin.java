package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.centertain.ceac.decal.client.DecalRenderer;
import net.centertain.ceac.decal.client.DecalShaders;
import net.centertain.ceac.decal.client.TranslucentCaptureState;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(
            method = "renderChunkLayer",
            at = @At("HEAD")
    )
    private void ceac$beginTranslucentCapture(
            RenderType renderType,
            PoseStack poseStack,
            double camX,
            double camY,
            double camZ,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (renderType == RenderType.translucent())
            TranslucentCaptureState.begin();
    }

    @Inject(
            method = "renderChunkLayer",
            at = @At("TAIL")
    )
    private void ceac$endTranslucentCapture(
            RenderType renderType,
            PoseStack poseStack,
            double camX,
            double camY,
            double camZ,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (renderType != RenderType.translucent())
            return;

        TranslucentCaptureState.end();

        Minecraft minecraft = Minecraft.getInstance();

        DecalRenderer.renderKBuffer(
                minecraft.gameRenderer.getMainCamera(),
                minecraft.options.graphicsMode().get() == GraphicsStatus.FABULOUS
        );
    }

    @Inject(
            method = "renderChunkLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ShaderInstance;apply()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void ceac$prepareCaptureShader(
            RenderType renderType,
            PoseStack poseStack,
            double camX,
            double camY,
            double camZ,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (!TranslucentCaptureState.isActive())
            return;
        ShaderInstance capture = DecalShaders.getTranslucentCapture();
        if (RenderSystem.getShader() != capture)
            return;

        Uniform modelView = capture.getUniform("ModelViewMat");
        Uniform proj = capture.getUniform("ProjMat");
        Uniform chunkOffset = capture.getUniform("ChunkOffset");

        if (modelView != null)
            modelView.set(poseStack.last().pose());
        if (proj != null)
            proj.set(projectionMatrix);
        if (chunkOffset != null)
            chunkOffset.set(0.0F, 0.0F, 0.0F);

        TranslucentKBuffer.setShaderUniforms(capture);
    }
}
