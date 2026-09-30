package net.centertain.ceac.screen.elements;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.renderable.BakedModelRenderable;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class ModelDisplay implements Element {
    private int x;
    private int y;
    private int width;
    private int height;

    private BakedModel model;

    private Vec3 rotation;
    private Vec3 scale;

    public ModelDisplay(
            int width,
            int height,
            @NotNull BakedModel model,
            @NotNull Vec3 rotation,
            @NotNull Vec3 scale
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
    }

    public ModelDisplay(
            int x,
            int y,
            int width,
            int height,
            @NotNull BakedModel model,
            @NotNull Vec3 rotation,
            @NotNull Vec3 scale
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
    }

    public ModelDisplay(
            @NotNull Element dimensionSupplier,
            @NotNull BakedModel model,
            @NotNull Vec3 rotation,
            @NotNull Vec3 scale
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
    }

    public ModelDisplay(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @NotNull BakedModel model,
            @NotNull Vec3 rotation,
            @NotNull Vec3 scale
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public @NotNull BakedModel getModel() {
        return model;
    }
    public @NotNull Vec3 getRotation() {
        return rotation;
    }
    public @NotNull Vec3 getScale() {
        return scale;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setModel(@NotNull BakedModel model) {
        this.model = model;
    }
    public void setRotation(@NotNull Vec3 rotation) {
        this.rotation = rotation;
    }
    public void setScale(@NotNull Vec3 scale) {
        this.scale = scale;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
    }


    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        PoseStack poseStack = guiGraphics.pose();
        ModelBounds bounds = getModelBounds();

        double fit = Math.min(
                width / bounds.width(),
                height / bounds.height()
        );

        poseStack.pushPose();
        poseStack.translate(
                x + width / 2.0,
                y + height / 2.0,
                100.0
        );
        poseStack.mulPose(new Quaternionf(
                (float) Math.toRadians(rotation.z),
                0.0f,
                0.0f,
                1.0f
        ));
        poseStack.mulPose(new Quaternionf(
                (float) Math.toRadians(rotation.y),
                0.0f,
                1.0f,
                0.0f
        ));
        poseStack.mulPose(new Quaternionf(
                (float) Math.toRadians(rotation.x),
                1.0f,
                0.0f,
                0.0f
        ));
        poseStack.scale(
                (float) (fit * scale.x()),
                (float) (-fit * scale.y()),
                (float) (fit * scale.z())
        );
        poseStack.translate(
                -bounds.center().x,
                -bounds.center().y,
                -bounds.center().z
        );

        BakedModelRenderable.of(model).render(
                poseStack,
                guiGraphics.bufferSource(),
                RenderType::entityTranslucent,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                partialTick,
                new BakedModelRenderable.Context(ModelData.EMPTY)
        );

        guiGraphics.flush();
        poseStack.popPose();
    }

    private ModelBounds getModelBounds() {
        RandomSource random = RandomSource.create(42);

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Direction direction : Direction.values()) {
            random.setSeed(42);

            for (BakedQuad quad : model.getQuads(
                    null,
                    direction,
                    random,
                    ModelData.EMPTY,
                    null
            )) {
                int[] vertices = quad.getVertices();

                for (int i = 0; i < 4; i++) {
                    int offset = i * DefaultVertexFormat.BLOCK.getIntegerSize();

                    double vertexX = Float.intBitsToFloat(vertices[offset]);
                    double vertexY = Float.intBitsToFloat(vertices[offset + 1]);
                    double vertexZ = Float.intBitsToFloat(vertices[offset + 2]);

                    minX = Math.min(minX, vertexX);
                    minY = Math.min(minY, vertexY);
                    minZ = Math.min(minZ, vertexZ);
                    maxX = Math.max(maxX, vertexX);
                    maxY = Math.max(maxY, vertexY);
                    maxZ = Math.max(maxZ, vertexZ);
                }
            }
        }

        random.setSeed(42);

        for (BakedQuad quad : model.getQuads(
                null,
                null,
                random,
                ModelData.EMPTY,
                null
        )) {
            int[] vertices = quad.getVertices();

            for (int i = 0; i < 4; i++) {
                int offset = i * DefaultVertexFormat.BLOCK.getIntegerSize();

                double vertexX = Float.intBitsToFloat(vertices[offset]);
                double vertexY = Float.intBitsToFloat(vertices[offset + 1]);
                double vertexZ = Float.intBitsToFloat(vertices[offset + 2]);

                minX = Math.min(minX, vertexX);
                minY = Math.min(minY, vertexY);
                minZ = Math.min(minZ, vertexZ);
                maxX = Math.max(maxX, vertexX);
                maxY = Math.max(maxY, vertexY);
                maxZ = Math.max(maxZ, vertexZ);
            }
        }

        Vec3 center = new Vec3(
                (minX + maxX) / 2.0,
                (minY + maxY) / 2.0,
                (minZ + maxZ) / 2.0
        );

        return new ModelBounds(
                center,
                maxX - minX,
                maxY - minY
        );
    }

    private record ModelBounds(
            Vec3 center,
            double width,
            double height
    ) {}
}
