package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementLister;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class StackPanel implements Element, ElementLister {
    private int x;
    private int y;
    private @NotNull Alignment alignment;
    private int wideness;
    private int spacing;
    private int width;
    private int height;
    private int backgroundColor;
    private @NotNull List<@NotNull Element> elements;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Alignment> dynamicAlignment = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWideness = null;
    private @Nullable Supplier<@NotNull Integer> dynamicSpacing = null;
    private @Nullable Supplier<@NotNull Integer> dynamicBackgroundColor = null;
    private @Nullable Supplier<@NotNull List<@NotNull Element>> dynamicElements = null;

    public enum Alignment {
        HORIZONTAL,
        VERTICAL
    }

    public StackPanel(
            int x,
            int y,
            @NotNull Alignment alignment,
            int wideness,
            int spacing,
            int backgroundColor,
            @NotNull List<@NotNull Element> elements
    ) {
        this.x = x;
        this.y = y;
        this.alignment = alignment;
        this.wideness = wideness;
        this.spacing = spacing;
        this.backgroundColor = backgroundColor;
        this.elements = elements;

        layout();
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public @NotNull Alignment getAlignment() {
        return alignment;
    }
    public int getWideness() {
        return wideness;
    }
    public int getSpacing() {
        return spacing;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getBackgroundColor() {
        return backgroundColor;
    }
    public @NotNull List<@NotNull Element> getElements() {
        return elements;
    }

    public @Nullable Supplier<@NotNull Integer> getDynamicX() {
        return dynamicX;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicY() {
        return dynamicY;
    }
    public @Nullable Supplier<@NotNull Alignment> getDynamicAlignment() {
        return dynamicAlignment;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicWideness() {
        return dynamicWideness;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicSpacing() {
        return dynamicSpacing;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicBackgroundColor() {
        return dynamicBackgroundColor;
    }
    public @Nullable Supplier<@NotNull List<@NotNull Element>> getDynamicElements() {
        return dynamicElements;
    }

    public void setX(int x) {
        this.x = x;
        layout();
    }
    public void setY(int y) {
        this.y = y;
        layout();
    }
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        layout();
    }
    public void setWideness(int wideness) {
        this.wideness = wideness;
        layout();
    }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
        layout();
    }
    public void setWidth(int width) {}
    public void setHeight(int height) {}
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    public void setElements(List<Element> elements) {
        this.elements = elements;
        layout();
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        layout();
    }

    public void setDynamicX(@Nullable Supplier<@NotNull Integer> dynamicX) {
        this.dynamicX = dynamicX;
    }
    public void setDynamicY(@Nullable Supplier<@NotNull Integer> dynamicY) {
        this.dynamicY = dynamicY;
    }
    public void setDynamicAlignment(@Nullable Supplier<@NotNull Alignment> dynamicAlignment) {
        this.dynamicAlignment = dynamicAlignment;
    }
    public void setDynamicWideness(@Nullable Supplier<@NotNull Integer> dynamicWideness) {
        this.dynamicWideness = dynamicWideness;
    }
    public void setDynamicSpacing(@Nullable Supplier<@NotNull Integer> dynamicSpacing) {
        this.dynamicSpacing = dynamicSpacing;
    }
    public void setDynamicBackgroundColor(@Nullable Supplier<@NotNull Integer> dynamicBackgroundColor) {
        this.dynamicBackgroundColor = dynamicBackgroundColor;
    }
    public void setDynamicElements(@Nullable Supplier<@NotNull List<@NotNull Element>> dynamicElements) {
        this.dynamicElements = dynamicElements;
    }



    public void addElement(Element element) {
        elements.add(element);
        layout();
    }

    public boolean removeElement(int index) {
        if (index < 0 || index >= elements.size())
            return false;
        elements.remove(index);
        layout();
        return true;
    }

    public boolean removeElement(Element element) {
        if (!elements.remove(element))
            return false;
        layout();
        return true;
    }

    private void layout() {
        boolean vertical = alignment == Alignment.VERTICAL;

        int currentPosition = 0;

        for (Element element : elements) {
            if (vertical) {
                element.setX(x);
                element.setY(y + currentPosition);
                currentPosition += element.getHeight() + spacing;
            } else {
                element.setX(x + currentPosition);
                element.setY(y);
                currentPosition += element.getWidth() + spacing;
            }
        }

        if (vertical) {
            width = wideness;
            height = getStackHeight();
        } else {
            width = getStackWidth();
            height = wideness;
        }
    }

    private int getStackWidth() {
        if (elements.isEmpty())
            return 0;
        int width = spacing * (elements.size() - 1);
        for (Element element : elements)
            width += element.getWidth();
        return width;
    }

    private int getStackHeight() {
        if (elements.isEmpty())
            return 0;
        int height = spacing * (elements.size() - 1);
        for (Element element : elements)
            height += element.getHeight();
        return height;
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicAlignment != null) setAlignment(dynamicAlignment.get());
        if (dynamicWideness != null) setWideness(dynamicWideness.get());
        if (dynamicSpacing != null) setSpacing(dynamicSpacing.get());
        if (dynamicBackgroundColor != null) setBackgroundColor(dynamicBackgroundColor.get());
        if (dynamicElements != null) setElements(dynamicElements.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();
        layout();

        guiGraphics.fill(
                x,
                y,
                x + width,
                y + height,
                backgroundColor
        );

        for (Element element : elements)
            element.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
