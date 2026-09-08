package net.centertain.ceac.decal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.decal.client.DecalPlacement;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class DecalPreviewer {
    private static final double EXTRUSION = 0.002;

    private static final double GRID_RADIUS = 4.0;
    private static final double GRID_OFFSET = 0.003;

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

        right = right.scale(Math.cos(rotation))
                .add(up.scale(Math.sin(rotation)))
                .normalize();
        up = normal.cross(right).normalize();

        this.right = right;
        this.up = up;

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

        drawGrid(vertexConsumer, pose, normalMatrix);
        drawBox(vertexConsumer, pose, normalMatrix, corners);
        drawArrow(vertexConsumer, pose, normalMatrix);

        poseStack.popPose();
    }

    private void drawBox(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3[] corners
    ) {
        if (corners.length != 8)
            return;
        int color = GuiConstants.COLOR_SOLID_WHITE;
        int colorX = GuiConstants.COLOR_SOLID_RED;
        int colorY = GuiConstants.COLOR_SOLID_GREEN;
        int colorZ = GuiConstants.COLOR_SOLID_BLUE;

        drawLine(vertexConsumer, pose, normalMatrix, corners[0], corners[1], colorZ); // Z base
        drawLine(vertexConsumer, pose, normalMatrix, corners[0], corners[2], colorY); // Y base
        drawLine(vertexConsumer, pose, normalMatrix, corners[0], corners[4], colorX); // X base
        drawLine(vertexConsumer, pose, normalMatrix, corners[1], corners[3], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[1], corners[5], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[2], corners[3], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[2], corners[6], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[3], corners[7], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[4], corners[5], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[4], corners[6], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[5], corners[7], color);
        drawLine(vertexConsumer, pose, normalMatrix, corners[6], corners[7], color); // Heh...
    }

    private void drawArrow(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            Matrix3f normalMatrix
    ) {
        Vec3 faceCenter = origin.add(normal.scale(halfDepth));
        double distance = faceCenter.distanceTo(origin);
        int color = GuiConstants.COLOR_SOLID_YELLOW;

        // Arrow shaft
        drawLine(vertexConsumer, pose, normalMatrix, faceCenter, origin, color);

        // Arrow head base calculations
        Vec3 squareCenter = faceCenter.lerp(origin, 2.0 / 3.0);
        double halfSide = distance / 12.0;
        Vec3 squareAxisA = right.add(up).normalize().scale(halfSide);
        Vec3 squareAxisB = up.subtract(right).normalize().scale(halfSide);
        Vec3[] vertices = {
                squareCenter.add(squareAxisA).add(squareAxisB),
                squareCenter.add(squareAxisA).subtract(squareAxisB),
                squareCenter.subtract(squareAxisA).subtract(squareAxisB),
                squareCenter.subtract(squareAxisA).add(squareAxisB)
        };

        // Arrow head base
        drawLine(vertexConsumer, pose, normalMatrix, vertices[0], vertices[1], color);
        drawLine(vertexConsumer, pose, normalMatrix, vertices[1], vertices[2], color);
        drawLine(vertexConsumer, pose, normalMatrix, vertices[2], vertices[3], color);
        drawLine(vertexConsumer, pose, normalMatrix, vertices[3], vertices[0], color);

        // Arrow head connectors
        for (Vec3 vertex : vertices)
            drawLine(vertexConsumer, pose, normalMatrix, vertex, origin, color);
    }

    private void drawGrid(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            Matrix3f normalMatrix
    ) {
        double gridSize = DecalPlacement.getGridSize();
        int gridSizeIndex = DecalPlacement.getGridSizeIndex();
        if (gridSizeIndex == 0)
            return;
        int color = GuiConstants.COLOR_TRANSLUCENT_WHITE_25;
        int lineCount = (int) Math.floor(GRID_RADIUS / gridSize);

        for (int i = -lineCount; i <= lineCount; i++) {
            double offset = i * gridSize;
            Vec3 start = origin
                    .add(right.scale(offset))
                    .add(up.scale(-GRID_RADIUS))
                    .add(normal.scale(GRID_OFFSET));
            Vec3 end = origin
                    .add(right.scale(offset))
                    .add(up.scale(GRID_RADIUS))
                    .add(normal.scale(GRID_OFFSET));
            drawLine(vertexConsumer, pose, normalMatrix, start, end, color);
        }
        for (int i = -lineCount; i <= lineCount; i++) {
            double offset = i * gridSize;
            Vec3 start = origin
                    .add(right.scale(-GRID_RADIUS))
                    .add(up.scale(offset))
                    .add(normal.scale(GRID_OFFSET));
            Vec3 end = origin
                    .add(right.scale(GRID_RADIUS))
                    .add(up.scale(offset))
                    .add(normal.scale(GRID_OFFSET));
            drawLine(vertexConsumer, pose, normalMatrix, start, end, color);
        }
    }

    private void drawLine(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3 start,
            Vec3 end,
            int colorARGB
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
                .color(colorARGB)
                .normal(normalMatrix, x, y, z)
                .endVertex();
        vertexConsumer
                .vertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .color(colorARGB)
                .normal(normalMatrix, x, y, z)
                .endVertex();
    }
}
