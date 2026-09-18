package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;

import java.util.List;

public class PhysStackPanel implements PhysElement {
    private int x;
    private int y;
    private Alignment alignment;
    private int wideness;
    private int spacing;
    private int width;
    private int height;
    private int backgroundColor;
    private List<PhysElement> elements;

    public enum Alignment{
        HORIZONTAL,
        VERTICAL
    }

    public PhysStackPanel(
            int x,
            int y,
            Alignment alignment,
            int wideness,
            int spacing,
            List<PhysElement> elements
    ) {
        this.x = x;
        this.y = y;
        this.alignment = alignment;
        this.wideness = wideness;
        this.spacing = spacing;
        this.elements = elements;
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
    public List<PhysElement> getElements() {
        return List.copyOf(elements);
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    public void setWideness(int wideness) {
        this.wideness = wideness;
    }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
    public void setElements(List<PhysElement> elements) {
        this.elements = elements;
    }


    public void addElement(PhysElement element) {
        elements.add(element);
    }
    public boolean removeElement(int index) {
        if (index < 0 || index > elements.size() - 1)
            return false;
        elements.remove(index);
        return true;
    }
    public boolean removeElement(PhysElement element) {
        if (!elements.contains(element))
            return false;
        elements.remove(element);
        return true;
    }


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        boolean isVertical = alignment == Alignment.VERTICAL;

        width = isVertical
                ? wideness
                : getStackWidth();
        height = !isVertical
                ? wideness
                : getStackHeight();

        int currentPosition = 0;

        for (PhysElement element : elements) {
            int add;
            if (isVertical) {
                add = element.getHeight() + spacing;
                element.setX(x);
                element.setY(y + currentPosition);
            } else {
                add = element.getWidth() + spacing;
                element.setX(x + currentPosition);
                element.setY(y);
            }
            currentPosition += add;
            element.renderPhysical(guiGraphics);
        }
    }

    private int getStackWidth() {
        if (elements.isEmpty())
            return 0;
        int width = spacing * (elements.size() - 1);
        for (PhysElement element : elements)
            width += element.getWidth();
        return width;
    }
    private int getStackHeight() {
        if (elements.isEmpty())
            return 0;
        int height = spacing * (elements.size() - 1);
        for (PhysElement element : elements)
            height += element.getHeight();
        return height;
    }
}
