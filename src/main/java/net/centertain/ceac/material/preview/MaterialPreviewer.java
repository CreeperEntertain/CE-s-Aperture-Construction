package net.centertain.ceac.material.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.material.Material;
import net.centertain.ceac.material.shapes.MaterialShapeFace;
import net.centertain.ceac.phys_screen.MaterialPreviewerHelpScreen;
import net.centertain.ceac.phys_screen.framework.PhysScreen;
import net.centertain.ceac.phys_screen.utility.PhysRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.List;

public final class MaterialPreviewer {
    private static final double EXTRUSION = 0.002;
    private static final double DIRECTION_BIAS = 1.0e-6;

    private static final int SELECTED_ALPHA = 255;
    private static final int UNSELECTED_ALPHA = 128;

    private static final PhysScreen helpScreen = new MaterialPreviewerHelpScreen();

    private static boolean initialized = false;
    private static boolean helpScreenShown = true;
    private static boolean previewVisible = false;

    private static @Nullable Material material;
    private static @Nullable Vector2i materialCoordinate;
    private static @Nullable BlockPos position;
    private static @Nullable MaterialShapeFace face;

    private MaterialPreviewer() {}

    public static boolean getHelpScreenShown() {
        return helpScreenShown;
    }
    public static boolean getPreviewVisible() {
        return previewVisible;
    }
    public static @Nullable Material getMaterial() {
        return material;
    }
    public static @Nullable Vector2i getMaterialCoordinate() {
        return materialCoordinate;
    }
    public static @Nullable BlockPos getPosition() {
        return position;
    }
    public static @Nullable MaterialShapeFace getFace() {
        return face;
    }

    public static void setHelpScreenShown(boolean state) {
        helpScreenShown = state;
    }
    public static void setPreviewVisible(boolean state) {
        previewVisible = state;
    }
    public static void setMaterial(@Nullable Material newMaterial) {
        material = newMaterial;
    }
    public static void setMaterialCoordinate(@Nullable Vector2i newMaterialCoordinate) {
        materialCoordinate = newMaterialCoordinate;
    }
    public static void setPosition(@Nullable BlockPos newPosition) {
        position = newPosition;
    }
    public static void setFace(@Nullable MaterialShapeFace newFace) {
        face = newFace;
    }


