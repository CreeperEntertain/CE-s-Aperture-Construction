package net.centertain.ceac.screen;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.DecalPack;
import net.centertain.ceac.screen.elements.Button;
import net.centertain.ceac.screen.elements.FlowPanel;
import net.centertain.ceac.screen.elements.ScrollContainer;
import net.centertain.ceac.screen.elements.StackPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DecalItemScreen extends Screen {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private DecalPack selectedPack;
    private ScrollContainer tabScroll;
    private ScrollContainer decalScroll;

    private final ItemStack stack;

    public DecalItemScreen(ItemStack stack) {
        super(Component.empty());
        this.stack = stack;
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void setPack(DecalPack pack) {
        selectedPack = pack;
        System.out.println("Button clicked for pack " + pack.getName());
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollDelta
    ) {
        if (tabScroll != null && tabScroll.mouseScrolled(mouseX, mouseY, scrollDelta))
            return true;
        if (decalScroll != null && decalScroll.mouseScrolled(mouseX, mouseY, scrollDelta))
            return true;
        return super.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    @Override
    protected void init() {
        super.init();

        left = GuiConstants.SCREEN_PADDING + GuiConstants.STACK_PANEL_WIDTH + GuiConstants.ELEMENT_PADDING;
        top = GuiConstants.SCREEN_PADDING;
        right = width - GuiConstants.SCREEN_PADDING;
        bottom = height - GuiConstants.SCREEN_PADDING;

        List<DecalPack> decalPacks = DecalLoader.getPacks();
        List<Button> tabs = new ArrayList<>();
        for (DecalPack decalPack : decalPacks) {
            Button button = new Button(
                    0,
                    0,
                    GuiConstants.STACK_PANEL_WIDTH,
                    GuiConstants.TAB_BUTTON_HEIGHT,
                    Component.literal(decalPack.getName()),
                    GuiConstants.COLOR_SOLID_WHITE,
                    1.0f,
                    GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                    GuiConstants.COLOR_SOLID_WHITE,
                    null,
                    () -> setPack(decalPack)
            );

            tabs.add(button);
            addWidget(button);
        }
        StackPanel tabStack = new StackPanel(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                GuiConstants.STACK_PANEL_WIDTH,
                tabs,
                GuiConstants.COLOR_TRANSPARENT
        );
        tabScroll = new ScrollContainer(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                GuiConstants.STACK_PANEL_WIDTH,
                height - GuiConstants.SCREEN_PADDING * 2,
                tabStack,
                tabStack.getHeight()
        );
        addRenderableOnly(tabScroll);

        List<Button> decals = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            for (DecalPack decalPack : decalPacks) {
                Button button = new Button(
                        0,
                        0,
                        GuiConstants.STACK_PANEL_WIDTH,
                        GuiConstants.TAB_BUTTON_HEIGHT,
                        Component.literal(decalPack.getName()),
                        GuiConstants.COLOR_SOLID_WHITE,
                        0.5f,
                        GuiConstants.COLOR_TRANSPARENT,
                        GuiConstants.COLOR_SOLID_WHITE,
                        null,
                        () -> setPack(decalPack)
                );

                decals.add(button);
                addWidget(button);
            }
        FlowPanel decalPanel = new FlowPanel(
                left,
                top,
                right - left,
                decals,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );
        decalScroll = new ScrollContainer(
                left,
                top,
                right - left,
                bottom - top,
                decalPanel,
                decalPanel.getHeight()
        );
        addRenderableOnly(decalScroll);
    }
}
