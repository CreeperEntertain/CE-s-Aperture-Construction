package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class PhysPadder implements PhysElement {
    private int x;
    private int y;
    private int width;
    private int height;
    private int padding;
    private PhysElement element;

    public PhysPadder(
            int width,
            int height,
            int padding,
            @NotNull PhysElement element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.element = element;
    }
    public PhysPadder(
            int x,
            int y,
            int width,
            int height,
            int padding,
            @NotNull PhysElement element
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.element = element;
    }
    public PhysPadder(
            @NotNull PhysElement dimensionSupplier,
            int padding,
            @NotNull PhysElement element
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.padding = padding;
        this.element = element;
    }
    public PhysPadder(
            @NotNull PhysElement positionSupplier,
            int width,
            int height,
            int padding,
            @NotNull PhysElement element
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
    public PhysElement getElement() {
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
    public void setElement(@NotNull PhysElement element) {
        this.element = element;
    }

    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        if (element.getX() < x + padding)
            element.setX(x + padding);
        if (element.getY() < y + padding)
            element.setY(y + padding);
        if (element.getWidth() > getWidth())
            element.setWidth(getWidth());
        if (element.getHeight() > getHeight())
            element.setHeight(getHeight());

        element.renderPhysical(guiGraphics);
    }
}
