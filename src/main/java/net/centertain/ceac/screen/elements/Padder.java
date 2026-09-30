package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.ElementContainer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Padder implements Element, ElementContainer {
    private int x;
    private int y;
    private int width;
    private int height;

    private int padding;
    private Element element;

    public Padder(
            int width,
            int height,
            int padding,
            @NotNull Element element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.element = element;
    }

    public Padder(
            int x,
            int y,
            int width,
            int height,
            int padding,
            @NotNull Element element
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.element = element;
    }

    public Padder(
            @NotNull Element dimensionSupplier,
            int padding,
            @NotNull Element element
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.padding = padding;
        this.element = element;
    }

    public Padder(
            @NotNull Element positionSupplier,
            int width,
            int height,
            int padding,
            @NotNull Element element
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.element = element;
    }

    public int getX() {
        return x + padding;
    }
    public int getY() {
        return y + padding;
    }
    public int getRealX() {
        return x;
    }
    public int getRealY() {
        return y;
    }
    public int getWidth() {
        return Math.max(0, width - (2 * padding));
    }
    public int getHeight() {
        return Math.max(0, height - (2 * padding));
    }
    public int getRealWidth() {
        return width;
    }
    public int getRealHeight() {
        return height;
    }
    public int getPadding() {
        return padding;
    }
    public @NotNull Element getElement() {
        return element;
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
    public void setPadding(int padding) {
        this.padding = padding;
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
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
        if (element.getX() < x + padding)
            element.setX(x + padding);
        if (element.getY() < y + padding)
            element.setY(y + padding);
        if (element.getWidth() > getWidth())
            element.setWidth(getWidth());
        if (element.getHeight() > getHeight())
            element.setHeight(getHeight());

        element.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }
}
