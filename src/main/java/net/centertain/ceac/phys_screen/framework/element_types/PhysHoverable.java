package net.centertain.ceac.phys_screen.framework.element_types;

public interface PhysHoverable {
    void updatePhysicalHover(
            double mouseX,
            double mouseY
    );

    boolean getIsHovered();
}
