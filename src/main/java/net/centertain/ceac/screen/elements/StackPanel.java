package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementLister;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackPanel implements Element, ElementLister {
    private int x;
    private int y;

    private Alignment alignment;
    private int wideness;
    private int spacing;

    private int width;
    private int height;

    private int backgroundColor;

    private List<Element> elements;

    public enum Alignment {
        HORIZONTAL,
        VERTICAL
    }

    public StackPanel(
            int x,
            int y,
            Alignment alignment,
            int wideness,
            int spacing,
            int backgroundColor,
            List<Element> elements
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
    public Alignment getAlignment() {
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
    public List<Element> getElements() {
        return elements;
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

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
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
