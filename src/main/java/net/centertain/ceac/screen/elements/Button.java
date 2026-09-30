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

public class Button extends AbstractWidget implements Element, ElementContainer, Hoverable, GuiEventListener {
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

    public Button(
            int x,
            int y,
            @NotNull Element element,
            @NotNull Runnable onPress,
            int backgroundColor,
            int outlineColor
    ) {
        super(
                x,
                y,
                element.getWidth(),
                element.getHeight(),
                Component.empty()
        );

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

    @Override
    public void renderWidget(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
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
    protected void updateWidgetNarration(
            @NotNull NarrationElementOutput narrationElementOutput
    ) {
        narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
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
