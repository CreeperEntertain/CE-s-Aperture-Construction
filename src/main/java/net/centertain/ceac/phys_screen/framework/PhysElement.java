package net.centertain.ceac.phys_screen.framework;

public interface PhysElement {
    void renderPhysical(PhysGuiGraphics guiGraphics);

    int getX();
    int getY();
    int getWidth();
    int getHeight();

    void setX(int x);
    void setY(int y);
}
