package net.centertain.ceac.screen;

import net.centertain.ceac.screen.framework.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PurchasingTermialScreen extends Screen {
    public PurchasingTermialScreen() {
        super(Component.empty());
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

    @Override
    protected void init() {
        super.init();
    }
}
