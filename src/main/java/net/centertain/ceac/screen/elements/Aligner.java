package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Aligner implements Element, ElementContainer {
    private int x;
    private int y;
    private int width;
    private int height;

    private Alignment alignment;
    private Element element;

    public enum Alignment {
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
            () -> alignTopLeft(element),
            () -> alignTopRight(element),
            () -> alignBottomLeft(element),
            () -> alignBottomRight(element),
            () -> alignLeft(element),
            () -> alignRight(element),
            () -> alignTop(element),
            () -> alignBottom(element),
            () -> alignCenter(element)
    };

    public Aligner(
            int width,
            int height,
            Alignment alignment,
            @NotNull Element element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
        runnables[alignment.ordinal()].run();
    }

    public Aligner(
            int x,
            int y,
            int width,
            int height,
            Alignment alignment,
            @NotNull Element element
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
        runnables[alignment.ordinal()].run();
    }

    public Aligner(
            @NotNull Element dimensionSupplier,
            Alignment alignment,
            @NotNull Element element
    ) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        this.alignment = alignment;
        this.element = element;
        runnables[alignment.ordinal()].run();
    }

    public Aligner(
            @NotNull Element positionSupplier,
            int width,
            int height,
            Alignment alignment,
            @NotNull Element element
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
        runnables[alignment.ordinal()].run();
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
    public @NotNull Element getElement() {
        return element;
    }

    public int getLeftSpan() {
        return element.getX() - x;
    }
    public int getTopSpan() {
        return element.getY() - y;
    }
    public int getRightSpan() {
        return (width - element.getWidth()) - getLeftSpan();
    }
    public int getBottomSpan() {
        return (height - element.getHeight() - getTopSpan());
    }

    public void setX(int x) {
        this.x = x;
        runnables[alignment.ordinal()].run();
    }
    public void setY(int y) {
        this.y = y;
        runnables[alignment.ordinal()].run();
    }
    public void setWidth(int width) {
        this.width = width;
        runnables[alignment.ordinal()].run();
    }
    public void setHeight(int height) {
        this.height = height;
        runnables[alignment.ordinal()].run();
    }
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        runnables[alignment.ordinal()].run();
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
        runnables[alignment.ordinal()].run();
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        runnables[alignment.ordinal()].run();
    }


    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        element.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void alignTopLeft(Element element) {
        element.setX(x);
        element.setY(y);
    }
    private void alignTopRight(Element element) {
        element.setX(x + width - element.getWidth());
        element.setY(y);
    }
    private void alignBottomLeft(Element element) {
        element.setX(x);
        element.setY(y + height - element.getHeight());
    }
    private void alignBottomRight(Element element) {
        element.setX(x + width - element.getWidth());
        element.setY(y + height - element.getHeight());
    }
    private void alignLeft(Element element) {
        element.setX(x);
        element.setY(y + (height - element.getHeight()) / 2);
    }
    private void alignRight(Element element) {
        element.setX(x + width - element.getWidth());
        element.setY(y + (height - element.getHeight()) / 2);
    }
    private void alignTop(Element element) {
        element.setX(x + (width - element.getWidth()) / 2);
        element.setY(y);
    }
    private void alignBottom(Element element) {
        element.setX(x + (width - element.getWidth()) / 2);
        element.setY(y + height - element.getHeight());
    }
    private void alignCenter(Element element) {
        element.setX(x + (width - element.getWidth()) / 2);
        element.setY(y + (height - element.getHeight()) / 2);
    }
}
