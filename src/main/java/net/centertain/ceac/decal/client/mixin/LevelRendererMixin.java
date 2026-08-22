package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.decal.client.DecalShaders;
import net.centertain.ceac.decal.client.TranslucentCaptureState;
import net.centertain.ceac.decal.client.TranslucentRenderTargets;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void ceac$renderTranslucentDepth(
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
        RenderTarget target = TranslucentRenderTargets.getTranslucentDepth();
        if (target == null)
            return;

        LevelRendererAccessor renderer = (LevelRendererAccessor) (Object) this;
        Vec3 cameraPos = camera.getPosition();

        target.clear(Minecraft.ON_OSX);

        // TEMPORARILY kept as the destination whilst validating capture shader
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

        TranslucentCaptureState.begin();

        try {
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.disableBlend();

            renderer.ceac$renderChunkLayer(
                    RenderType.translucent(),
                    poseStack,
                    cameraPos.x,
                    cameraPos.y,
                    cameraPos.z,
                    projectionMatrix
            );

            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
        } finally {
            TranslucentCaptureState.end();
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
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

        assert modelView != null;
        modelView.set(poseStack.last().pose());
        assert proj != null;
        proj.set(projectionMatrix);
        assert chunkOffset != null;
        chunkOffset.set(0.0F, 0.0F, 0.0F);
    }
}
