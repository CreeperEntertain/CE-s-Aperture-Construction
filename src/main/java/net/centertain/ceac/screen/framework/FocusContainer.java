package net.centertain.ceac.screen.framework;

import net.minecraft.client.gui.components.events.GuiEventListener;

public interface FocusContainer {
    GuiEventListener getFocusedElement();
}
