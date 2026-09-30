package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Empty implements Element {
    public Empty() {}

    public int getX() {
        return 0;
    }
    public int getY() {
        return 0;
    }
    public int getWidth() {
        return 0;
    }
    public int getHeight() {
        return 0;
    }

    public void setX(int x) {}
    public void setY(int y) {}
    public void setWidth(int width) {}
    public void setHeight(int height) {}

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {}
}
