package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementLister;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Container implements Element, ElementLister {
    private int x;
    private int y;
    private int width;
    private int height;

    private List<Element> elements;

    private int backgroundColor;

    public Container(
            int x,
            int y,
            int width,
            int height,
            @NotNull List<Element> elements
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
            @NotNull List<Element> elements,
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
    public List<Element> getElements() {
        return elements;
    }
    public int getBackgroundColor() {
        return backgroundColor;
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
    public void setElements(@NotNull List<Element> elements) {
        this.elements = elements;
    }
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
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
}
