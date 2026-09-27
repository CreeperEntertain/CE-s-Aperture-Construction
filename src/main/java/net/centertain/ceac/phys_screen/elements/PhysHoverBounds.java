package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.centertain.ceac.phys_screen.framework.element_types.PhysHoverable;
import org.jetbrains.annotations.NotNull;

public class PhysHoverBounds implements PhysElement, PhysHoverable {
    private boolean isHovered;

    private int x;
    private int y;
    private int width;
    private int height;

    public PhysHoverBounds(
            int width,
            int height
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
    }

    public PhysHoverBounds(
            @NotNull PhysElement positionSupplier,
            int width,
            int height
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
    }

    public PhysHoverBounds(@NotNull PhysElement dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
    }

    public boolean getIsHovered() {
        return isHovered;
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
    public @NotNull PhysDimensions getDimensions() {
        return new PhysDimensions(getX(), getY(), getWidth(), getHeight());
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
    public void setDimensions(@NotNull PhysElement dimensionSupplier) {
        setX(dimensionSupplier.getX());
        setY(dimensionSupplier.getY());
        setWidth(dimensionSupplier.getWidth());
        setHeight(dimensionSupplier.getHeight());
    }



    public void updatePhysicalHover(
            double mouseX,
            double mouseY
    ) {
        isHovered =
                mouseX >= getX() &&
                mouseY >= getY() &&
                mouseX < getX() + getWidth() &&
                mouseY < getY() + getHeight();
    }

    public void renderPhysical(PhysGuiGraphics guiGraphics) {}
}
