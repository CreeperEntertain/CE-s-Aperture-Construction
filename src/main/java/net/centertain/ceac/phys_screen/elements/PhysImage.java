package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PhysImage implements PhysElement {
    private int x;
    private int y;
    private int width;
    private int height;
    private ResourceLocation texture;

    public PhysImage(
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
    public PhysImage(
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
    public PhysImage(
            @NotNull PhysElement dimensionSupplier,
            ResourceLocation texture
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.texture = texture;
    }
    public PhysImage(
            @NotNull PhysElement positionSupplier,
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


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        guiGraphics.blit(
                texture,
                x,
                y,
                width,
                height
        );
    }
}
