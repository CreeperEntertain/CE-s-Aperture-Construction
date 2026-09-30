package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.FocusContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PageList implements Element, ElementContainer, FocusContainer, GuiEventListener {
    private int x;
    private int y;
    private int width;
    private int height;

    private List<Page> pages;
    private int pageIndex;

    private TabPosition tabPosition;
    private int tabSize;
    private boolean showPageNames;

    private GuiEventListener focusedElement;
    private boolean isHovered;

    public enum TabPosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    public PageList(
            int width,
            int height,
            @NotNull List<Page> pages,
            TabPosition tabPosition,
            int tabSize,
            boolean showPageNames
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.tabPosition = tabPosition;
        this.tabSize = tabSize;
        this.showPageNames = showPageNames;
    }

    public PageList(
            int x,
            int y,
            int width,
            int height,
            @NotNull List<Page> pages,
            TabPosition tabPosition,
            int tabSize,
            boolean showPageNames
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.tabPosition = tabPosition;
        this.tabSize = tabSize;
        this.showPageNames = showPageNames;
    }

    public PageList(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @NotNull List<Page> pages,
            TabPosition tabPosition,
            int tabSize,
            boolean showPageNames
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.tabPosition = tabPosition;
        this.tabSize = tabSize;
        this.showPageNames = showPageNames;
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
    public @NotNull List<Page> getPages() {
        return pages;
    }
    public int getPageIndex() {
        return pageIndex;
    }
    public TabPosition getTabPosition() {
        return tabPosition;
    }
    public int getTabSize() {
        return tabSize;
    }
    public boolean getShowPageNames() {
        return showPageNames;
    }
    public GuiEventListener getFocusedElement() {
        return focusedElement;
    }
    public @NotNull Element getElement() {
        if (pages.isEmpty())
            return new Dimensions(0, 0, 0, 0);
        return pages.get(pageIndex);
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
    public void setPages(@NotNull List<Page> pages) {
        this.pages = pages;
        if (pageIndex >= pages.size())
            pageIndex = Math.max(0, pages.size() - 1);
    }
    public void setPageIndex(int pageIndex) {
        if (pages.isEmpty()) {
            this.pageIndex = 0;
            return;
        }
        this.pageIndex = Math.max(0, Math.min(pageIndex, pages.size() - 1));
        focusedElement = null;
    }
    public void setTabPosition(TabPosition tabPosition) {
        this.tabPosition = tabPosition;
    }
    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }
    public void setShowPageNames(boolean showPageNames) {
        this.showPageNames = showPageNames;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
    }


    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        if (pages.isEmpty())
            return;

        Page page = pages.get(pageIndex);

        int contentX = x;
        int contentY = y;
        int contentWidth = width;
        int contentHeight = height;

        switch (tabPosition) {
            case LEFT -> {
                contentX += tabSize;
                contentWidth -= tabSize;
            }
            case TOP -> {
                contentY += tabSize;
                contentHeight -= tabSize;
            }
            case RIGHT -> contentWidth -= tabSize;
            case BOTTOM -> contentHeight -= tabSize;
        }

        contentWidth = Math.max(0, contentWidth);
        contentHeight = Math.max(0, contentHeight);

        renderTabs(
                guiGraphics,
                mouseX,
                mouseY
        );

        page.setX(contentX);
        page.setY(contentY);
        page.setWidth(contentWidth);
        page.setHeight(contentHeight);

        page.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void renderTabs(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        int count = pages.size();

        if (count == 0)
            return;

        switch (tabPosition) {
            case TOP, BOTTOM -> {
                int tabWidth = width / count;
                for (int i = 0; i < count; i++) {
                    int tabX = x + i * tabWidth;
                    int currentTabWidth = i == count - 1
                            ? width - (i * tabWidth)
                            : tabWidth;
                    int tabY = tabPosition == TabPosition.TOP
                            ? y
                            : y + height - tabSize;
                    renderTab(
                            guiGraphics,
                            i,
                            tabX,
                            tabY,
                            currentTabWidth,
                            tabSize,
                            mouseX,
                            mouseY
                    );
                }
            }
            case LEFT, RIGHT -> {
                int tabHeight = height / count;
                for (int i = 0; i < count; i++) {
                    int tabY = y + i * tabHeight;
                    int currentTabHeight = i == count - 1
                            ? height - (i * tabHeight)
                            : tabHeight;
                    int tabX = tabPosition == TabPosition.LEFT
                            ? x
                            : x + width - tabSize;
                    renderTab(
                            guiGraphics,
                            i,
                            tabX,
                            tabY,
                            tabSize,
                            currentTabHeight,
                            mouseX,
                            mouseY
                    );
                }
            }
        }
    }

    private void renderTab(
            @NotNull GuiGraphics guiGraphics,
            int index,
            int tabX,
            int tabY,
            int tabWidth,
            int tabHeight,
            double mouseX,
            double mouseY
    ) {
        boolean hovered =
                mouseX >= tabX &&
                mouseY >= tabY &&
                mouseX < tabX + tabWidth &&
                mouseY < tabY + tabHeight;
        boolean selected = index == pageIndex;

        guiGraphics.fill(
                tabX,
                tabY,
                tabX + tabWidth,
                tabY + tabHeight,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        int outlineColor = selected
                ? GuiConstants.COLOR_MINECRAFT_WHITE
                : GuiConstants.COLOR_MINECRAFT_GRAY;

        if (hovered || selected)
            renderHoverOutline(guiGraphics, tabX, tabY, tabWidth, tabHeight, outlineColor);

        String text = showPageNames
                ? pages.get(index).getName()
                : String.valueOf(index + 1);

        int textWidth = Minecraft.getInstance().font.width(text);
        int textX = tabX + (tabWidth - textWidth) / 2;
        int textY = tabY + (tabHeight - Minecraft.getInstance().font.lineHeight) / 2;

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                text,
                textX,
                textY,
                GuiConstants.COLOR_MINECRAFT_WHITE,
                false
        );
    }

    private void renderHoverOutline(
            @NotNull GuiGraphics guiGraphics,
            int tabX,
            int tabY,
            int tabWidth,
            int tabHeight,
            int outlineColor
    ) {
        guiGraphics.fill(
                tabX,
                tabY,
                tabX + tabWidth,
                tabY + 1,
                outlineColor
        );
        guiGraphics.fill(
                tabX,
                tabY + tabHeight - 1,
                tabX + tabWidth,
                tabY + tabHeight,
                outlineColor
        );
        guiGraphics.fill(
                tabX,
                tabY,
                tabX + 1,
                tabY + tabHeight,
                outlineColor
        );
        guiGraphics.fill(
                tabX + tabWidth - 1,
                tabY,
                tabX + tabWidth,
                tabY + tabHeight,
                outlineColor
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0 || pages.isEmpty())
            return false;

        int clickedPage = getTabIndex(mouseX, mouseY);

        if (clickedPage < 0)
            return false;

        pageIndex = clickedPage;
        focusedElement = null;
        return true;
    }

    private int getTabIndex(
            double mouseX,
            double mouseY
    ) {
        if (
                mouseX < x ||
                mouseY < y ||
                mouseX >= x + width ||
                mouseY >= y + height
        )
            return -1;

        int count = pages.size();

        switch (tabPosition) {
            case TOP, BOTTOM -> {
                int tabWidth = width / count;

                if (tabPosition == TabPosition.TOP && mouseY >= y + tabSize)
                    return -1;
                if (tabPosition == TabPosition.BOTTOM && mouseY < y + height - tabSize)
                    return -1;

                int index = (int) ((mouseX - x) / tabWidth);
                return Math.min(index, count - 1);
            }
            case LEFT, RIGHT -> {
                int tabHeight = height / count;

                if (tabPosition == TabPosition.LEFT && mouseX >= x + tabSize)
                    return -1;
                if (tabPosition == TabPosition.RIGHT && mouseX < x + width - tabSize)
                    return -1;

                int index = (int) ((mouseY - y) / tabHeight);
                return Math.min(index, count - 1);
            }
        }

        return -1;
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
        if (!focused)
            focusedElement = null;
    }

    @Override
    public boolean isFocused() {
        return focusedElement != null;
    }
}
