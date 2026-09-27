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

    private final List<PhysHoverable> hoverables = new ArrayList<>();
    private final List<PhysClickable> clickables = new ArrayList<>();
    private final List<PhysElementContainer> containers = new ArrayList<>();
    private final List<PhysElementLister> listers = new ArrayList<>();

    protected PhysScreen(Component title) {
        super(title);
    }

    protected void build() {}

    @SuppressWarnings("UnusedReturnValue")
    protected <T extends PhysElement> T addPhysElement(T element) {
        physElements.add(element);

        if (element instanceof GuiEventListener && element instanceof NarratableEntry)
            addWidget((GuiEventListener & NarratableEntry) element);
        if (element instanceof PhysHoverable hoverable)
            hoverables.add(hoverable);
        if (element instanceof PhysClickable clickable)
            clickables.add(clickable);
        if (element instanceof PhysElementContainer container)
            containers.add(container);
        if (element instanceof PhysElementLister lister)
            listers.add(lister);

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
            if (child instanceof PhysHoverable hoverable)
                hoverable.updatePhysicalHover(mouseX, mouseY);
        }

        for (PhysElement element : physElements)
            processElementHover(mouseX, mouseY, element);

        return mouseOver;
    }

    private void processElementHover(
            double mouseX,
            double mouseY,
            PhysElement element
    ) {
        if (element instanceof PhysHoverable hoverable)
            hoverable.updatePhysicalHover(mouseX, mouseY);
        if (element instanceof PhysElementContainer container)
            processElementHover(mouseX, mouseY, container.getElement());
        if (element instanceof PhysElementLister lister)
            for (PhysElement listedElement : lister.getElements())
                processElementHover(mouseX, mouseY, listedElement);
    }

    public boolean clickMouse(
            double mouseX,
            double mouseY
    ) {
        if (
                mouseX < 0.0 ||
                mouseY < 0.0 ||
                mouseX >= getScreenWidth() ||
                mouseY >= getScreenHeight()
        )
            return false;

        for (PhysClickable clickable : clickables)
            if (clickable.registerClick())
                return true;
        for (PhysElementContainer container : containers) {
            if (!(container.getElement() instanceof PhysClickable clickable))
                continue;
            if (clickable.registerClick())
                return true;
        }

        return false;
    }
}
