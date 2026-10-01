package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Image implements Element {
    private int x;
    private int y;
    private int width;
    private int height;
    private @NotNull ResourceLocation texture;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull ResourceLocation> dynamicTexture = null;

    public Image(
            int width,
            int height,
            @NotNull ResourceLocation texture
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Image(
            int x,
            int y,
            int width,
            int height,
            @NotNull ResourceLocation texture
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Image(
            @NotNull Element dimensionSupplier,
            @NotNull ResourceLocation texture
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.texture = texture;
    }

    public Image(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @NotNull ResourceLocation texture
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.texture = texture;
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
    public @NotNull ResourceLocation getTexture() {
        return texture;
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
    public @Nullable Supplier<@NotNull ResourceLocation> getDynamicTexture() {
        return dynamicTexture;
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
    public void setTexture(@NotNull ResourceLocation texture) {
        this.texture = texture;
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
    public void setDynamicTexture(@Nullable Supplier<@NotNull ResourceLocation> dynamicTexture) {
        this.dynamicTexture = dynamicTexture;
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicTexture != null) setTexture(dynamicTexture.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        guiGraphics.blit(
                texture,
                x,
                y,
                0,
                0,
                width,
                height,
                width,
                height
        );
    }
}
