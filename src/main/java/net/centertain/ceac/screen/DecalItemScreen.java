package net.centertain.ceac.screen;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.DecalPack;
import net.centertain.ceac.screen.elements.Button;
import net.centertain.ceac.screen.elements.StackPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DecalItemScreen extends Screen {
    private DecalPack selectedPack;

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

        int left = GuiConstants.SCREEN_PADDING + GuiConstants.STACK_PANEL_WIDTH + GuiConstants.ELEMENT_PADDING;
        int top = GuiConstants.SCREEN_PADDING;
        int right = width - GuiConstants.SCREEN_PADDING;
        int bottom = height - GuiConstants.SCREEN_PADDING;

        guiGraphics.fill(left, top, right, bottom, GuiConstants.COLOR_TRANSLUCENT_BLACK_75);

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
                    GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                    GuiConstants.COLOR_SOLID_WHITE,
                    null,
                    () -> setPack(decalPack)
            );
            tabs.add(button);
            addRenderableWidget(button);
        }

        addRenderableOnly(new StackPanel(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                GuiConstants.STACK_PANEL_WIDTH,
                tabs,
                GuiConstants.COLOR_TRANSPARENT
        ));

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void setPack(DecalPack pack) {
        selectedPack = pack;
        System.out.println("Button clicked for pack " + pack.getName());
    }

    @Override
    protected void init() {
        super.init();
    }
}
