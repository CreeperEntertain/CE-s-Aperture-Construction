package net.centertain.ceac.phys_screen.framework;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.client.mixin.GameRendererAccessor;
import net.centertain.ceac.client.render.CeacRenderTypes;
import net.centertain.ceac.client.render.ClippingVertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class PhysGuiGraphics {
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final double cameraZ;
    private final Matrix4f projectionMatrix;

    private final List<TextDraw> queuedText = new ArrayList<>();

    private record TextDraw(
            Font font,
            Component text,
            Matrix4f pose,
            int x,
            int y,
            int color,
            boolean shadow,
            float clipLeft,
            float clipRight
    ) {}


    public PhysGuiGraphics(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            double cameraZ,
            Matrix4f projectionMatrix
    ) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.cameraZ = cameraZ;
        this.projectionMatrix = projectionMatrix;
    }


    public PoseStack pose() {
        return poseStack;
    }
    public MultiBufferSource bufferSource() {
        return bufferSource;
    }

    public VertexConsumer vertexConsumer() {
        return bufferSource.getBuffer(CeacRenderTypes.IN_WORLD_UI);
    }

    public void fill(
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(CeacRenderTypes.IN_WORLD_UI);

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
        queuedText.add(new TextDraw(
                font,
                text,
                new Matrix4f(poseStack.last().pose()),
                x,
                y,
                color,
                shadow,
                Float.NEGATIVE_INFINITY,
                Float.POSITIVE_INFINITY
        ));
    }

    public void drawStringClipped(
            Font font,
            Component text,
            int x,
            int y,
            int color,
            boolean shadow,
            float clipLeft,
            float clipRight
    ) {
        queuedText.add(new TextDraw(
                font,
                text,
                new Matrix4f(poseStack.last().pose()),
                x,
                y,
                color,
                shadow,
                clipLeft,
                clipRight
        ));
    }

    public void renderQueuedText() {
        Minecraft minecraft = Minecraft.getInstance();

        GameRenderer gameRenderer = minecraft.gameRenderer;
        Camera camera = gameRenderer.getMainCamera();

        double textFov = ((GameRendererAccessor) gameRenderer).ceac$getFov(
                camera,
                minecraft.getFrameTime(),
                false
        );

        Matrix4f textProjectionMatrix = gameRenderer.getProjectionMatrix(textFov);

        for (TextDraw textDraw : queuedText) {
            Matrix4f pose = new Matrix4f(textDraw.pose);
            float correction = (float) getTextProjectionCorrection(
                    textDraw.pose,
                    projectionMatrix,
                    textProjectionMatrix
            );
            pose.translate(
                    0.0f,
                    0.0f,
                    correction
            );

            if (Float.isInfinite(textDraw.clipLeft)) {
                textDraw.font.drawInBatch(
                        textDraw.text.getVisualOrderText(),
                        textDraw.x,
                        textDraw.y,
                        textDraw.color,
                        textDraw.shadow,
                        pose,
                        bufferSource,
                        Font.DisplayMode.NORMAL,
                        0,
                        15728880
                );
            } else {
                MultiBufferSource clippedBufferSource = renderClippedTextSource(textDraw.clipLeft, textDraw.clipRight);
                textDraw.font.drawInBatch(
                        textDraw.text.getVisualOrderText(),
                        textDraw.x,
                        textDraw.y,
                        textDraw.color,
                        textDraw.shadow,
                        pose,
                        clippedBufferSource,
                        Font.DisplayMode.NORMAL,
                        0,
                        15728880
                );
            }
        }

        queuedText.clear();
    }

    private double getTextProjectionCorrection(
            Matrix4f pose,
            Matrix4f worldProjection,
            Matrix4f textProjection
    ) {
        Vector4f viewPosition = new Vector4f(
                0.0f,
                0.0f,
                0.0f,
                1.0f
        );
        pose.transform(viewPosition);

        Vector4f viewZ = new Vector4f(
                0.0f,
                0.0f,
                1.0f,
                0.0f
        );
        pose.transform(viewZ);

        Vector4f worldClip = new Vector4f(viewPosition);
        worldProjection.transform(worldClip);

        double targetX = worldClip.x() / worldClip.w();
        double targetY = worldClip.y() / worldClip.w();

        Vector4f textClip = new Vector4f(viewPosition);
        textProjection.transform(textClip);

        Vector4f textZClip = new Vector4f(viewZ);
        textProjection.transform(textZClip);

        double bx = textClip.x() - targetX * textClip.w();
        double by = textClip.y() - targetY * textClip.w();

        double ax = textZClip.x() - targetX * textZClip.w();
        double ay = textZClip.y() - targetY * textZClip.w();

        double denominator = ax * ax + ay * ay;

        if (denominator < 1.0e-12)
            return 0.0;

        return -(ax * bx + ay * by) / denominator;
    }

    private MultiBufferSource renderClippedTextSource(
            float clipLeft,
            float clipRight
    ) {
        return renderType -> new ClippingVertexConsumer(
                bufferSource.getBuffer(renderType),
                clipLeft,
                clipRight
        );
    }

    public void blit(
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height
    ) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(CeacRenderTypes.inWorldUiTextured(texture));

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

        bufferSource.getBuffer(CeacRenderTypes.IN_WORLD_UI);
    }
}
