package net.centertain.ceac.decal.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Invoker("renderChunkLayer")
    void ceac$renderChunkLayer(
            RenderType renderType,
            PoseStack poseStack,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f projectionMatrix
    );
}
