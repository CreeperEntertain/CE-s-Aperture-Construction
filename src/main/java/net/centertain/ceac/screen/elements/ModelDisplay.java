package net.centertain.ceac.screen.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
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
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(
                x + width / 2.0,
                y + height / 2.0,
                100.0
        );
        poseStack.scale(
                (float) (16.0 * scale.x),
                (float) (-16.0 * scale.y),
                (float) (16.0 * scale.z)
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
        itemRenderer.render(
                ItemStack.EMPTY,
                ItemDisplayContext.GUI,
                false,
                poseStack,
                guiGraphics.bufferSource(),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        guiGraphics.flush();
        poseStack.popPose();
    }
}
