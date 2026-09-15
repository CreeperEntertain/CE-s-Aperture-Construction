package net.centertain.ceac.phys_screen.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.decal.client.render.CeacRenderTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class PhysGuiGraphics {
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final VertexConsumer vertexConsumer;

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
        return  bufferSource;
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
}
