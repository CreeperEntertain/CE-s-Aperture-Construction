package net.centertain.ceac.phys_screen.framework;

import net.centertain.ceac.phys_screen.elements.PhysDimensions;
import org.jetbrains.annotations.NotNull;

public interface PhysElement {
    void renderPhysical(PhysGuiGraphics guiGraphics);

    int getX();
    int getY();
    int getWidth();
    int getHeight();
    PhysDimensions getDimensions();

    void setX(int x);
    void setY(int y);
    void setWidth(int width);
    void setHeight(int height);
    void setDimensions(@NotNull PhysElement dimensionSupplier);
}
