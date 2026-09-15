package net.centertain.ceac.phys_screen.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.client.render.CeacRenderTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PhysGuiGraphics {
    private final PoseStack poseStack;
    private final VertexConsumer vertexConsumer;
    private final MultiBufferSource bufferSource;

    public PhysGuiGraphics(
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.vertexConsumer = bufferSource.getBuffer(CeacRenderTypes.IN_WORLD_UI);
    }

    public PoseStack pose() {
        return poseStack;
    }
    public MultiBufferSource bufferSource() {
        return bufferSource;
    }

    public VertexConsumer vertexConsumer() {
        return vertexConsumer;
    }

    public void fill(
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {
        Matrix4f pose = poseStack.last().pose();

        vertexConsumer
                .vertex(pose, left, top, 0.0f)
                .color(color)
                .endVertex();
        vertexConsumer
                .vertex(pose, right, top, 0.0f)
                .color(color)
                .endVertex();
        vertexConsumer
                .vertex(pose, right, bottom, 0.0f)
                .color(color)
                .endVertex();
        vertexConsumer
                .vertex(pose, left, bottom, 0.0f)
                .color(color)
                .endVertex();
    }

    public void drawString(
            Font font,
            Component text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        font.drawInBatch(
                text.getVisualOrderText(),
                x,
                y,
                color,
                shadow,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                15728880
        );
    }

    public void blit(
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height
    ) {
        RenderSystem.setShaderTexture(0, texture);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.text(texture));

        Matrix4f pose = poseStack.last().pose();

        vertexConsumer
                .vertex(pose, x, y, 0.0f)
                .color(255, 255, 255, 255)
                .uv(0.0f, 0.0f)
                .endVertex();
        vertexConsumer
                .vertex(pose, x, y + height, 0.0f)
                .color(255, 255, 255, 255)
                .uv(0.0f, 1.0f)
                .endVertex();
        vertexConsumer
                .vertex(pose, x + width, y + height, 0.0f)
                .color(255, 255, 255, 255)
                .uv(1.0f, 1.0f)
                .endVertex();
        vertexConsumer
                .vertex(pose, x + width, y, 0.0f)
                .color(255, 255, 255, 255)
                .uv(1.0f, 0.0f)
                .endVertex();
    }
}
