package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.FocusContainer;
import net.centertain.ceac.screen.framework.element_types.HoverTransformer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ScrollContainer implements Element, ElementContainer, FocusContainer, HoverTransformer, GuiEventListener {
    private int x;
    private int y;
    private int width;
    private int height;
    private @NotNull Alignment alignment;
    private @NotNull Element element;
    private int contentSize;
    private int scrollSpeed;
    private double scrollOffset;
    private @Nullable GuiEventListener focusedElement;
    private boolean isHovered;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull Alignment> dynamicAlignment = null;
    private @Nullable Supplier<@NotNull Element> dynamicElement = null;
    private @Nullable Supplier<@NotNull Integer> dynamicContentSize = null;
    private @Nullable Supplier<@NotNull Integer> dynamicScrollSpeed = null;
    private @Nullable Supplier<@NotNull Double> dynamicScrollOffset = null;

    public enum Alignment {
        HORIZONTAL,
        VERTICAL
    }

    public ScrollContainer(
            int x,
            int y,
            int width,
            int height,
            @NotNull Alignment alignment,
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
    public @NotNull Alignment getAlignment() {
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
    public @Nullable GuiEventListener getFocusedElement() {
        return focusedElement;
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
    public @Nullable Supplier<@NotNull Alignment> getDynamicAlignment() {
        return dynamicAlignment;
    }
    public @Nullable Supplier<@NotNull Element> getDynamicElement() {
        return dynamicElement;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicContentSize() {
        return dynamicContentSize;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicScrollSpeed() {
        return dynamicScrollSpeed;
    }
    public @Nullable Supplier<@NotNull Double> getDynamicScrollOffset() {
        return dynamicScrollOffset;
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
    public void setAlignment(@NotNull Alignment alignment) {
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
    public void setDynamicAlignment(@Nullable Supplier<@NotNull Alignment> dynamicAlignment) {
        this.dynamicAlignment = dynamicAlignment;
    }
    public void setDynamicElement(@Nullable Supplier<@NotNull Element> dynamicElement) {
        this.dynamicElement = dynamicElement;
    }
    public void setDynamicContentSize(@Nullable Supplier<@NotNull Integer> dynamicContentSize) {
        this.dynamicContentSize = dynamicContentSize;
    }
    public void setDynamicScrollSpeed(@Nullable Supplier<@NotNull Integer> dynamicScrollSpeed) {
        this.dynamicScrollSpeed = dynamicScrollSpeed;
    }
    public void setDynamicScrollOffset(@Nullable Supplier<@NotNull Double> dynamicScrollOffset) {
        this.dynamicScrollOffset = dynamicScrollOffset;
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

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicAlignment != null) setAlignment(dynamicAlignment.get());
        if (dynamicElement != null) setElement(dynamicElement.get());
        if (dynamicContentSize != null) setContentSize(dynamicContentSize.get());
        if (dynamicScrollSpeed != null) setScrollSpeed(dynamicScrollSpeed.get());
        if (dynamicScrollOffset != null) setScrollOffset(dynamicScrollOffset.get());
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

        var pose = guiGraphics.pose().last().pose();

        int scissorX = Math.round(x + pose.m30());
        int scissorY = Math.round(y + pose.m31());

        guiGraphics.enableScissor(
                scissorX,
                scissorY,
                scissorX + width,
                scissorY + height
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

        contentSize = alignment == Alignment.VERTICAL
                ? element.getHeight()
                : element.getWidth();

        clampScroll();

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
        if (!canTransformMouse(mouseX, mouseY))
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
        if (!canTransformMouse(mouseX, mouseY))
            return false;
        if (!(element instanceof GuiEventListener listener))
            return false;

        double contentMouseX = mouseX;
        double contentMouseY = mouseY;

        if (alignment == Alignment.HORIZONTAL)
            contentMouseX += scrollOffset;
        else
            contentMouseY += scrollOffset;

        if (!listener.mouseClicked(contentMouseX, contentMouseY, button))
            return false;

        focusedElement = listener;
        return true;
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
        if (alignment == Alignment.HORIZONTAL)
            return mouseX + scrollOffset;
        return mouseX;
    }

    @Override
    public double transformMouseY(
            double mouseX,
            double mouseY
    ) {
        if (alignment == Alignment.VERTICAL)
            return mouseY + scrollOffset;
        return mouseY;
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }
}
