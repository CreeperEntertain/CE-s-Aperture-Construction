package net.centertain.ceac.screen.elements;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import net.centertain.ceac.screen.framework.Element;
import org.jetbrains.annotations.NotNull;

public class ModelDisplay implements Element {
    private int x;
    private int y;
    private int width;
    private int height;

    private ItemStack stack;

    private boolean flatLighting;

    public ModelDisplay(
            int width,
            int height,
            @NotNull ItemStack stack,
            boolean flatLighting
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.stack = stack;
        this.flatLighting = flatLighting;
    }

    public ModelDisplay(
            int x,
            int y,
            int width,
            int height,
            @NotNull ItemStack stack,
            boolean flatLighting
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.stack = stack;
        this.flatLighting = flatLighting;
    }

    public ModelDisplay(
            @NotNull Element dimensionSupplier,
            @NotNull ItemStack stack,
            boolean flatLighting
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.stack = stack;
        this.flatLighting = flatLighting;
    }

    public ModelDisplay(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @NotNull ItemStack stack,
            boolean flatLighting
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.stack = stack;
        this.flatLighting = flatLighting;
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
    public @NotNull ItemStack getStack() {
        return stack;
    }
    public boolean getFlatLighting() {
        return flatLighting;
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
    public void setStack(@NotNull ItemStack stack) {
        this.stack = stack;
    }
    public void setFlatLighting(boolean flatLighting) {
        this.flatLighting = flatLighting;
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
        if (stack.isEmpty())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BakedModel model = itemRenderer.getModel(
                stack,
                minecraft.level,
                null,
                0
        );

        PoseStack poseStack = guiGraphics.pose();

        float scale = Math.min(width, height);

        poseStack.pushPose();
        poseStack.translate(
                x + width / 2.0F,
                y + height / 2.0F,
                100.0F
        );
        poseStack.scale(
                scale,
                -scale,
                scale
        );

        if (flatLighting)
            Lighting.setupForFlatItems();
        else
            Lighting.setupFor3DItems();

        itemRenderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                poseStack,
                guiGraphics.bufferSource(),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );

        guiGraphics.flush();

        Lighting.setupFor3DItems();

        poseStack.popPose();
    }
}