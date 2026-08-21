package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.decal.client.TranslucentRenderTargets;
import net.centertain.ceac.decal.client.TranslucentRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
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
                    //       ^ WHAT THE FUCK??
                    ordinal = 3,
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

        target.clear(Minecraft.ON_OSX);
        target.bindWrite(true);

        LevelRendererAccessor renderer = (LevelRendererAccessor) (Object) this;
        Vec3 cameraPos = camera.getPosition();

        renderer.ceac$renderChunkLayer(
                TranslucentRenderTypes.translucentDepth(),
                poseStack,
                cameraPos.x,
                cameraPos.y,
                cameraPos.z,
                projectionMatrix
        );

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }
}
