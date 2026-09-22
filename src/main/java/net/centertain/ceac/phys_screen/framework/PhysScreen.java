package net.centertain.ceac.phys_screen.framework;

import net.centertain.ceac.phys_screen.elements.PhysButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PhysScreen extends Screen {
    private final List<PhysElement> physElements = new ArrayList<>();

    protected PhysScreen(Component title) {
        super(title);
    }

    protected void build() {}

    @SuppressWarnings("UnusedReturnValue")
    protected <T extends PhysElement> T addPhysElement(T element) {
        physElements.add(element);

        if (element instanceof GuiEventListener && element instanceof NarratableEntry)
            addWidget((GuiEventListener & NarratableEntry) element);

        return element;
    }

    @Override
    protected void init() {
        build();
    }

    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        for (PhysElement element : physElements)
            element.renderPhysical(guiGraphics);

        guiGraphics.renderQueuedText();
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

    public boolean updateMouse(
            double mouseX,
            double mouseY
    ) {
        boolean mouseOver =
                mouseX >= 0.0 &&
                mouseY >= 0.0 &&
                mouseX < getScreenWidth() &&
                mouseY < getScreenHeight();

        for (GuiEventListener child : children()) {
            if (child instanceof PhysButton button)
                button.updatePhysicalHover(
                        mouseX,
                        mouseY
                );
        }

        return mouseOver;
    }

    public boolean clickMouse(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (
                mouseX < 0.0 ||
                mouseY < 0.0 ||
                mouseX >= getScreenWidth() ||
                mouseY >= getScreenHeight()
        )
            return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
