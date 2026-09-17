package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;

public class PhysRect implements PhysElement {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final int fillColor;
    private final int outlineColor;
    private final int outlineWidth;

    public PhysRect(
            int x,
            int y,
            int width,
            int height,
            int fillColor,
            int outlineColor,
            int outlineWidth
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
    }

    public PhysRect(
            int x,
            int y,
            int width,
            int height,
            int fillColor
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.outlineColor = 0x00000000;
        this.outlineWidth = 0;
    }


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + height,
                fillColor
        );

        if (outlineWidth > 0)
            renderOutline(guiGraphics);
    }

    private void renderOutline(PhysGuiGraphics guiGraphics) {
        guiGraphics.fill(
                x,
                y,
                x + Math.min(width, outlineWidth),
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + Math.min(height, outlineWidth),
                outlineColor
        );
        guiGraphics.fill(
                x + Math.max(0, width - outlineWidth),
                y,
                x + width,
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y + Math.max(0, height - outlineWidth),
                x + width,
                y + height,
                outlineColor
        );
    }
}
