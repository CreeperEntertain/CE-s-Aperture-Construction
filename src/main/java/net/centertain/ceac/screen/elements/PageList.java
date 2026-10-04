package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementContainer;
import net.centertain.ceac.screen.framework.element_types.FocusContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PageList implements Element, ElementContainer, FocusContainer, GuiEventListener {
    private int x;
    private int y;
    private int width;
    private int height;
    private @NotNull List<@NotNull Page> pages;
    private int pageIndex;
    private @NotNull TabPosition tabPosition;
    private int tabRibbonWidth;
    private boolean showPageNames;
    private @Nullable ScrollContainer tabScroll;
    private @Nullable FlowPanel tabPanel;
    private final @NotNull List<@NotNull Button> tabButtons;
    private @Nullable GuiEventListener focusedElement;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull List<@NotNull Page>> dynamicPages = null;
    private @Nullable Supplier<@NotNull Integer> dynamicPageIndex = null;
    private @Nullable Supplier<@NotNull TabPosition> dynamicTabPosition = null;
    private @Nullable Supplier<@NotNull Integer> dynamicTabRibbonWidth = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicShowPageNames = null;

    public enum TabPosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    public PageList(
            int width,
            int height,
            @NotNull List<@NotNull Page> pages,
            @NotNull TabPosition tabPosition,
            int tabSize,
            boolean showPageNames
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.tabPosition = tabPosition;
        this.tabRibbonWidth = tabSize;
        this.showPageNames = showPageNames;
        this.tabButtons = new ArrayList<>();
        rebuildTabs();
    }

    public PageList(
            int x,
            int y,
            int width,
            int height,
            @NotNull List<@NotNull Page> pages,
            @NotNull TabPosition tabPosition,
            int tabSize,
            boolean showPageNames
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.tabPosition = tabPosition;
        this.tabRibbonWidth = tabSize;
        this.showPageNames = showPageNames;
        this.tabButtons = new ArrayList<>();
        rebuildTabs();
    }

    public PageList(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @NotNull List<@NotNull Page> pages,
            @NotNull TabPosition tabPosition,
            int tabSize,
            boolean showPageNames
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.tabPosition = tabPosition;
        this.tabRibbonWidth = tabSize;
        this.showPageNames = showPageNames;
        this.tabButtons = new ArrayList<>();
        rebuildTabs();
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
    public @NotNull List<@NotNull Page> getPages() {
        return pages;
    }
    public int getPageIndex() {
        return pageIndex;
    }
    public @NotNull TabPosition getTabPosition() {
        return tabPosition;
    }
    public int getTabRibbonWidth() {
        return tabRibbonWidth;
    }
    public boolean getShowPageNames() {
        return showPageNames;
    }
    public @Nullable GuiEventListener getFocusedElement() {
        return focusedElement;
    }
    public @NotNull Element getElement() {
        if (pages.isEmpty())
            return new Dimensions(0, 0, 0, 0);
        return pages.get(pageIndex);
    }

    private @Nullable Supplier<@NotNull Integer> getDynamicX() {
        return dynamicX;
    }
    private @Nullable Supplier<@NotNull Integer> getDynamicY() {
        return dynamicY;
    }
    private @Nullable Supplier<@NotNull Integer> getDynamicWidth() {
        return dynamicWidth;
    }
    private @Nullable Supplier<@NotNull Integer> getDynamicHeight() {
        return dynamicHeight;
    }
    private @Nullable Supplier<@NotNull List<@NotNull Page>> getDynamicPages() {
        return dynamicPages;
    }
    private @Nullable Supplier<@NotNull Integer> getDynamicPageIndex() {
        return dynamicPageIndex;
    }
    private @Nullable Supplier<@NotNull TabPosition> getDynamicTabPosition() {
        return dynamicTabPosition;
    }
    private @Nullable Supplier<@NotNull Integer> getDynamicTabRibbonWidth() {
        return dynamicTabRibbonWidth;
    }
    private @Nullable Supplier<@NotNull Boolean> getDynamicShowPageNames() {
        return dynamicShowPageNames;
    }

    public void setX(int x) {
        this.x = x;
        layoutTabs();
    }
    public void setY(int y) {
        this.y = y;
        layoutTabs();
    }
    public void setWidth(int width) {
        this.width = width;
        layoutTabs();
    }
    public void setHeight(int height) {
        this.height = height;
        layoutTabs();
    }
    public void setPages(@NotNull List<@NotNull Page> pages) {
        this.pages = pages;
        if (pageIndex >= pages.size())
            pageIndex = Math.max(0, pages.size() - 1);
        rebuildTabs();
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
        rebuildTabs();
    }
    public void setTabRibbonWidth(int tabRibbonWidth) {
        this.tabRibbonWidth = tabRibbonWidth;
        rebuildTabs();
    }
    public void setShowPageNames(boolean showPageNames) {
        this.showPageNames = showPageNames;
        rebuildTabs();
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();
        layoutTabs();
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
    public void setDynamicPages(@Nullable Supplier<@NotNull List<@NotNull Page>> dynamicPages) {
        this.dynamicPages = dynamicPages;
    }
    public void setDynamicPageIndex(@Nullable Supplier<@NotNull Integer> dynamicPageIndex) {
        this.dynamicPageIndex = dynamicPageIndex;
    }
    public void setDynamicTabPosition(@Nullable Supplier<@NotNull TabPosition> dynamicTabPosition) {
        this.dynamicTabPosition = dynamicTabPosition;
    }
    public void setDynamicTabRibbonWidth(@Nullable Supplier<@NotNull Integer> dynamicTabRibbonWidth) {
        this.dynamicTabRibbonWidth = dynamicTabRibbonWidth;
    }
    public void setDynamicShowPageNames(@Nullable Supplier<@NotNull Boolean> dynamicShowPageNames) {
        this.dynamicShowPageNames = dynamicShowPageNames;
    }


    public void addPage(@NotNull Page page) {
        pages.add(page);
        rebuildTabs();
    }

    public boolean removePage(int pageIndex) {
        if (pageIndex < 0 || pageIndex > pages.size() - 1)
            return false;
        pages.remove(pageIndex);
        if (this.pageIndex >= pages.size())
            this.pageIndex = Math.max(0, pages.size() - 1);
        rebuildTabs();
        return true;
    }

    public boolean removePage(@NotNull Page page) {
        if (!pages.remove(page))
            return false;
        if (pageIndex >= pages.size())
            pageIndex = Math.max(0, pages.size() - 1);
        rebuildTabs();
        return true;
    }


    @SuppressWarnings("ExtractMethodRecommender")
    private void rebuildTabs() {
        tabButtons.clear();

        List<Element> buttons = new ArrayList<>();

        for (int i = 0; i < pages.size(); i++) {
            final int page = i;

            String text = showPageNames
                    ? pages.get(i).getName()
                    : String.valueOf(i + 1);

            MarqueeLabel label = new MarqueeLabel(
                    tabPosition == PageList.TabPosition.TOP ||
                            tabPosition == PageList.TabPosition.BOTTOM
                            ? GuiConstants.TAB_BUTTON_HEIGHT
                            : tabRibbonWidth,
                    Component.literal(text),
                    MarqueeLabel.Alignment.CENTER,
                    GuiConstants.COLOR_SOLID_WHITE,
                    1.0f,
                    false
            );

            int tabWidth = switch (tabPosition) {
                case LEFT, RIGHT -> tabRibbonWidth;
                case TOP, BOTTOM -> GuiConstants.TAB_BUTTON_HEIGHT;
            };

            int tabHeight = switch (tabPosition) {
                case LEFT, RIGHT -> GuiConstants.TAB_BUTTON_HEIGHT;
                case TOP, BOTTOM -> tabRibbonWidth;
            };

            Aligner aligner = new Aligner(
                    tabWidth,
                    tabHeight,
                    Aligner.Alignment.CENTER,
                    label
            );

            Button button = new Button(
                    0,
                    0,
                    aligner,
                    () -> setPageIndex(page),
                    GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                    GuiConstants.COLOR_SOLID_GRAY
            );

            button.setWidth(tabWidth);
            button.setHeight(tabHeight);

            tabButtons.add(button);
            buttons.add(button);
        }

        boolean horizontal =
                tabPosition == TabPosition.TOP ||
                tabPosition == TabPosition.BOTTOM;

        tabPanel = new FlowPanel(
                getTabPanelX(),
                getTabPanelY(),
                horizontal
                        ? FlowPanel.Alignment.HORIZONTAL
                        : FlowPanel.Alignment.VERTICAL,
                getTabContentSize(),
                GuiConstants.PAGE_TAB_SPACING,
                GuiConstants.COLOR_TRANSPARENT,
                buttons
        );

        tabScroll = new ScrollContainer(
                x,
                y,
                width,
                height,
                horizontal
                        ? ScrollContainer.Alignment.HORIZONTAL
                        : ScrollContainer.Alignment.VERTICAL,
                tabPanel,
                getTabContentSize(),
                GuiConstants.STACK_SCROLL_SPEED
        );
    }

    private void layoutTabs() {
        if (tabPanel == null || tabScroll == null)
            return;

        int contentSize = getTabContentSize();

        tabPanel.setX(getTabPanelX());
        tabPanel.setY(getTabPanelY());
        tabPanel.setWideness(contentSize);
        tabPanel.setSpacing(GuiConstants.PAGE_TAB_SPACING);

        tabScroll.setX(x);
        tabScroll.setY(y);
        tabScroll.setWidth(width);
        tabScroll.setHeight(height);
        tabScroll.setContentSize(contentSize);
    }

    private int getTabContentSize() {
        boolean horizontal =
                tabPosition == TabPosition.TOP ||
                tabPosition == TabPosition.BOTTOM;

        if (tabButtons.isEmpty())
            return 0;

        int size = 0;

        for (Button button : tabButtons)
            size += horizontal
                    ? button.getWidth()
                    : button.getHeight();

        size += GuiConstants.PAGE_TAB_SPACING * (tabButtons.size() - 1);

        return size;
    }

    private int getTabPanelX() {
        return switch (tabPosition) {
            case LEFT, TOP, BOTTOM -> x;
            case RIGHT -> x + width - tabRibbonWidth;
        };
    }

    private int getTabPanelY() {
        return switch (tabPosition) {
            case LEFT, TOP, RIGHT -> y;
            case BOTTOM -> y + height - tabRibbonWidth;
        };
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isMouseOverTabStrip(
            double mouseX,
            double mouseY
    ) {
        return switch (tabPosition) {
            case LEFT ->
                    mouseX >= x &&
                    mouseY >= y &&
                    mouseX < x + tabRibbonWidth &&
                    mouseY < y + height;
            case TOP ->
                    mouseX >= x &&
                    mouseY >= y &&
                    mouseX < x + width &&
                    mouseY < y + tabRibbonWidth;
            case RIGHT ->
                    mouseX >= x + width - tabRibbonWidth &&
                    mouseY >= y &&
                    mouseX < x + width &&
                    mouseY < y + height;
            case BOTTOM ->
                    mouseX >= x &&
                    mouseY >= y + height - tabRibbonWidth &&
                    mouseX < x + width &&
                    mouseY < y + height;
        };
    }

    private void updateTabHover(
            double mouseX,
            double mouseY
    ) {
        boolean horizontal =
                tabPosition == TabPosition.TOP ||
                tabPosition == TabPosition.BOTTOM;

        double contentMouseX = mouseX;
        double contentMouseY = mouseY;

        if (tabScroll != null) {
            if (horizontal)
                contentMouseX += tabScroll.getScrollOffset();
            else
                contentMouseY += tabScroll.getScrollOffset();
        }

        if (!isMouseOverTabStrip(mouseX, mouseY)) {
            contentMouseX = -1;
            contentMouseY = -1;
        }

        for (Button button : tabButtons)
            button.updateHover(contentMouseX, contentMouseY);
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicTabRibbonWidth != null) setTabRibbonWidth(dynamicTabRibbonWidth.get());
        if (dynamicTabPosition != null) setTabPosition(dynamicTabPosition.get());
        if (dynamicPages != null) setPages(dynamicPages.get());
        if (dynamicPageIndex != null) setPageIndex(dynamicPageIndex.get());
        if (dynamicShowPageNames != null) setShowPageNames(dynamicShowPageNames.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        if (pages.isEmpty())
            return;

        layoutTabs();
        updateTabHover(mouseX, mouseY);

        if (tabScroll != null)
            tabScroll.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

        Page page = pages.get(pageIndex);

        int contentX = x;
        int contentY = y;
        int contentWidth = width;
        int contentHeight = height;

        switch (tabPosition) {
            case LEFT -> {
                contentX += tabRibbonWidth + GuiConstants.PAGE_TAB_SPACING;
                contentWidth -= tabRibbonWidth + GuiConstants.PAGE_TAB_SPACING;
            }
            case TOP -> {
                contentY += tabRibbonWidth + GuiConstants.PAGE_TAB_SPACING;
                contentHeight -= tabRibbonWidth + GuiConstants.PAGE_TAB_SPACING;
            }
            case RIGHT -> contentWidth -= tabRibbonWidth + GuiConstants.PAGE_TAB_SPACING;
            case BOTTOM -> contentHeight -= tabRibbonWidth + GuiConstants.PAGE_TAB_SPACING;
        }

        contentWidth = Math.max(0, contentWidth);
        contentHeight = Math.max(0, contentHeight);

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

        renderSelectedTabOutline(
                guiGraphics
        );
    }

    private void renderSelectedTabOutline(
            @NotNull GuiGraphics guiGraphics
    ) {
        if (pageIndex < 0 || pageIndex >= tabButtons.size())
            return;

        Button button = tabButtons.get(pageIndex);

        int tabX = button.getX();
        int tabY = button.getY();

        boolean horizontal = tabPosition == TabPosition.TOP || tabPosition == TabPosition.BOTTOM;

        if (tabScroll != null) {
            if (horizontal)
                tabX -= (int) tabScroll.getScrollOffset();
            else
                tabY -= (int) tabScroll.getScrollOffset();
        }

        guiGraphics.enableScissor(
                x,
                y,
                x + width,
                y + height
        );

        renderOutline(
                guiGraphics,
                tabX,
                tabY,
                button.getWidth(),
                button.getHeight(),
                GuiConstants.COLOR_SOLID_WHITE
        );

        guiGraphics.disableScissor();
    }

    @SuppressWarnings("SameParameterValue")
    private void renderOutline(
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
        if (
                button != 0 ||
                pages.isEmpty() ||
                !isMouseOverTabStrip(mouseX, mouseY)
        )
            return false;

        updateTabHover(mouseX, mouseY);

        if (tabScroll != null)
            if (!tabScroll.mouseClicked(mouseX, mouseY, button))
                return false;

        focusedElement = null;
        return true;
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollDelta
    ) {
        if (!isMouseOverTabStrip(mouseX, mouseY))
            return false;
        if (tabScroll == null)
            return false;
        return tabScroll.mouseScrolled(
                mouseX,
                mouseY,
                scrollDelta
        );
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

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return new ScreenRectangle(x, y, width, height);
    }
}