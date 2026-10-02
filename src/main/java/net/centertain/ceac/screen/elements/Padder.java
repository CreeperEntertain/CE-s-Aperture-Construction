package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Padder implements Element, ElementContainer {
    private int x;
    private int y;
    private int width;
    private int height;
    private int padding;
    private @NotNull Element element;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull Integer> dynamicPadding = null;
    private @Nullable Supplier<@NotNull Element> dynamicElement = null;

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
        fitElement();
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
        fitElement();
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
        fitElement();
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
        fitElement();
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
    public @Nullable Supplier<@NotNull Integer> getDynamicPadding() {
        return dynamicPadding;
    }
    public @Nullable Supplier<@NotNull Element> getDynamicElement() {
        return dynamicElement;
    }

    public void setX(int x) {
        int delta = x - this.x;
        this.x = x;
        element.setX(element.getX() + delta);
        fitElement();
    }
    public void setY(int y) {
        int delta = y - this.y;
        this.y = y;
        element.setY(element.getY() + delta);
        fitElement();
    }
    public void setWidth(int width) {
        this.width = width;
        fitElement();
    }
    public void setHeight(int height) {
        this.height = height;
        fitElement();
    }
    public void setPadding(int padding) {
        this.padding = padding;
        fitElement();
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
        fitElement();
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        fitElement();
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
    public void setDynamicPadding(@Nullable Supplier<@NotNull Integer> dynamicPadding) {
        this.dynamicPadding = dynamicPadding;
    }
    public void setDynamicElement(@Nullable Supplier<@NotNull Element> dynamicElement) {
        this.dynamicElement = dynamicElement;
    }


    private void fitElement() {
        if (element.getX() < getX())
            element.setX(x + padding);
        if (element.getY() < getY())
            element.setY(y + padding);
        if (element.getWidth() > getWidth())
            element.setWidth(getWidth());
        if (element.getHeight() > getHeight())
            element.setHeight(getHeight());
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicPadding != null) setPadding(dynamicPadding.get());
        if (dynamicElement != null) setElement(dynamicElement.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();
        fitElement();

        element.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }
}
