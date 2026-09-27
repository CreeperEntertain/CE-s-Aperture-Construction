package net.centertain.ceac.phys_screen.framework;

public interface PhysHoverable {
    void updatePhysicalHover(
            double mouseX,
            double mouseY
    );

    boolean getIsHovered();
}
