package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class Foldout implements Element, ElementContainer, GuiEventListener {
    private static final int BAR_HEIGHT = GuiConstants.FOLDOUT_TOP_HEIGHT;

    private int x;
    private int y;
    private int width;
    private int distance;

    private Component title;
    private Element element;
    private boolean foldedOut;
    private boolean focused;

    public Foldout(
            Component title,
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
        return BAR_HEIGHT + distance + (foldedOut ? element.getHeight() : 0);
    }
    public int getDistance() {
        return distance;
    }
    public Component getTitle() {
        return title;
    }
    public String getTitleString() {
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
    public void setTitle(Component title) {
        this.title = title;
    }
    public void setTitle(String title) {
        this.title = Component.literal(title);
    }
    public void setElement(@NotNull Element element) {
        this.element = element;
        this.element.setWidth(width);
    }
    public void setFoldedOut(boolean foldedOut) {
        this.foldedOut = foldedOut;
    }
    public void toggle() {
        foldedOut = !foldedOut;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        element.setWidth(width);
    }


    @Override
    public boolean hasElement() {
        return foldedOut;
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        boolean hovered = isMouseOver(mouseX, mouseY);

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
}
