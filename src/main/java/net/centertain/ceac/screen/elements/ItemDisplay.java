package net.centertain.ceac.screen.elements;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.centertain.ceac.screen.framework.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ItemDisplay implements Element {
    private int x;
    private int y;
    private int width;
    private int height;
    private @NotNull ItemStack stack;
    private boolean flatLighting;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull ItemStack> dynamicStack = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicFlatLighting = null;

    public ItemDisplay(
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

    public ItemDisplay(
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

    public ItemDisplay(
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

    public ItemDisplay(
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

    public @Nullable Supplier<@NotNull Integer> getDynamicX() {
        return dynamicX;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicY() {
        return dynamicY;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicWidth() {
        return dynamicWidth;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicHeight() {
        return dynamicHeight;
    }
    public @Nullable Supplier<@NotNull ItemStack> getDynamicStack() {
        return dynamicStack;
    }
    public @Nullable Supplier<@NotNull Boolean> getDynamicFlatLighting() {
        return dynamicFlatLighting;
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

    public void setDynamicX(@Nullable Supplier<@NotNull Integer> dynamicX) {
        this.dynamicX = dynamicX;
    }
    public void setDynamicY(@Nullable Supplier<@NotNull Integer> dynamicY) {
        this.dynamicY = dynamicY;
    }
    public void setDynamicWidth(@Nullable Supplier<@NotNull Integer> dynamicWidth) {
        this.dynamicWidth = dynamicWidth;
    }
    public void setDynamicHeight(@Nullable Supplier<@NotNull Integer> dynamicHeight) {
        this.dynamicHeight = dynamicHeight;
    }
    public void setDynamicStack(@Nullable Supplier<@NotNull ItemStack> dynamicStack) {
        this.dynamicStack = dynamicStack;
    }
    public void setDynamicFlatLighting(@Nullable Supplier<@NotNull Boolean> dynamicFlatLighting) {
        this.dynamicFlatLighting = dynamicFlatLighting;
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicStack != null) setStack(dynamicStack.get());
        if (dynamicFlatLighting != null) setFlatLighting(dynamicFlatLighting.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        if (stack.isEmpty())
            return;

        PoseStack poseStack = guiGraphics.pose();

        float scale = Math.min(width, height) / 16.0F;

        poseStack.pushPose();
        poseStack.translate(
                x + width / 2.0F,
                y + height / 2.0F,
                0.0F
        );
        poseStack.scale(
                scale,
                scale,
                scale
        );

        if (flatLighting)
            Lighting.setupForFlatItems();

        guiGraphics.renderItem(stack, -8, -8);

        if (flatLighting)
            Lighting.setupFor3DItems();

        poseStack.popPose();
    }
}