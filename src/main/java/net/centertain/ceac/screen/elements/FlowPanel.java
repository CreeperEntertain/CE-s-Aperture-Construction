package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.ElementLister;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlowPanel implements Element, ElementLister, GuiEventListener {
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

    public FlowPanel(
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

    public FlowPanel(
            @NotNull Element positionSupplier,
            Alignment alignment,
            int wideness,
            int spacing,
            int backgroundColor,
            List<Element> elements
    ) {
        this(
                positionSupplier.getX(),
                positionSupplier.getY(),
                alignment,
                wideness,
                spacing,
                backgroundColor,
                elements
        );
    }

    public FlowPanel(
            Alignment alignment,
            int wideness,
            int spacing,
            int backgroundColor,
            List<Element> elements
    ) {
        this(
                0,
                0,
                alignment,
                wideness,
                spacing,
                backgroundColor,
                elements
        );
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
    public void setDimensions(@NotNull Element positionSupplier) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        layout();
    }


    public void addElement(
            @NotNull Element element
    ) {
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

    public boolean removeElement(
            @NotNull Element element
    ) {
        if (!elements.remove(element))
            return false;
        layout();
        return true;
    }

    private void layout() {
        if (alignment == Alignment.HORIZONTAL)
            layoutHorizontal();
        else
            layoutVertical();
    }

    private void layoutHorizontal() {
        width = wideness;
        if (elements.isEmpty()) {
            height = 0;
            return;
        }

        int currentX = x;
        int currentY = y;
        int rowHeight = 0;
        int totalHeight = 0;

        for (Element element : elements) {
            int elementWidth = element.getWidth();
            int elementHeight = element.getHeight();
            boolean hasElementsInRow = currentX > x;
            if (
                    hasElementsInRow &&
                    currentX + elementWidth > x + wideness
            ) {
                currentX = x;
                currentY += rowHeight + spacing;
                totalHeight += rowHeight + spacing;
                rowHeight = 0;
            }

            element.setX(currentX);
            element.setY(currentY);

            currentX += elementWidth + spacing;
            rowHeight = Math.max(rowHeight, elementHeight);
        }

        totalHeight += rowHeight;
        height = totalHeight;
    }

    private void layoutVertical() {
        height = wideness;
        if (elements.isEmpty()) {
            width = 0;
            return;
        }

        int currentX = x;
        int currentY = y;
        int columnWidth = 0;
        int totalWidth = 0;

        for (Element element : elements) {
            int elementWidth = element.getWidth();
            int elementHeight = element.getHeight();
            boolean hasElementsInColumn = currentY > y;
            if (
                    hasElementsInColumn &&
                    currentY + elementHeight > y + wideness
            ) {
                currentY = y;
                currentX += columnWidth + spacing;
                totalWidth += columnWidth + spacing;
                columnWidth = 0;
            }

            element.setX(currentX);
            element.setY(currentY);

            currentY += elementHeight + spacing;
            columnWidth = Math.max(columnWidth, elementWidth);
        }

        totalWidth += columnWidth;
        width = totalWidth;
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
            element.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            Element element = elements.get(i);
            if (!(element instanceof GuiEventListener listener))
                continue;
            if (
                    listener.isMouseOver(mouseX, mouseY) &&
                    listener.mouseClicked(mouseX, mouseY, button)
            )
                return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(
            double mouseX,
            double mouseY
    ) {
        return
                mouseX >= x &&
                mouseY >= y &&
                mouseX < x + width &&
                mouseY < y + height;
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }
}
