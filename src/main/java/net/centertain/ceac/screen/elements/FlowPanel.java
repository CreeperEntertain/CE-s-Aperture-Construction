package net.centertain.ceac.screen.elements;

import net.centertain.ceac.GuiConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlowPanel implements Renderable {
    private final List<? extends AbstractWidget> elements;
    private final int backgroundColor;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public FlowPanel(
            int x,
            int y,
            int width,
            List<? extends AbstractWidget> elements,
            int backgroundColor
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.elements = List.copyOf(elements);
        this.backgroundColor = backgroundColor;

        int currentX = x;
        int currentY = y;
        int rowHeight = 0;

        for (AbstractWidget element : this.elements) {
            int elementWidth = element.getWidth();
            int elementHeight = element.getHeight();

            if (currentX > x && currentX + elementWidth > x + width) {
                currentX = x;
                currentY += rowHeight + GuiConstants.ELEMENT_PADDING;
                rowHeight = 0;
            }

            element.setPosition(currentX, currentY);

            currentX += elementWidth + GuiConstants.ELEMENT_PADDING;
            rowHeight = Math.max(rowHeight, elementHeight);
        }

        this.height = this.elements.isEmpty() ? 0 : currentY + rowHeight - y;
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

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + height,
                backgroundColor
        );
        for (AbstractWidget element : elements)
            element.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
