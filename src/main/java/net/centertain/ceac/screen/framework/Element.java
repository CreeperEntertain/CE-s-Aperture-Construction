package net.centertain.ceac.screen.framework;

import net.centertain.ceac.screen.elements.Dimensions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

public interface Element extends Renderable {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    default @NotNull Dimensions getDimensions() {
        return new Dimensions(
                getX(),
                getY(),
                getWidth(),
                getHeight()
        );
    }

    void setX(int x);
    void setY(int y);
    void setWidth(int width);
    void setHeight(int height);
    default void setDimensions(@NotNull Element element) {
        setX(element.getX());
        setY(element.getY());
        setWidth(element.getWidth());
        setHeight(element.getHeight());
    }

    @Override
    void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    );
}
