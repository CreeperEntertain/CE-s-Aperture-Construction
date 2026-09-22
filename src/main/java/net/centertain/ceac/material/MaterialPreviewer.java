package net.centertain.ceac.material;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.block.custom.MaterialShape;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.List;

public final class MaterialPreviewer {
    private static final double EXTRUSION = 0.002;
    private static final double DIRECTION_BIAS = 1.0e-6;

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

        ResourceLocation texture = material.getTexture(materialCoordinate);
        if (texture == null)
            return false;

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

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



        poseStack.pushPose();

        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.translate(
                position.getX() - cameraPosition.x,
                position.getY() - cameraPosition.y,
                position.getZ() - cameraPosition.z
        );

        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

        for (int i = 1; i < vertices.size() - 1; i++) {
            putVertex(
                    consumer,
                    pose,
                    vertices.get(0),
                    normal,
                    projection,
                    minU, minV, uSize, vSize,
                    sprite
            );
            putVertex(
                    consumer,
                    pose,
                    vertices.get(i),
                    normal,
                    projection,
                    minU, minV, uSize, vSize,
                    sprite
            );
            putVertex(
                    consumer,
                    pose,
                    vertices.get(i + 1),
                    normal,
                    projection,
                    minU, minV, uSize, vSize,
                    sprite
            );
            putVertex(
                    consumer,
                    pose,
                    vertices.get(0),
                    normal,
                    projection,
                    minU, minV, uSize, vSize,
                    sprite
            );
        }

        bufferSource.endBatch(RenderType.translucent());

        poseStack.popPose();

        return true;
    }

    private static void putVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            Vec3 vertex,
            Vec3 normal,
            Direction projection,
            double minU,
            double minV,
            double uSize,
            double vSize,
            TextureAtlasSprite sprite
    ) {
        Vec3 point = vertex.add(normal.scale(EXTRUSION));

        double u = (getTextureU(projection, vertex) - minU) / uSize;
        double v = (getTextureV(projection, vertex) - minV) / vSize;

        float textureU = sprite.getU(u * 16.0);
        float textureV = sprite.getV(v * 16.0);

        consumer.vertex(
                pose.pose(),
                (float) point.x,
                (float) point.y,
                (float) point.z
        )
                .color(255, 255, 255, 127)
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
            case DOWN, NORTH, SOUTH, WEST, EAST -> 1.0 - point.y;
        };
    }
}
