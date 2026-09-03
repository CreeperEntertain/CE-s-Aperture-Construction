package net.centertain.ceac.screen;

import net.centertain.ceac.GuiConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DecalItemScreen extends Screen {
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

        guiGraphics.fill(left, top, right, bottom, 0xC0000000);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();
    }
}
