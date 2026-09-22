package net.centertain.ceac.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public final class ClippingVertexConsumer extends VertexConsumerWrapper {

    private final float clipLeft;
    private final float clipRight;

    private final Vertex[] vertices = new Vertex[4];
    private int vertexCount;

    private Matrix4f matrix;
    private double x;
    private double y;
    private double z;

    private int red;
    private int green;
    private int blue;
    private int alpha;

    private float u;
    private float v;

    private int overlayU;
    private int overlayV;
    private boolean hasOverlay;

    private int lightU;
    private int lightV;
    private boolean hasLight;

    private float normalX;
    private float normalY;
    private float normalZ;
    private boolean hasNormal;


    private record Vertex(
            Matrix4f matrix,
            double x, double y, double z,
            int red, int green, int blue, int alpha,
            float u, float v,
            int overlayU, int overlayV, boolean hasOverlay,
            int lightU, int lightV, boolean hasLight,
            float normalX, float normalY, float normalZ, boolean hasNormal
    ) {}


    public ClippingVertexConsumer(
            VertexConsumer parent,
            float clipLeft,
            float clipRight
    ) {
        super(parent);
        this.clipLeft = clipLeft;
        this.clipRight = clipRight;
    }


    @Override
    public @NotNull VertexConsumer vertex(
            @NotNull Matrix4f matrix,
            float x,
            float y,
            float z
    ) {
        this.matrix = new Matrix4f(matrix);
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }


    @Override
    public @NotNull VertexConsumer color(
            int red,
            int green,
            int blue,
            int alpha
    ) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;

        return this;
    }


    @Override
    public @NotNull VertexConsumer uv(
            float u,
            float v
    ) {
        this.u = u;
        this.v = v;

        return this;
    }


    @Override
    public @NotNull VertexConsumer overlayCoords(
            int u,
            int v
    ) {
        overlayU = u;
        overlayV = v;
        hasOverlay = true;

        return this;
    }


    @Override
    public @NotNull VertexConsumer uv2(
            int u,
            int v
    ) {
        lightU = u;
        lightV = v;
        hasLight = true;

        return this;
    }


    @Override
    public @NotNull VertexConsumer normal(
            float x,
            float y,
            float z
    ) {
        normalX = x;
        normalY = y;
        normalZ = z;
        hasNormal = true;

        return this;
    }


    @Override
    public void endVertex() {
        if (matrix == null)
            return;

        vertices[vertexCount++] = new Vertex(
                matrix,
                x, y, z,
                red, green, blue, alpha,
                u, v,
                overlayU, overlayV, hasOverlay,
                lightU, lightV, hasLight,
                normalX, normalY, normalZ, hasNormal
        );

        matrix = null;

        if (vertexCount == 4)
            renderQuad();
    }


    private void renderQuad() {
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;

        for (Vertex vertex : vertices) {
            minX = Math.min(minX, (float) vertex.x);
            maxX = Math.max(maxX, (float) vertex.x);
        }

        if (maxX <= clipLeft || minX >= clipRight) {
            vertexCount = 0;
            return;
        }

        float width = maxX - minX;

        for (Vertex vertex : vertices) {
            float oldX = (float) vertex.x;
            float newX = Math.max(clipLeft, Math.min(clipRight, oldX));

            float newU = vertex.u;

            if (width > 0.0f && newX != oldX)
                newU += (newX - oldX) * (getU1() - getU0()) / width;

            VertexConsumer vertexConsumer = parent.vertex(
                    vertex.matrix,
                    newX,
                    (float) vertex.y,
                    (float) vertex.z
            );

            vertexConsumer.color(
                    vertex.red,
                    vertex.green,
                    vertex.blue,
                    vertex.alpha
            );
            vertexConsumer.uv(newU, vertex.v);

            if (vertex.hasOverlay)
                vertexConsumer.overlayCoords(vertex.overlayU, vertex.overlayV);

            if (vertex.hasLight)
                vertexConsumer.uv2(vertex.lightU, vertex.lightV);

            if (vertex.hasNormal)
                vertexConsumer.normal(
                        vertex.normalX,
                        vertex.normalY,
                        vertex.normalZ
                );

            vertexConsumer.endVertex();
        }

        vertexCount = 0;
    }


    private float getU0() {
        float u0 = vertices[0].u;

        for (int i = 1; i < 4; i++)
            u0 = Math.min(u0, vertices[i].u);

        return u0;
    }


    private float getU1() {
        float u1 = vertices[0].u;

        for (int i = 1; i < 4; i++)
            u1 = Math.max(u1, vertices[i].u);

        return u1;
    }
}
