package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PhysImage implements PhysElement {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final ResourceLocation texture;

    public PhysImage(
            int x,
            int y,
            int width,
            int height,
            ResourceLocation texture
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        guiGraphics.blit(
                texture,
                x,
                y,
                width,
                height
        );
    }
}
