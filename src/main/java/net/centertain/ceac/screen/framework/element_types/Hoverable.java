package net.centertain.ceac.screen.framework.element_types;

public interface Hoverable {
    void updateHover(
            double mouseX,
            double mouseY
    );

    boolean getIsHovered();
}
