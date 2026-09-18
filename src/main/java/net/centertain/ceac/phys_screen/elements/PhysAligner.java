package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import org.jetbrains.annotations.NotNull;

public class PhysAligner implements PhysElement {
    private int x;
    private int y;
    private int width;
    private int height;
    private Alignment alignment;
    private PhysElement element;

    public enum Alignment{
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER
    }

    private final Runnable[] runnables = new Runnable[] {
            () -> alignTopLeft(this.element),
            () -> alignTopRight(this.element),
            () -> alignBottomLeft(this.element),
            () -> alignBottomRight(this.element),
            () -> alignLeft(this.element),
            () -> alignRight(this.element),
            () -> alignTop(this.element),
            () -> alignBottom(this.element),
            () -> alignCenter(this.element)
    };

    public PhysAligner(
            int width,
            int height,
            Alignment alignment,
            @NotNull PhysElement element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
    }
    public PhysAligner(
            int x,
            int y,
            int width,
            int height,
            Alignment alignment,
            @NotNull PhysElement element
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
    }
    public PhysAligner(
            @NotNull PhysElement dimensionSupplier,
            Alignment alignment,
            @NotNull PhysElement element
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.alignment = alignment;
        this.element = element;
    }
    public PhysAligner(
            @NotNull PhysElement positionSupplier,
            int width,
            int height,
            Alignment alignment,
            @NotNull PhysElement element
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
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
    public Alignment getAlignment() {
        return alignment;
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
    public void setDimensions(@NotNull PhysElement element) {
        this.x = element.getX();
        this.y = element.getY();
        this.width = element.getWidth();
        this.height = element.getHeight();
    }
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    public void setElement(@NotNull PhysElement element) {
        this.element = element;
    }


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        runnables[alignment.ordinal()].run(); // O(1) energy
        element.renderPhysical(guiGraphics);
    }

    private void alignTopLeft(PhysElement element) {
        element.setX(x);
        element.setY(y);
    }
    private void alignTopRight(PhysElement element) {
        element.setX(x + width - element.getWidth());
        element.setY(y);
    }
    private void alignBottomLeft(PhysElement element) {
        element.setX(x);
        element.setY(y + height - element.getHeight());
    }
    private void alignBottomRight(PhysElement element) {
        element.setX(x + width - element.getWidth());
        element.setY(y + height - element.getHeight());
    }
    private void alignLeft(PhysElement element) {
        element.setX(x);
        element.setY(y + (height - element.getHeight()) / 2);
    }
    private void alignRight(PhysElement element) {
        element.setX(x + width - element.getWidth());
        element.setY(y + (height - element.getHeight()) / 2);
    }
    private void alignTop(PhysElement element) {
        element.setX(x + (width - element.getWidth()) / 2);
        element.setY(y);
    }
    private void alignBottom(PhysElement element) {
        element.setX(x + (width - element.getWidth()) / 2);
        element.setY(y + height - element.getHeight());
    }
    private void alignCenter(PhysElement element) {
        element.setX(x + (width - element.getWidth()) / 2);
        element.setY(y + (height - element.getHeight()) / 2);
    }
}
