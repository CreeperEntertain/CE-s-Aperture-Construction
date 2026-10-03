package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.Hoverable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Foldout implements Element, ElementContainer, Hoverable, GuiEventListener {
    private static final int BAR_HEIGHT = GuiConstants.FOLDOUT_TOP_HEIGHT;

    private int x;
    private int y;
    private int width;
    private int distance;
    private @NotNull Component title;
    private @NotNull Element element;
    private boolean foldedOut;
    private boolean focused;
    private boolean isHovered;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicDistance = null;
    private @Nullable Supplier<@NotNull Component> dynamicTitle = null;
    private @Nullable Supplier<@NotNull Element> dynamicElement = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicFoldedOut = null;

    public Foldout(
            @NotNull Component title,
            int width,
            int distance,
            boolean defaultFoldedOut,
            @NotNull Element element
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.distance = distance;
        this.title = title;
        this.element = element;
        this.foldedOut = defaultFoldedOut;

        this.element.setWidth(width);
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
        return BAR_HEIGHT + (foldedOut ? this.distance : 0) + (foldedOut ? element.getHeight() : 0);
    }
    public int getDistance() {
        return distance;
    }
    public @NotNull Component getTitle() {
        return title;
    }
    public @NotNull String getTitleString() {
        return title.getString();
    }
    public @NotNull Element getElement() {
        return element;
    }
    public boolean getFoldedOut() {
        return foldedOut;
    }
    public boolean getIsFocused() {
        return focused;
    }
    public boolean getIsHovered() {
        return isHovered;
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
    public @Nullable Supplier<@NotNull Integer> getDynamicDistance() {
        return dynamicDistance;
    }
    public @Nullable Supplier<@NotNull Component> getDynamicTitle() {
        return dynamicTitle;
    }
    public @Nullable Supplier<@NotNull Element> getDynamicElement() {
        return dynamicElement;
    }
    public @Nullable Supplier<@NotNull Boolean> getDynamicFoldedOut() {
        return dynamicFoldedOut;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
        element.setWidth(width);
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public void setHeight(int height) {}
    public void setTitle(@NotNull Component title) {
        this.title = title;
    }
    public void setTitle(@NotNull String title) {
        this.title = Component.literal(title);
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
        this.element.setWidth(width);
    }
    public void setFoldedOut(boolean foldedOut) {
        this.foldedOut = foldedOut;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        element.setWidth(width);
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
    public void setDynamicDistance(@Nullable Supplier<@NotNull Integer> dynamicDistance) {
        this.dynamicDistance = dynamicDistance;
    }
    public void setDynamicTitle(@Nullable Supplier<@NotNull Component> dynamicTitle) {
        this.dynamicTitle = dynamicTitle;
    }
    public void setDynamicElement(@Nullable Supplier<@NotNull Element> dynamicElement) {
        this.dynamicElement = dynamicElement;
    }
    public void setDynamicFoldedOut(@Nullable Supplier<@NotNull Boolean> dynamicFoldedOut) {
        this.dynamicFoldedOut = dynamicFoldedOut;
    }


    public void toggle() {
        foldedOut = !foldedOut;
    }

    @Override
    public boolean hasElement() {
        return foldedOut;
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicDistance != null) setDistance(dynamicDistance.get());
        if (dynamicTitle != null) setTitle(dynamicTitle.get());
        if (dynamicFoldedOut != null) setFoldedOut(dynamicFoldedOut.get());
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

        boolean hovered = isHovered;

        guiGraphics.fill(
                x,
                y,
                x + width,
                y + BAR_HEIGHT,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                foldedOut ? "v" : ">",
                x + GuiConstants.ELEMENT_PADDING,
                y + 3,
                GuiConstants.COLOR_SOLID_WHITE,
                false
        );
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                title,
                x + GuiConstants.ELEMENT_PADDING + 8,
                y + 3,
                GuiConstants.COLOR_SOLID_WHITE,
                false
        );

        if (hovered)
            renderHoverOutline(guiGraphics);
        if (!foldedOut)
            return;

        element.setX(x);
        element.setY(y + BAR_HEIGHT + distance);
        element.setWidth(width);

        element.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void renderHoverOutline(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + 1,
                GuiConstants.COLOR_SOLID_GRAY
        );
        guiGraphics.fill(
                x,
                y + BAR_HEIGHT - 1,
                x + width,
                y + BAR_HEIGHT,
                GuiConstants.COLOR_SOLID_GRAY
        );
        guiGraphics.fill(
                x,
                y,
                x + 1,
                y + BAR_HEIGHT,
                GuiConstants.COLOR_SOLID_GRAY
        );
        guiGraphics.fill(
                x + width - 1,
                y,
                x + width,
                y + BAR_HEIGHT,
                GuiConstants.COLOR_SOLID_GRAY
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0 || !isMouseOver(mouseX, mouseY))
            return false;
        toggle();
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
                mouseY < y + BAR_HEIGHT;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void updateHover(
            double mouseX,
            double mouseY
    ) {
        isHovered = isMouseOver(mouseX, mouseY);
    }
}
