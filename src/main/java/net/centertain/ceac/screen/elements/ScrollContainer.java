package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.ElementContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ScrollContainer implements Element, ElementContainer, GuiEventListener {
    private int x;
    private int y;
    private int width;
    private int height;

    private Alignment alignment;

    private Element element;

    private int contentSize;
    private int scrollSpeed;

    private double scrollOffset;

    public enum Alignment {
        HORIZONTAL,
        VERTICAL
    }

    public ScrollContainer(
            int x,
            int y,
            int width,
            int height,
            Alignment alignment,
            @NotNull Element element,
            int contentSize,
            int scrollSpeed
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alignment = alignment;
        this.element = element;
        this.contentSize = contentSize;
        this.scrollSpeed = scrollSpeed;
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
    public Alignment getAlignment() {
        return alignment;
    }
    public @NotNull Element getElement() {
        return element;
    }
    public int getContentSize() {
        return contentSize;
    }
    public int getScrollSpeed() {
        return scrollSpeed;
    }
    public double getScrollOffset() {
        return scrollOffset;
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
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
    }
    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }
    public void setScrollOffset(double scrollOffset) {
        this.scrollOffset = Mth.clamp(scrollOffset, 0.0, getMaxScroll());
    }


    private int getViewportSize() {
        return alignment == Alignment.VERTICAL ? height : width;
    }

    private int getMaxScroll() {
        return Math.max(0, contentSize - getViewportSize());
    }

    private void clampScroll() {
        setScrollOffset(scrollOffset);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean canScroll() {
        return contentSize > getViewportSize();
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.enableScissor(
                x,
                y,
                x + width,
                y + width
        );
        guiGraphics.pose().pushPose();

        if (alignment == Alignment.VERTICAL) {
            guiGraphics.pose().translate(
                    0.0,
                    -scrollOffset,
                    0.0
            );
            element.render(
                    guiGraphics,
                    mouseX,
                    (int) (mouseY + scrollOffset),
                    partialTick
            );
        } else {
            guiGraphics.pose().translate(
                    -scrollOffset,
                    0.0,
                    0.0
            );
            element.render(
                    guiGraphics,
                    (int) (mouseX + scrollOffset),
                    mouseY,
                    partialTick
            );
        }

        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();

        if (!canScroll())
            return;

        renderScollbar(guiGraphics);
    }

    private void renderScollbar(
            GuiGraphics guiGraphics
    ) {
        if (alignment == Alignment.VERTICAL) {
            int scrollbarWidth = GuiConstants.SCROLL_BAR_WIDTH;
            int scrollbarX = x + width  - scrollbarWidth;
            int scrollbarHeight = Math.max(1, (int) ((double) height * height / contentSize));
            int scrollbarTravel = height - scrollbarHeight;
            int scrollbarY = y + (getMaxScroll() > 0
                    ? (int) (scrollbarTravel * (scrollOffset / getMaxScroll()))
                    : 0
            );

            guiGraphics.fill(
                    scrollbarX,
                    scrollbarY,
                    scrollbarX + scrollbarWidth,
                    scrollbarY + scrollbarHeight,
                    GuiConstants.COLOR_SOLID_LIGHT_GRAY
            );
        } else {
            @SuppressWarnings("SuspiciousNameCombination")
            int scrollbarHeight = GuiConstants.SCROLL_BAR_WIDTH;
            int scrollbarY = y + height - scrollbarHeight;
            int scrollbarWidth = Math.max(1, (int) ((double) width * width / contentSize));
            int scrollbarTravel = width - scrollbarWidth;
            int scrollbarX = x + (getMaxScroll() > 0
                    ? (int) (scrollbarTravel * (scrollOffset / getMaxScroll()))
                    : 0
            );

            guiGraphics.fill(
                    scrollbarX,
                    scrollbarY,
                    scrollbarX + scrollbarWidth,
                    scrollbarY + scrollbarHeight,
                    GuiConstants.COLOR_SOLID_LIGHT_GRAY
            );
        }
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollDelta
    ) {
        if (!isMouseOver(mouseX, mouseY))
            return false;
        if (!canScroll())
            return false;

        setScrollOffset(scrollOffset - scrollDelta * scrollSpeed);

        return true;
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!isMouseOver(mouseX, mouseY))
            return false;
        if (!(element instanceof GuiEventListener listener))
            return false;

        double contentMouseX = mouseX;
        double contentMouseY = mouseY;

        if (alignment == Alignment.HORIZONTAL)
            contentMouseX += scrollOffset;
        else
            contentMouseY += scrollOffset;

        return listener.mouseClicked(contentMouseX, contentMouseY, button);
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
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }
}