    public static void update(
            Material newMaterial,
            Vector2i newMaterialCoordinate,
            BlockPos newPosition,
            MaterialShapeFace newFace
    ) {
        material = newMaterial;
        materialCoordinate = newMaterialCoordinate;
        position = newPosition;
        face = newFace;
        init();
    }
    public static void destroy() {
        material = null;
        materialCoordinate = null;
        position = null;
        face = null;
    }
    public static void init() {
        if (initialized)
            return;
        initialized = true;
        helpScreen.init(
                Minecraft.getInstance(),
                MaterialPreviewerHelpScreen.WIDTH,
                MaterialPreviewerHelpScreen.HEIGHT
        );
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // Shut up
    public static boolean isActive() {
        return (
                material != null &&
                materialCoordinate != null &&
                position != null &&
                face != null
        );
    }
    public static boolean isHealthy() {
        return (
                (material == null) == (materialCoordinate == null) &&
                (materialCoordinate == null) == (position == null) &&
                (position == null) == (face == null)
        );
    }

    public static boolean renderHelpScreen(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Matrix4f projectionMatrix
    ) {
        if (!isActive())
            return false;

        assert face != null;
        assert position != null;

        Level level = Minecraft.getInstance().level;
        if (level == null)
            return false;
        BlockState state = level.getBlockState(position);
        if (!(state.getBlock() instanceof MaterialShape shape))
            return false;
        List<Vec3> vertices = face.getVertices().stream()
                .map(vertex -> shape.transformPointToWorld(state, vertex))
                .toList();
        double totalX = 0;
        double totalY = 0;
        double totalZ = 0;
        for (Vec3 vertex : vertices) {
            totalX += vertex.x;
            totalY += vertex.y;
            totalZ += vertex.z;
        }
        Vec3 faceCenter = new Vec3(
                totalX / vertices.size(),
                totalY / vertices.size(),
                totalZ / vertices.size()
        ).add(
                position.getX(),
                position.getY(),
                position.getZ()
        );

        boolean hovered = PhysRenderer.billboardAfterLevel(
                helpScreen,
                faceCenter,
                GuiConstants.HELP_SCREEN_OFFSET.multiply(1.5, 1.5, 1.5),
                poseStack,
                bufferSource,
                projectionMatrix,
                true,
                0.5
        );

        return true;
    }

    public static boolean render(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource
    ) {
        if (!MaterialPlacement.getAdjustOffset())
            return false;
        if (!isActive())
            return false;

        // Doing this shit because the isActive() check proves they're not null. So the compiler shuts the hell up.
        assert material != null;
        assert materialCoordinate != null;
        assert position != null;
        assert face != null;

        Level level = Minecraft.getInstance().level;
        if (level == null)
            return false;
        BlockState state = level.getBlockState(position);
        if (!(state.getBlock() instanceof MaterialShape shape))
            return false;
        List<Vec3> vertices = face.getVertices().stream()
                .map(vertex -> shape.transformPointToWorld(state, vertex))
                .toList();
        if (vertices.size() < 3) // Fuck it, another check to calm the soul. Amen.
            return false;

        if (material.getTexture(materialCoordinate) == null)
            return false;

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();
        Direction projection = Direction.getNearest(
                normal.x,
                normal.y * (1.0 - DIRECTION_BIAS),
                normal.z
        );

        double minU = Double.POSITIVE_INFINITY;
        double maxU = Double.NEGATIVE_INFINITY;
        double minV = Double.POSITIVE_INFINITY;
        double maxV = Double.NEGATIVE_INFINITY;

        for (Vec3 vertex : vertices) {
            double u = getTextureU(projection, vertex);
            double v = getTextureV(projection, vertex);

            minU = Math.min(minU, u);
            maxU = Math.max(maxU, u);
            minV = Math.min(minV, v);
            maxV = Math.max(maxV, v);
        }

        double uSize = maxU - minU;
        double vSize = maxV - minV;
        if (uSize <= 1.0e-7 || vSize <= 1.0e-7)
            return false;

        double[][] uv = new double[vertices.size()][2];

        for (int i = 0; i < vertices.size(); i++) {
            uv[i][0] = (getTextureU(projection, vertices.get(i)) - minU) / uSize;
            uv[i][1] = (getTextureV(projection, vertices.get(i)) - minV) / vSize;
        }

        int i0 = -1;
        int i1 = -1;
        int i2 = -1;

        for (int aIndex = 0; aIndex < vertices.size(); aIndex++)
            for (int bIndex = aIndex + 1; bIndex < vertices.size(); bIndex++)
                for (int cIndex = bIndex + 1; cIndex < vertices.size(); cIndex++) {
                    double du1 = uv[bIndex][0] - uv[aIndex][0];
                    double dv1 = uv[bIndex][1] - uv[aIndex][1];
                    double du2 = uv[cIndex][0] - uv[aIndex][0];
                    double dv2 = uv[cIndex][1] - uv[aIndex][1];

                    double determinant = du1 * dv2 - du2 * dv1;

                    if (Math.abs(determinant) > 1.0e-7) {
                        i0 = aIndex;
                        i1 = bIndex;
                        i2 = cIndex;
                        break;
                    }
                }

        if (i0 < 0)
            return false;

        Vec3 p0 = vertices.get(i0);
        Vec3 p1 = vertices.get(i1);
        Vec3 p2 = vertices.get(i2);

        double u0 = uv[i0][0];
        double v0 = uv[i0][1];

        double du1 = uv[i1][0] - u0;
        double dv1 = uv[i1][1] - v0;
        double du2 = uv[i2][0] - u0;
        double dv2 = uv[i2][1] - v0;

        double determinant = du1 * dv2 - du2 * dv1;

        Vec3 delta1 = p1.subtract(p0);
        Vec3 delta2 = p2.subtract(p0);

        Vec3 uAxis = delta1.scale(dv2)
                .subtract(delta2.scale(dv1))
                .scale(1.0 / determinant);

        Vec3 vAxis = delta2.scale(du1)
                .subtract(delta1.scale(du2))
                .scale(1.0 / determinant);

        Vec3[] plane = {
                getTexturePlanePoint(p0, u0, v0, uAxis, vAxis, 0.0, 0.0),
                getTexturePlanePoint(p0, u0, v0, uAxis, vAxis, 1.0, 0.0),
                getTexturePlanePoint(p0, u0, v0, uAxis, vAxis, 1.0, 1.0),
                getTexturePlanePoint(p0, u0, v0, uAxis, vAxis, 0.0, 1.0)
        };

        double[][] planeUV = {
                {0.0, 0.0},
                {1.0, 0.0},
                {1.0, 1.0},
                {0.0, 1.0}
        };

        Vec3 planeNormal = plane[1]
                .subtract(plane[0])
                .cross(plane[2].subtract(plane[0]))
                .normalize();

        if (planeNormal.dot(normal) < 0.0) {
            Vec3 tempPoint = plane[1];
            plane[1] = plane[3];
            plane[3] = tempPoint;

            double[] tempUV = planeUV[1];
            planeUV[1] = planeUV[3];
            planeUV[3] = tempUV;
        }

        poseStack.pushPose();

        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.translate(
                position.getX() - cameraPosition.x,
                position.getY() - cameraPosition.y,
                position.getZ() - cameraPosition.z
        );

        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

        for (var entry : material.getTextures().entrySet()) {
            Vector2i coordinate = entry.getKey();

            int deltaX = coordinate.x - materialCoordinate.x;
            int deltaY = coordinate.y - materialCoordinate.y;

            Vec3 offset = uAxis.scale(deltaX).add(vAxis.scale(deltaY));

            int alpha = coordinate.equals(materialCoordinate)
                    ? SELECTED_ALPHA
                    : UNSELECTED_ALPHA;

            ResourceLocation location = entry.getValue();

            TextureAtlasSprite entrySprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(location);

            for (int i = 0; i < plane.length; i++) {
                Vec3 point = plane[i].add(offset);

                putVertex(
                        consumer,
                        pose,
                        point,
                        normal,
                        planeUV[i][0],
                        planeUV[i][1],
                        alpha,
                        entrySprite
                );
            }
        }

        bufferSource.endBatch(RenderType.translucent());

        poseStack.popPose();

        return true;
    }

    private static Vec3 getTexturePlanePoint(
            Vec3 origin,
            double originU,
            double originV,
            Vec3 uAxis,
            Vec3 vAxis,
            double u,
            double v
    ) {
        return origin
                .add(uAxis.scale(u - originU))
                .add(vAxis.scale(v - originV));
    }

    private static void putVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            Vec3 vertex,
            Vec3 normal,
            double u,
            double v,
            int alpha,
            TextureAtlasSprite sprite
    ) {
        Vec3 point = vertex.add(normal.scale(EXTRUSION));

        float textureU = sprite.getU(u * 16.0);
        float textureV = sprite.getV(v * 16.0);

        consumer.vertex(
                pose.pose(),
                (float) point.x,
                (float) point.y,
                (float) point.z
        )
                .color(255, 255, 255, alpha)
                .uv(textureU, textureV)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(
                        pose.normal(),
                        (float) normal.x,
                        (float) normal.y,
                        (float) normal.z
                )
                .endVertex();
    }

    private static double getTextureU(Direction direction, Vec3 point) {
        return switch (direction) {
            case UP, DOWN, SOUTH -> point.x;
            case NORTH -> 1.0 - point.x;
            case WEST -> point.z;
            case EAST -> 1.0 - point.z;
        };
    }

    private static double getTextureV(Direction direction, Vec3 point) {
        return switch (direction) {
            case UP -> point.z;
            case DOWN -> 1.0 - point.z;
            case NORTH, SOUTH, WEST, EAST -> 1.0 - point.y;
        };
    }
}
