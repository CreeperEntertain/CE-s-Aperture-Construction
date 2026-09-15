package net.centertain.ceac;

import net.centertain.ceac.phys_screen.elements.PhysButton;
import net.centertain.ceac.phys_screen.elements.PhysGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PhysScreen extends Screen {
    protected PhysScreen(Component title) {
        super(title);
    }

    protected void build() {}

    @Override
    protected void init() {
        build();
    }

    public void renderPhysical(
            PhysGuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        for (GuiEventListener child : children()) {
            if (child instanceof PhysButton button)
                button.renderPhysical(
                        guiGraphics,
                        mouseX,
                        mouseY,
                        partialTick
                );
        }
    }

    public int getScreenWidth() {
        return width;
    }
    public int getScreenHeight() {
        return height;
    }

    public double getPhysicalWidth() {
        double scale = 2.0 / Math.max(width, height);
        return width * scale;
    }
    public double getPhysicalHeight() {
        double scale = 2.0 / Math.max(width, height);
        return height * scale;
    }
}
