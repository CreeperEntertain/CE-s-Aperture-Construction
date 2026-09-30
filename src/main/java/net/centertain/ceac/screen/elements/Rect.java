package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Rect implements Element {
    private int x;
    private int y;
    private int width;
    private int height;

    private int fillColor;
    private int outlineColor;
    private int outlineWidth;

    public Rect(
            int width,
            int height,
            int fillColor
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = 0x00000000;
        this.outlineWidth = 0;
    }

    public Rect(
            int x,
            int y,
            int width,
            int height,
            int fillColor
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = 0x00000000;
        this.outlineWidth = 0;
    }

    public Rect(
            int x,
            int y,
            int width,
            int height,
            int fillColor,
            int outlineColor,
            int outlineWidth
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
    }

    public Rect(
            @NotNull Element dimensionSupplier,
            int fillColor
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.fillColor = fillColor;
        this.outlineColor = 0x00000000;
        this.outlineWidth = 0;
    }

    public Rect(
            @NotNull Element positionSupplier,
            int width,
            int height,
            int fillColor
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = 0x00000000;
        this.outlineWidth = 0;
    }

    public Rect(
            @NotNull Element positionSupplier,
            int width,
            int height,
            int fillColor,
            int outlineColor,
            int outlineWidth
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
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
    public int getFillColor() {
        return fillColor;
    }
    public int getOutlineColor() {
        return outlineColor;
    }
    public int getOutlineWidth() {
        return outlineWidth;
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
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }
    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }
    public void setOutlineWidth(int outlineWidth) {
        this.outlineWidth = outlineWidth;
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
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + height,
                fillColor
        );
        if (outlineWidth > 0)
            renderOutline(guiGraphics);
    }

    private void renderOutline(GuiGraphics guiGraphics) {
        guiGraphics.fill(
                x,
                y,
                x + Math.min(width, outlineWidth),
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + Math.min(height, outlineWidth),
                outlineColor
        );
        guiGraphics.fill(
                x + Math.max(0, width - outlineWidth),
                y,
                x + width,
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y + Math.max(0, height - outlineWidth),
                x + width,
                y + height,
                outlineColor
        );
    }
}
