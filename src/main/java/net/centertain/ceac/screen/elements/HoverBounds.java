package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.Hoverable;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class HoverBounds implements Element, Hoverable {
    private boolean isHovered;
    private int x;
    private int y;
    private int width;
    private int height;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;

    public HoverBounds(
            int width,
            int height
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
    }

    public HoverBounds(
            @NotNull Element positionSupplier,
            int width,
            int height
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
    }

    public HoverBounds(
            @NotNull Element dimensionSupplier
    ) {
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

    public @Nullable Supplier<@NotNull Integer> getDynamicX() {
        return dynamicX;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicY() {
        return dynamicY;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicWidth() {
        return dynamicWidth;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicHeight() {
        return dynamicHeight;
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
    public void setDimensions(@NotNull Element dimensionSupplier) {
        setX(dimensionSupplier.getX());
        setY(dimensionSupplier.getY());
        setWidth(dimensionSupplier.getWidth());
        setHeight(dimensionSupplier.getHeight());
    }

    public void setDynamicX(@Nullable Supplier<@NotNull Integer> dynamicX) {
        this.dynamicX = dynamicX;
    }
    public void setDynamicY(@Nullable Supplier<@NotNull Integer> dynamicY) {
        this.dynamicY = dynamicY;
    }
    public void setDynamicWidth(@Nullable Supplier<@NotNull Integer> dynamicWidth) {
        this.dynamicWidth = dynamicWidth;
    }
    public void setDynamicHeight(@Nullable Supplier<@NotNull Integer> dynamicHeight) {
        this.dynamicHeight = dynamicHeight;
    }


    public void updateHover(
            double mouseX,
            double mouseY
    ) {
        isHovered =
                mouseX >= getX() &&
                mouseY >= getY() &&
                mouseX < getX() + getWidth() &&
                mouseY < getY() + getHeight();
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();
    }
}
