package net.centertain.ceac.material;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;

public final class MaterialPreviewer {
    private static final double EXTRUSION = 0.002;

    private static @Nullable Material material;
    private static @Nullable Vector2i materialCoordinate;
    private static @Nullable BlockPos position;
    private static @Nullable MaterialShapeFace face;

    private MaterialPreviewer() {}

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
    }
    public static void destroy() {
        material = null;
        materialCoordinate = null;
        position = null;
        face = null;
    }

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

    public static void render(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource
    ) {
        if (!MaterialPlacement.getAdjustOffset())
            return;
        if (!isActive())
            return;

        // Doing this shit because the isActive() check proves they're not null. So the compiler shuts the hell up.
        assert material != null;
        assert materialCoordinate != null;
        assert position != null;
        assert face != null;

        List<Vec3> vertices = face.getVertices();
        if (vertices.size() < 3) // Fuck it, another check to calm the soul. Amen.
            return;

        ResourceLocation texture = material.getTexture(materialCoordinate);
        if (texture == null)
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

        Direction side = Direction.getNearest(normal.x, normal.y, normal.z);

        Vec3 uAxis;
        Vec3 vAxis;

        switch (side) {
            case UP -> {
                uAxis = new Vec3(1, 0, 0);
                vAxis = new Vec3(0, 0, 1);
            }
            case DOWN -> {
                uAxis = new Vec3(-1, 0, 0);
                vAxis = new Vec3(0, 0, 1);
            }
            case NORTH -> {
                uAxis = new Vec3(-1, 0, 0);
                vAxis = new Vec3(0, 1, 0);
            }
            case SOUTH -> {
                uAxis = new Vec3(1, 0, 0);
                vAxis = new Vec3(0, 1, 0);
            }
            case WEST -> {
                uAxis = new Vec3(0, 0, 1);
                vAxis = new Vec3(0, 1, 0);
            }
            case EAST -> {
                uAxis = new Vec3(0, 0, -1);
                vAxis = new Vec3(0, 1, 0);
            }
            default -> throw new AssertionError(side);
        }

        double minU = Double.POSITIVE_INFINITY;
        double maxU = Double.NEGATIVE_INFINITY;
        double minV = Double.POSITIVE_INFINITY;
        double maxV = Double.NEGATIVE_INFINITY;

        for (Vec3 vertex : vertices) {
            double u = vertex.dot(uAxis);
            double v = vertex.dot(vAxis);

            minU = Math.min(minU, u);
            maxU = Math.max(maxU, u);
            minV = Math.min(minV, v);
            maxV = Math.max(maxV, v);
        }

        double uSize = maxU - minU;
        double vSize = maxV - minV;
        if (uSize <= 1.0e-7 || vSize <= 1.0e-7)
            return;



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
                    uAxis, vAxis, minU, minV, uSize, vSize,
                    sprite
            );
            putVertex(
                    consumer,
                    pose,
                    vertices.get(i),
                    normal,
                    uAxis, vAxis, minU, minV, uSize, vSize,
                    sprite
            );
            putVertex(
                    consumer,
                    pose,
                    vertices.get(i + 1),
                    normal,
                    uAxis, vAxis, minU, minV, uSize, vSize,
                    sprite
            );
            putVertex(
                    consumer,
                    pose,
                    vertices.get(0),
                    normal, uAxis, vAxis, minU, minV, uSize, vSize,
                    sprite
            );
        }

        bufferSource.endBatch(RenderType.translucent());

        poseStack.popPose();
    }

    private static void putVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            Vec3 vertex,
            Vec3 normal,
            Vec3 uAxis,
            Vec3 vAxis,
            double minU,
            double minV,
            double uSize,
            double vSize,
            TextureAtlasSprite sprite
    ) {
        Vec3 point = vertex.add(normal.scale(EXTRUSION));

        double u = (vertex.dot(uAxis) - minU) / uSize;
        double v = (vertex.dot(vAxis) - minV) / vSize;

        float textureU = sprite.getU(u * 16.0);
        float textureV = sprite.getV(v * 16.0);

        consumer.vertex(
                pose.pose(),
                (float) point.x,
                (float) point.y,
                (float) point.z
        )
                .color(255, 255, 255, 128)
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
}
