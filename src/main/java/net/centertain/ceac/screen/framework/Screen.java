package net.centertain.ceac.screen.framework;

import net.centertain.ceac.screen.framework.element_types.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    private final List<Element> elements = new ArrayList<>();

    private GuiEventListener focusedElement;

    private MouseDispatchResult mouseCapture;

    protected Screen(Component title) {
        super(title);
    }

    @SuppressWarnings("UnusedReturnValue")
    protected final <T extends Element> T addElement(@NotNull T element) {
        elements.add(element);
        addRenderableOnly(element);
        return element;
    }

    protected final void clearElements() {
        if (focusedElement != null)
            focusedElement.setFocused(false);
        focusedElement = null;
        mouseCapture = null;
        elements.clear();
        rebuildRenderables();
    }

    protected final List<Element> getElements() {
        return List.copyOf(elements);
    }
    protected final List<Element> getElementsMutable() {
        return elements;
    }

    @SuppressWarnings("UnusedReturnValue")
    protected final boolean removeElement(@NotNull Element element) {
        if (!elements.remove(element))
            return false;
        validateInputState();
        rebuildRenderables();
        return true;
    }

    private void rebuildRenderables() {
        // Because 1.20.1 has no removeRenderable() method
        clearWidgets();
        for (Element element : elements)
            addRenderableOnly(element);
    }

    protected void build() {}

    @Override
    protected void init() {
        super.init();
        clearElements();
        build();
    }

    @Override
    public void removed() {
        if (focusedElement != null)
            focusedElement.setFocused(false);
        focusedElement = null;
        mouseCapture = null;
        super.removed();
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        validateInputState();
        updateHover(mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void updateHover(
            double mouseX,
            double mouseY
    ) {
        Set<Element> visited = newIdentitySet();

        for (Element element : elements)
            updateHover(
                    element,
                    mouseX,
                    mouseY,
                    null,
                    visited
            );
    }

    private void updateHover(
            @NotNull Element element,
            double mouseX,
            double mouseY,
            Supplier<Boolean> parentHover,
            @NotNull Set<Element> visited
    ) {
        if (!visited.add(element))
            return;
        if (element instanceof Reactable reactable)
            reactable.parentHover(parentHover != null ? parentHover : () -> false);

        Supplier<Boolean> childHover = parentHover;

        if (element instanceof Hoverable hoverable) {
            hoverable.updateHover(mouseX, mouseY);
            childHover = hoverable::getIsHovered;
        }

        double childMouseX = mouseX;
        double childMouseY = mouseY;

        if (element instanceof HoverTransformer transformer)
            if (!transformer.isMouseOver(mouseX, mouseY)) {
                childMouseX = Double.NaN;
                childMouseY = Double.NaN;
            } else {
                childMouseX = transformer.transformMouseX(mouseX, mouseY);
                childMouseY = transformer.transformMouseY(mouseX, mouseY);
            }

        if (element instanceof ElementLister lister)
            for (Element child : lister.getElements())
                updateHover(child, childMouseX, childMouseY, childHover, visited);

        if (element instanceof ElementContainer container)
            updateHover(container.getElement(), childMouseX, childMouseY, childHover, visited);
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        validateInputState();
        mouseCapture = null;
        MouseDispatchResult result = dispatchMouse(
                elements,
                mouseX,
                mouseY,
                (listener, x, y) -> listener.mouseClicked(x, y, button)
        );
        if (result != null) {
            setFocusedElement((GuiEventListener) result.element());
            mouseCapture = createMouseCapture(result);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (isMouseCaptureValid()) {
            MouseDispatchResult result = dispatchCapturedMouse(
                    mouseCapture,
                    mouseX,
                    mouseY,
                    (listener, x, y) -> listener.mouseReleased(x, y, button)
            );
            mouseCapture = null;
            if (result != null)
                return true;
        } else
            mouseCapture = null;
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
        if (
                isMouseCaptureValid() &&
                dispatchCapturedMouse(
                        mouseCapture,
                        mouseX,
                        mouseY,
                        (listener, x, y) ->
                                listener.mouseDragged(x, y, button, dragX, dragY)
                ) != null
        )
            return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollDelta
    ) {
        MouseDispatchResult result = dispatchMouse(
                elements,
                mouseX,
                mouseY,
                (listener, x, y) -> listener.mouseScrolled(x, y, scrollDelta)
        );
        if (result != null)
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

    private MouseDispatchResult dispatchMouse(
            @NotNull List<Element> elements,
            double mouseX,
            double mouseY,
            @NotNull MouseEvent event
    ) {
        Set<Element> visited = newIdentitySet();
        List<Element> path = new ArrayList<>();

        for (int i = elements.size() - 1; i >= 0; i--) {
            MouseDispatchResult result = dispatchMouse(
                    elements.get(i),
                    mouseX,
                    mouseY,
                    event,
                    path,
                    visited
            );

            if (result != null)
                return result;
        }

        return null;
    }

    private MouseDispatchResult dispatchMouse(
            @NotNull Element element,
            double mouseX,
            double mouseY,
            @NotNull MouseEvent event,
            @NotNull List<Element> path,
            @NotNull Set<Element> visited
    ) {
        if (!visited.add(element))
            return null;
        path.add(element);
        if (element instanceof HoverTransformer transformer)
            if (!transformer.isMouseOver(mouseX, mouseY)) {
                path.remove(path.size() - 1);
                return null;
            }
        if (element instanceof GuiEventListener listener)
            if (event.invoke(listener, mouseX, mouseY))
                return new MouseDispatchResult(element, List.copyOf(path));

        double childMouseX = mouseX;
        double childMouseY = mouseY;

        if (element instanceof HoverTransformer transformer) {
            childMouseX = transformer.transformMouseX(mouseX, mouseY);
            childMouseY = transformer.transformMouseY(mouseX, mouseY);
        }

        if (element instanceof ElementLister lister) {
            List<Element> children = lister.getElements();
            for (int i = children.size() - 1; i >= 0; i--) {
                MouseDispatchResult result = dispatchMouse(
                        children.get(i),
                        childMouseX,
                        childMouseY,
                        event,
                        path,
                        visited
                );
                if (result != null)
                    return result;
            }
        }
        if (element instanceof ElementContainer container) {
            MouseDispatchResult result = dispatchMouse(
                    container.getElement(),
                    childMouseX,
                    childMouseY,
                    event,
                    path,
                    visited
            );
            if (result != null)
                return result;
        }
        path.remove(path.size() - 1);
        return null;
    }

    private void setFocusedElement(
            @NotNull GuiEventListener element
    ) {
        GuiEventListener target = resolveFocusedElement(element);
        if (focusedElement == target)
            return;
        if (focusedElement != null)
            focusedElement.setFocused(false);
        focusedElement = target;
        focusedElement.setFocused(true);
    }

    private GuiEventListener resolveFocusedElement(
            @NotNull GuiEventListener element
    ) {
        return resolveFocusedElement(element, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private GuiEventListener resolveFocusedElement(
            @NotNull GuiEventListener element,
            @NotNull Set<GuiEventListener> visited
    ) {
        if (!visited.add(element))
            return element;
        if (!(element instanceof FocusContainer container))
            return element;
        GuiEventListener child = container.getFocusedElement();
        if (child == null)
            return element;
        return resolveFocusedElement(child, visited);
    }

    private void validateInputState() {
        if (focusedElement instanceof Element focused && !containsElement(elements, focused, newIdentitySet())) {
            focusedElement.setFocused(false);
            focusedElement = null;
        }
        if (mouseCapture != null && !containsElement(elements, mouseCapture.element(), newIdentitySet()))
            mouseCapture = null;
    }

    private boolean isMouseCaptureValid() {
        if (mouseCapture == null)
            return false;
        if (!containsElement(elements, mouseCapture.element(), newIdentitySet())) {
            mouseCapture = null;
            return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean containsElement(
            @NotNull List<Element> elements,
            @NotNull Element target,
            @NotNull Set<Element> visited
    ) {
        for (Element element : elements)
            if (containsElement(element, target, visited))
                return true;
        return false;
    }

    private boolean containsElement(
            @NotNull Element element,
            @NotNull Element target,
            @NotNull Set<Element> visited
    ) {
        if (!visited.add(element))
            return false;
        if (element == target)
            return true;
        if (element instanceof ElementLister lister)
            for (Element child : lister.getElements())
                if (containsElement(child, target, visited))
                    return true;
        if (element instanceof ElementContainer container)
            return containsElement(container.getElement(), target, visited);
        return false;
    }

    private List<Element> findElementPath(
            @NotNull Element target
    ) {
        Set<Element> visited = newIdentitySet();
        for (Element element : elements) {
            List<Element> path = new ArrayList<>();
            if (findElementPath(element, target, path, visited))
                return List.copyOf(path);
        }
        return null;
    }

    private boolean findElementPath(
            @NotNull Element element,
            @NotNull Element target,
            @NotNull List<Element> path,
            @NotNull Set<Element> visited
    ) {
        if (!visited.add(element))
            return false;
        path.add(element);
        if (element == target)
            return true;
        if (element instanceof ElementLister lister)
            for (Element child : lister.getElements())
                if (findElementPath(child, target, path, visited))
                    return true;
        if (element instanceof ElementContainer container)
            if (findElementPath(container.getElement(), target, path, visited))
                return true;
        path.remove(path.size() - 1);
        return false;
    }

    private MouseDispatchResult createMouseCapture(
            @NotNull MouseDispatchResult result
    ) {
        List<Element> path = findElementPath(result.element());
        if (path == null)
            return result;
        return new MouseDispatchResult(result.element(), path);
    }

    private MouseDispatchResult dispatchCapturedMouse(
            @NotNull MouseDispatchResult capture,
            double mouseX,
            double mouseY,
            @NotNull MouseEvent event
    ) {
        double currentMouseX = mouseX;
        double currentMouseY = mouseY;

        for (Element element : capture.path()) {
            if (element == capture.element()) {
                if (!event.invoke((GuiEventListener) capture.element(), currentMouseX, currentMouseY))
                    return null;
                return capture;
            }
            if (element instanceof HoverTransformer transformer) {
                double nextMouseX = transformer.transformMouseX(currentMouseX, currentMouseY);
                double nextMouseY = transformer.transformMouseY(currentMouseX, currentMouseY);

                currentMouseX = nextMouseX;
                currentMouseY = nextMouseY;
            }
        }

        return null;
    }

    @FunctionalInterface
    private interface MouseEvent {
        boolean invoke(
                @NotNull GuiEventListener listener,
                double mouseX,
                double mouseY
        );
    }

    private record MouseDispatchResult(
            @NotNull Element element,
            @NotNull List<Element> path
    ) {}

    private static Set<Element> newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}