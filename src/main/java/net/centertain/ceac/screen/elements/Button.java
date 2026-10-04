package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.Hoverable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Button implements Element, ElementContainer, Hoverable, GuiEventListener {
    private int x;
    private int y;
    private int width;
    private int height;
    private Element element;
    private Runnable onPress;
    private int backgroundColor;
    private int outlineColor;
    private boolean isHovered;
    private boolean isFocused;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull Element> dynamicElement = null;
    private @Nullable Supplier<@NotNull Runnable> dynamicOnPress = null;
    private @Nullable Supplier<@NotNull Integer> dynamicBackgroundColor = null;
    private @Nullable Supplier<@NotNull Integer> dynamicOutlineColor = null;

    public Button(
            int x,
            int y,
            @NotNull Element element,
            @NotNull Runnable onPress,
            int backgroundColor,
            int outlineColor
    ) {
        this.x = x;
        this.y = y;
        this.element = element;
        this.onPress = onPress;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;

        this.width = element instanceof Padder padder
                ? padder.getRealWidth()
                : element.getWidth();
        this.height = element instanceof Padder padder
                ? padder.getRealHeight()
                : element.getHeight();
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
    public @NotNull Element getElement() {
        return element;
    }
    public @NotNull Runnable getOnPress() {
        return onPress;
    }
    public int getBackgroundColor() {
        return backgroundColor;
    }
    public int getOutlineColor() {
        return outlineColor;
    }
    public boolean getIsHovered() {
        return isHovered;
    }
    public boolean getIsFocused() {
        return isFocused;
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
    public @Nullable Supplier<@NotNull Element> getDynamicElement() {
        return dynamicElement;
    }
    public @Nullable Supplier<@NotNull Runnable> getDynamicOnPress() {
        return dynamicOnPress;
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
    public void setElement(@NotNull Element element) {
        this.element = element;
    }
    public void setOnPress(@NotNull Runnable onPress) {
        this.onPress = onPress;
    }
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = element.getX();
        this.y = element.getY();
        this.width = element.getWidth();
        this.height = element.getHeight();
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
    public void setDynamicElement(@Nullable Supplier<@NotNull Element> dynamicElement) {
        this.dynamicElement = dynamicElement;
    }
    public void setDynamicOnPress(@Nullable Supplier<@NotNull Runnable> dynamicOnPress) {
        this.dynamicOnPress = dynamicOnPress;
    }
    public void setDynamicBackgroundColor(@Nullable Supplier<@NotNull Integer> dynamicBackgroundColor) {
        this.dynamicBackgroundColor = dynamicBackgroundColor;
    }
    public void setDynamicOutlineColor(@Nullable Supplier<@NotNull Integer> dynamicOutlineColor) {
        this.dynamicOutlineColor = dynamicOutlineColor;
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicElement != null) setElement(dynamicElement.get());
        if (dynamicOnPress != null) setOnPress(dynamicOnPress.get());
        if (dynamicBackgroundColor != null) setBackgroundColor(dynamicBackgroundColor.get());
        if (dynamicOutlineColor != null) setOutlineColor(dynamicOutlineColor.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        guiGraphics.fill(
                x,
                y,
                x + width,
                y + height,
                backgroundColor
        );

        element.setX(x);
        element.setY(y);
        element.render(guiGraphics, mouseX, mouseY, partialTick);

        if (element instanceof Padder padder) {
            width = padder.getRealWidth();
            height = padder.getRealHeight();
        } else {
            width = element.getWidth();
            height = element.getHeight();
        }

        if (!isHovered)
            return;

        guiGraphics.fill(
                x,
                y,
                x + width,
                y + 1,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y + height - 1,
                x + width,
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y,
                x + 1,
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x + width - 1,
                y,
                x + width,
                y + height,
                outlineColor
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!isHovered)
            return false;
        onPress.run();
        return true;
    }

    @Override
    public boolean isMouseOver(
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
    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    public void updateHover(
            double mouseX,
            double mouseY
    ) {
        isHovered = isMouseOver(mouseX, mouseY);
    }
}
