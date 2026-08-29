package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.centertain.ceac.decal.client.*;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(
            method = "renderLevel",
            at = @At("HEAD")
    )
    private void ceac$beginOpaqueLightmapFrame(
            PoseStack poseStack,
            float partialTick,
            long finishNanoTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (ClientDecals.getAll().isEmpty())
            return;
        DecalRenderer.captureOpaqueLightmap();
    }

    @Inject(
            method = "renderChunkLayer",
            at = @At("HEAD")
    )
    private void ceac$beginRenderLayerCapture(
            RenderType renderType,
            PoseStack poseStack,
            double camX,
            double camY,
            double camZ,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (renderType == RenderType.translucent()) {
            TranslucentCaptureState.begin();
            return;
        }
        if (
                renderType == RenderType.solid() ||
                renderType == RenderType.cutoutMipped() ||
                renderType == RenderType.cutout()
        )
            OpaqueLightmapCaptureState.begin(renderType);
    }

    @Inject(
            method = "renderChunkLayer",
            at = @At("TAIL")
    )
    private void ceac$endRenderLayerCapture(
            RenderType renderType,
            PoseStack poseStack,
            double camX,
            double camY,
            double camZ,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        if (renderType == RenderType.translucent()) {
            TranslucentCaptureState.end();
            Minecraft minecraft = Minecraft.getInstance();
            DecalRenderer.renderKBuffer(
                    minecraft.gameRenderer.getMainCamera(),
                    minecraft.options.graphicsMode().get() == GraphicsStatus.FABULOUS
            );
            return;
        }
        if (OpaqueLightmapCaptureState.isActive())
            OpaqueLightmapCaptureState.end();
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
        if (TranslucentCaptureState.isActive()) {
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

            return;
        }

        if (!OpaqueLightmapCaptureState.isActive())
            return;

        ShaderInstance capture = DecalShaders.getOpaqueLightmapCapture();

        if (RenderSystem.getShader() != capture)
            return;

        Uniform modelView = capture.getUniform("ModelViewMat");
        Uniform proj = capture.getUniform("ProjMat");
        Uniform chunkOffset = capture.getUniform("ChunkOffset");
        Uniform alphaCutoff = capture.getUniform("AlphaCutoff");
        Uniform colorModulator = capture.getUniform("ColorModulator");
        Uniform fogStart = capture.getUniform("FogStart");
        Uniform fogEnd = capture.getUniform("FogEnd");
        Uniform fogColor = capture.getUniform("FogColor");
        Uniform fogShape = capture.getUniform("FogShape");

        if (modelView != null)
            modelView.set(poseStack.last().pose());
        if (proj != null)
            proj.set(projectionMatrix);
        if (chunkOffset != null)
            chunkOffset.set(0.0F, 0.0F, 0.0F);
        if (alphaCutoff != null) {
            RenderType type = OpaqueLightmapCaptureState.getRenderType();
            if (type == RenderType.cutoutMipped())
                alphaCutoff.set(0.5F);
            else if (type == RenderType.cutout())
                alphaCutoff.set(0.1F);
            else
                alphaCutoff.set(0.0F);
        }
        if (colorModulator != null) {
            float[] color = RenderSystem.getShaderColor();
            colorModulator.set(color[0], color[1], color[2], color[3]);
        }
        if (fogStart != null)
            fogStart.set(RenderSystem.getShaderFogStart());
        if (fogEnd != null)
            fogEnd.set(RenderSystem.getShaderFogEnd());
        if (fogColor != null) {
            float[] color = RenderSystem.getShaderFogColor();
            fogColor.set(color[0], color[1], color[2], color[3]);
        }
        if (fogShape != null)
            fogShape.set(0);

        GL42.glBindImageTexture(
                0,
                DecalRenderer.getOpaqueLightmapDepthTexture(),
                0,
                false,
                0,
                GL15.GL_READ_WRITE,
                GL30.GL_R32UI
        );
        GL42.glBindImageTexture(
                1,
                DecalRenderer.getOpaqueLightmapTexture(),
                0,
                false,
                0,
                GL15.GL_WRITE_ONLY,
                GL30.GL_RGBA8
        );
    }
}
