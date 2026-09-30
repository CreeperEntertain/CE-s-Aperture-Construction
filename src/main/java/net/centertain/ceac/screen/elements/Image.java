package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Image implements Element {
    private int x;
    private int y;
    private int width;
    private int height;

    private ResourceLocation texture;

    public Image(
            int width,
            int height,
            ResourceLocation texture
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
            ResourceLocation texture
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public Image(
            @NotNull Element dimensionSupplier,
            ResourceLocation texture
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
            ResourceLocation texture
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
    public ResourceLocation getTexture() {
        return texture;
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
    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
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
