package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.HoverTransformer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Page implements Element, ElementContainer, HoverTransformer {
    private int x;
    private int y;
    private int width;
    private int height;

    private String name;
    private Element element;

    public Page(
            @NotNull String name,
            @NotNull Element element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = element.getWidth();
        this.height = element.getHeight();
        this.name = name;
        this.element = element;

        element.setX(x);
        element.setY(y);
        element.setWidth(width);
        element.setHeight(height);
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
    public @NotNull String getName() {
        return name;
    }
    public @NotNull Element getElement() {
        return element;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
        element.setX(x);
        element.setWidth(width);
    }
    public void setHeight(int height) {
        this.height = height;
        element.setY(y);
        element.setHeight(height);
    }
    public void setName(@NotNull String name) {
        this.name = name;
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
        element.setX(x);
        element.setY(y);
        element.setWidth(width);
        element.setHeight(height);
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        setWidth(dimensionSupplier.getWidth());
        setHeight(dimensionSupplier.getHeight());
    }


    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        element.setX(x);
        element.setY(y);
        element.setWidth(width);
        element.setHeight(height);

        guiGraphics.enableScissor(
                x,
                y,
                x + width,
                y + height
        );

        element.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        guiGraphics.disableScissor();
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
    public double transformMouseX(
            double mouseX,
            double mouseY
    ) {
        return mouseX;
    }

    @Override
    public double transformMouseY(
            double mouseX,
            double mouseY
    ) {
        return mouseY;
    }
}
