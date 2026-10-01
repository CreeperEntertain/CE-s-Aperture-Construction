package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementLister;
import net.centertain.ceac.screen.framework.element_types.HoverTransformer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class Container implements Element, ElementLister, HoverTransformer {
    private int x;
    private int y;
    private int width;
    private int height;
    private @NotNull List<@NotNull Element> elements;
    private int backgroundColor;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull List<@NotNull Element>> dynamicElements = null;
    private @Nullable Supplier<@NotNull Integer> dynamicBackgroundColor = null;

    public Container(
            int x,
            int y,
            int width,
            int height,
            @NotNull List<@NotNull Element> elements
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.elements = elements;
        this.backgroundColor = 0x00000000;
    }

    public Container(
            int x,
            int y,
            int width,
            int height,
            @NotNull List<@NotNull Element> elements,
            int backgroundColor
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.elements = elements;
        this.backgroundColor = backgroundColor;
    }

    public Container(
            int x,
            int y,
            int width,
            int height,
            @NotNull Element element
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.elements = List.of(element);
        this.backgroundColor = 0x00000000;
    }

    public Container(
            int x,
            int y,
            int width,
            int height,
            @NotNull Element element,
            int backgroundColor
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.elements = List.of(element);
        this.backgroundColor = backgroundColor;
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
    public @NotNull List<@NotNull Element> getElements() {
        return elements;
    }
    public int getBackgroundColor() {
        return backgroundColor;
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
    public @Nullable Supplier<@NotNull List<@NotNull Element>> getDynamicElements() {
        return dynamicElements;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicBackgroundColor() {
        return dynamicBackgroundColor;
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
    public void setElements(@NotNull List<@NotNull Element> elements) {
        this.elements = elements;
    }
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
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
    public void setDynamicElements(@Nullable Supplier<@NotNull List<@NotNull Element>> dynamicElements) {
        this.dynamicElements = dynamicElements;
    }
    public void setDynamicBackgroundColor(@Nullable Supplier<@NotNull Integer> dynamicBackgroundColor) {
        this.dynamicBackgroundColor = dynamicBackgroundColor;
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicElements != null) setElements(dynamicElements.get());
        if (dynamicBackgroundColor != null) setBackgroundColor(dynamicBackgroundColor.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        guiGraphics.fill(
                x,
                y,
                x + width,
                y + height,
                backgroundColor
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0);

        for (Element element : elements)
            element.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean isMouseOver(
            double mouseX,
            double mouseY
    ) {
        return true;
    }

    @Override
    public double transformMouseX(
            double mouseX,
            double mouseY
    ) {
        return mouseX - x;
    }

    @Override
    public double transformMouseY(
            double mouseX,
            double mouseY
    ) {
        return mouseY - y;
    }
}
