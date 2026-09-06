package net.centertain.ceac.decal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.GuiConstants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class DecalPreviewer {
    private static final double EXTRUSION = 0.002;

    private final Decal decal;

    private final Vec3 origin;
    private final Vec3 normal;
    private final Vec3 right;
    private final Vec3 up;

    private final double halfWidth;
    private final double halfHeight;
    private final double halfDepth;

    public DecalPreviewer(Decal decal) {
        this.decal = decal;

        this.origin = decal.getOrigin();
        this.normal = decal.getNormal().normalize();

        Vec3 reference = Math.abs(normal.y) < 0.999
                ? new Vec3(0, 1, 0)
                : new Vec3(1, 0, 0);

        Vec3 right = reference.cross(normal).normalize();
        Vec3 up = normal.cross(right).normalize();

        double rotation = (Math.PI * 2.0 / 16.0) * decal.getRotation();

        this.right = right.scale(Math.cos(rotation)).add(up.scale(Math.sin(rotation))).normalize();
        this.up = normal.cross(right).normalize();

        this.halfWidth = decal.getPixelWidth() / 32.0 + EXTRUSION;
        this.halfHeight = decal.getPixelHeight() / 32.0 + EXTRUSION;
        this.halfDepth = decal.getBlockDepth() / 2.0 + EXTRUSION;
    }

    public Decal getDecal() {
        return decal;
    }
    public Vec3 getOrigin() {
        return origin;
    }
    public Vec3 getNormal() {
        return normal;
    }
    public Vec3 getRight() {
        return right;
    }
    public Vec3 getUp() {
        return up;
    }
    public double getHalfWidth() {
        return halfWidth;
    }
    public double getHalfHeight() {
        return halfHeight;
    }
    public double getHalfDepth() {
        return halfDepth;
    }

    public Vec3[] getCorners() {
        Vec3 width = right.scale(halfWidth);
        Vec3 height = up.scale(halfHeight);
        Vec3 depth = normal.scale(halfDepth);

        return new Vec3[] {
                origin.add(width).add(height).add(depth),
                origin.add(width).add(height).subtract(depth),
                origin.add(width).subtract(height).add(depth),
                origin.add(width).subtract(height).subtract(depth),
                origin.subtract(width).add(height).add(depth),
                origin.subtract(width).add(height).subtract(depth),
                origin.subtract(width).subtract(height).add(depth),
                origin.subtract(width).subtract(height).subtract(depth)
        };
    }

    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Vec3 cameraPosition
    ) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        poseStack.pushPose();
        poseStack.translate(
                -cameraPosition.x,
                -cameraPosition.y,
                -cameraPosition.z
        );

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        Vec3[] corners = getCorners();

        drawLine(vertexConsumer, pose, normalMatrix, corners[0], corners[1]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[0], corners[2]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[0], corners[4]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[1], corners[3]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[1], corners[5]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[2], corners[3]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[2], corners[6]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[3], corners[7]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[4], corners[5]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[4], corners[6]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[5], corners[7]);
        drawLine(vertexConsumer, pose, normalMatrix, corners[6], corners[7]); // Heh...

        poseStack.popPose();
    }

    private void drawLine(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3 start,
            Vec3 end
    ) {
        Vec3 direction = end.subtract(start);
        float length = (float) direction.length();

        if (length < 1.0e-6f)
            return;

        float x = (float) (direction.x / length);
        float y = (float) (direction.y / length);
        float z = (float) (direction.z / length);

        vertexConsumer
                .vertex(pose, (float) start.x, (float) start.y, (float) start.z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .normal(normalMatrix, x, y, z)
                .endVertex();
        vertexConsumer
                .vertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .normal(normalMatrix, x, y, z)
                .endVertex();
    }
}
