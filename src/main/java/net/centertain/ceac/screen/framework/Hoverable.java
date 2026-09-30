package net.centertain.ceac.screen.framework;

public interface Hoverable {
    void updateHover(
            double mouseX,
            double mouseY
    );

    boolean getIsHovered();
}
