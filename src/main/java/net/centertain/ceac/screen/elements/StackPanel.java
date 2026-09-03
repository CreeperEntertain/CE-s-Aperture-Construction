package net.centertain.ceac.screen.elements;

import net.centertain.ceac.GuiConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackPanel implements Renderable {
    private final List<? extends AbstractWidget> elements;
    private final int backgroundColor;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public StackPanel(
            int x,
            int y,
            int width,
            List<? extends AbstractWidget> elements,
            int backgroundColor
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.elements = elements;

        int offset = 0;

        for (AbstractWidget element : this.elements) {
            element.setPosition(x, y + offset);
            offset += element.getHeight() + GuiConstants.ELEMENT_PADDING;
        }
        if (!this.elements.isEmpty())
            offset -= GuiConstants.ELEMENT_PADDING;

        this.height = offset;
        this.backgroundColor = backgroundColor;
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
