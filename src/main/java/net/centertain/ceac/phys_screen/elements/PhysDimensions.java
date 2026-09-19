package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;

public class PhysDimensions implements PhysElement {
    private int x;
    private int y;
    private int width;
    private int height;

    public PhysDimensions(
            int x,
            int y,
            int width,
            int height
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public PhysDimensions(
            int width,
            int height
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
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


    public void renderPhysical(PhysGuiGraphics guiGraphics) {}
}
