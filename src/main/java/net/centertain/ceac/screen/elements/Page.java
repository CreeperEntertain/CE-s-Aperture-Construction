package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.HoverTransformer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Page implements Element, ElementContainer, HoverTransformer {
    private int x;
    private int y;
    private int width;
    private int height;
    private @NotNull String name;
    private @NotNull Element element;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull String> dynamicName = null;
    private @Nullable Supplier<@NotNull Element> dynamicElement = null;

    public Page(
            @NotNull String name,
            int width,
            int height,
            @NotNull Element element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.name = name;
        this.element = element;

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
    public @Nullable Supplier<@NotNull String> getDynamicName() {
        return dynamicName;
    }
    public @Nullable Supplier<@NotNull Element> getDynamicElement() {
        return dynamicElement;
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
    public void setDynamicName(@Nullable Supplier<@NotNull String> dynamicName) {
        this.dynamicName = dynamicName;
    }
    public void setDynamicElement(@Nullable Supplier<@NotNull Element> dynamicElement) {
        this.dynamicElement = dynamicElement;
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicName != null) setName(dynamicName.get());
        if (dynamicElement != null) setElement(dynamicElement.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

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
    public boolean canTransformMouse(
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
