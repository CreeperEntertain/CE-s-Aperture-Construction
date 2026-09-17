package net.centertain.ceac.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.phys_screen.framework.GameRendererViewOffsetAccessor;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererViewOffsetAccessor {

    @Unique
    private static Matrix4f ceac$projectionBeforeViewOffset;
    @Unique
    private static Matrix4f ceac$projectionAfterViewOffset;

    @ModifyArg(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"
            ),
            index = 0
    )
    private PoseStack ceac$captureProjectionBeforeViewOffset(
            PoseStack poseStack
    ) {
        ceac$projectionBeforeViewOffset =
                new Matrix4f(poseStack.last().pose());

        return poseStack;
    }

    @ModifyArg(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V"
            ),
            index = 7
    )
    private Matrix4f ceac$captureProjectionAfterViewOffset(
            Matrix4f projectionMatrix
    ) {
        ceac$projectionAfterViewOffset = new Matrix4f(projectionMatrix);

        return projectionMatrix;
    }

    @Override
    public Matrix4f ceac$getProjectionBeforeViewOffset() {
        return ceac$projectionBeforeViewOffset == null
                ? null
                : new Matrix4f(ceac$projectionBeforeViewOffset);
    }

    @Override
    public Matrix4f ceac$getProjectionAfterViewOffset() {
        return ceac$projectionAfterViewOffset == null
                ? null
                : new Matrix4f(ceac$projectionAfterViewOffset);
    }
}
