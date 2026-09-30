package net.centertain.ceac.screen.framework;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    private final List<Element> elements = new ArrayList<>();

    private GuiEventListener focusedElement;

    protected Screen(Component title) {
        super(title);
    }

    protected final <T extends Element> T addElement(@NotNull T element) {
        elements.add(element);
        addRenderableOnly(element);
        return element;
    }

    protected final void clearElements() {
        if (focusedElement != null)
            focusedElement.setFocused(false);
        focusedElement = null;
        elements.clear();
        rebuildRenderables();
    }

    protected final List<Element> getElements() {
        return List.copyOf(elements);
    }
    protected final List<Element> getElementsMutable() {
        return elements;
    }

    protected final boolean removeElement(@NotNull Element element) {
        if (!elements.remove(element))
            return false;
        if (focusedElement == element) {
            focusedElement.setFocused(false);
            focusedElement = null;
        }
        rebuildRenderables();
        return true;
    }

    private void rebuildRenderables() {
        // Because 1.20.1 has no removeRenderable() method
        clearWidgets();
        for (Element element : elements)
            addRenderableOnly(element);
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (dispatchMouse(
                elements,
                listener -> listener.mouseClicked(mouseX, mouseY, button),
                true
        ))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (dispatchMouse(
                elements,
                listener -> listener.mouseReleased(mouseX, mouseY, button),
                false
        ))
            return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (dispatchMouse(
                elements,
                listener ->
                        listener.mouseDragged(mouseX, mouseY, button, dragX, dragY),
                false
        ))
            return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollDelta
    ) {
        if (dispatchMouse(
                elements,
                listener -> listener.mouseScrolled(mouseX, mouseY, scrollDelta),
                false
        ))
            return true;
        return super.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (focusedElement != null && focusedElement.keyPressed(keyCode, scanCode, modifiers))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (focusedElement != null && focusedElement.keyReleased(keyCode, scanCode, modifiers))
            return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(
            char codePoint,
            int modifiers
    ) {
        if (focusedElement != null && focusedElement.charTyped(codePoint, modifiers))
            return true;
        return super.charTyped(codePoint, modifiers);
    }

    private boolean dispatchMouse(
            @NotNull List<Element> elements,
            @NotNull MouseEvent event,
            boolean focusOnSuccess
    ) {
        Set<Element> visited = newIdentitySet();
        for (int i = elements.size() - 1; i >= 0; i--) {
            Element element = elements.get(i);
            if (dispatchMouse(
                    element,
                    event,
                    focusOnSuccess,
                    visited
            ))
                return true;
        }
        return false;
    }

    private boolean dispatchMouse(
            @NotNull Element element,
            @NotNull MouseEvent event,
            boolean focusOnSuccess,
            @NotNull Set<Element> visited
    ) {
        if (!visited.add(element))
            return false;

        if (element instanceof GuiEventListener listener) {
            if (!event.invoke(listener))
                return false;
            if (focusOnSuccess)
                setFocusedElement(listener);
            return true;
        }

        if (element instanceof ElementLister lister) {
            List<Element> children = lister.getElements();
            for (int i = children.size() - 1; i >= 0; i--)
                if (dispatchMouse(
                        children.get(i),
                        event,
                        focusOnSuccess,
                        visited
                ))
                    return true;
        }

        if (element instanceof ElementContainer container)
            return dispatchMouse(
                    container.getElement(),
                    event,
                    focusOnSuccess,
                    visited
            );

        return false;
    }

    private void setFocusedElement(
            @NotNull GuiEventListener element
    ) {
        if (focusedElement == element)
            return;
        if (focusedElement != null)
            focusedElement.setFocused(false);
        focusedElement = element;
        focusedElement.setFocused(true);
    }

    @FunctionalInterface
    private interface MouseEvent {
        boolean invoke(@NotNull GuiEventListener listener);
    }

    private static Set<Element> newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}