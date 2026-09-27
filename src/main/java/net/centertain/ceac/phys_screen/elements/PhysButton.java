package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.*;
import net.centertain.ceac.phys_screen.framework.element_types.PhysClickable;
import net.centertain.ceac.phys_screen.framework.element_types.PhysElementContainer;
import net.centertain.ceac.phys_screen.framework.element_types.PhysHoverable;
import net.centertain.ceac.phys_screen.framework.element_types.PhysReactable;
import org.jetbrains.annotations.NotNull;

public class PhysButton implements PhysElement, PhysHoverable, PhysClickable, PhysElementContainer {
    private boolean isHovered;

    private int x;
    private int y;
    private int width;
    private int height;
    private PhysElement element;
    private Runnable onClick;
    private int backgroundColor;
    private int outlineColor;

    public PhysButton(
            int x,
            int y,
            @NotNull PhysElement element,
            @NotNull Runnable onClick,
            int backgroundColor
    ) {
        this.x = x;
        this.y = y;
        this.width = element.getWidth();
        this.height = element.getHeight();
        this.element = element;
        this.onClick = onClick;
        this.backgroundColor = backgroundColor;
        this.outlineColor = 0x00000000;

        updateHoverables();
    }

    public PhysButton(
            int x,
            int y,
            @NotNull PhysElement element,
            @NotNull Runnable onClick,
            int backgroundColor,
            int outlineColor
    ) {
        this.x = x;
        this.y = y;
        this.width = element.getWidth();
        this.height = element.getHeight();
        this.element = element;
        this.onClick = onClick;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;

        updateHoverables();
    }

    public PhysButton(
            @NotNull PhysElement positionSupplier,
            @NotNull PhysElement element,
            @NotNull Runnable onClick,
            int backgroundColor
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = element.getWidth();
        this.height = element.getHeight();
        this.element = element;
        this.onClick = onClick;
        this.backgroundColor = backgroundColor;
        this.outlineColor = 0x00000000;

        updateHoverables();
    }

    public PhysButton(
            @NotNull PhysElement positionSupplier,
            @NotNull PhysElement element,
            @NotNull Runnable onClick,
            int backgroundColor,
            int outlineColor
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = element.getWidth();
        this.height = element.getHeight();
        this.element = element;
        this.onClick = onClick;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;

        updateHoverables();
    }

    private void updateHoverables() {
        if (element instanceof PhysReactable reactable)
            reactable.parentHover(this::getIsHovered);
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
    public @NotNull PhysElement getElement() {
        return element;
    }
    public @NotNull Runnable getOnClick() {
        return onClick;
    }
    public int getBackgroundColor() {
        return backgroundColor;
    }
    public int getOutlineColor() {
        return outlineColor;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {}
    public void setHeight(int height) {}
    public void setDimensions(@NotNull PhysElement dimensionSupplier) {
        setX(dimensionSupplier.getX());
        setY(dimensionSupplier.getY());
        setWidth(dimensionSupplier.getWidth());
        setHeight(dimensionSupplier.getHeight());
    }
    public void setElement(@NotNull PhysElement element) {
        width = element.getWidth();
        height = element.getHeight();
        this.element = element;
    }
    public void setOnClick(@NotNull Runnable onClick) {
        this.onClick = onClick;
    }
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
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

    public boolean registerClick() {
        if (!isHovered)
            return false;
        onClick.run();
        return true;
    }


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        renderBackground(guiGraphics);

        element.setX(x);
        element.setY(y);
        element.renderPhysical(guiGraphics);

        renderHoverOutline(guiGraphics);
    }

    private void renderBackground(PhysGuiGraphics guiGraphics) {
        guiGraphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                backgroundColor
        );
    }

    private void renderHoverOutline(PhysGuiGraphics guiGraphics) {
        if (!isHovered)
            return;

        guiGraphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + 1,
                outlineColor
        );
        guiGraphics.fill(
                getX(),
                getY() + getHeight() - 1,
                getX() + getWidth(),
                getY() + getHeight(),
                outlineColor
        );
        guiGraphics.fill(
                getX(),
                getY(),
                getX() + 1,
                getY() + getHeight(),
                outlineColor
        );
        guiGraphics.fill(
                getX() + getWidth() - 1,
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                outlineColor
        );
    }
}
