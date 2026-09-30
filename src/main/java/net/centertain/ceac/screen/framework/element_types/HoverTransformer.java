package net.centertain.ceac.screen.framework.element_types;

public interface HoverTransformer {
    boolean isMouseOver(
            double mouseX,
            double mouseY
    );

    double transformMouseX(
            double mouseX,
            double mouseY
    );

    double transformMouseY(
            double mouseX,
            double mouseY
    );
}
