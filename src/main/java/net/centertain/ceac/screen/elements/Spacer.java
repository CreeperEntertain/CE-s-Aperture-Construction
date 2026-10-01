package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class Spacer implements Element {
    private int width;
    private int height;

    public Spacer(
            int width,
            int height
    ) {
        this.width = width;
        this.height = height;
    }

    public Spacer(
            @NotNull Element scaleSupplier
    ) {
        this.width = scaleSupplier.getWidth();
        this.height = scaleSupplier.getHeight();
    }

    public int getX() {
        return 0;
    }
    public int getY() {
        return 0;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void setX(int x) {}
    public void setY(int y) {}
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }


    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {}
}
