package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Supplier;

public class PhysMarquee implements PhysElement {
    private int x;
    private int y;
    private int width;
    private PhysLabel label;
    private Supplier<Boolean> scrollCondition;

    private long marqueeStartTime;
    private boolean marqueeActive;

    public PhysMarquee(
            int width,
            @NotNull PhysLabel label,
            @NotNull Supplier<Boolean> scrollCondition
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.label = label;
        this.scrollCondition = scrollCondition;
    }

    public PhysMarquee(
            @NotNull PhysElement positionSupplier,
            int width,
            @NotNull PhysLabel label,
            @NotNull Supplier<Boolean> scrollCondition
    ) {
        this.x = positionSupplier.getWidth();
        this.y = positionSupplier.getHeight();
        this.width = width;
        this.label = label;
        this.scrollCondition = scrollCondition;
    }

    public PhysMarquee(
            int x,
            int y,
            int width,
            @NotNull PhysLabel label,
            @NotNull Supplier<Boolean> scrollCondition
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.label = label;
        this.scrollCondition = scrollCondition;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return label.getHeight();
    }
    public @NotNull PhysDimensions getDimensions() {
        return new PhysDimensions(x, y, width, getHeight());
    }
    public PhysLabel getLabel() {
        return label;
    }
    public Supplier<Boolean> getScrollCondition() {
        return scrollCondition;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {}
    public void setDimensions(@NotNull PhysElement dimensions) {
        this.x = dimensions.getX();
        this.y = dimensions.getY();
        this.width = dimensions.getWidth();
    }
    public void setLabel(@NotNull PhysLabel label) {
        this.label = label;
    }
    public void setScrollCondition(@NotNull Supplier<Boolean> scrollCondition) {
        this.scrollCondition = scrollCondition;
    }



    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        label.setX(x);
        label.setY(y);

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(label.getText());

        // Text fits, render label
        if (textWidth <= width) {
            marqueeActive = false;
            label.renderPhysical(guiGraphics);
            return;
        }

        // No scrolling, clip text
        if (!scrollCondition.get()) {
            marqueeActive = false;
            guiGraphics.drawStringClipped(
                    font,
                    label.getText(),
                    label.getX(),
                    label.getY(),
                    label.getColor(),
                    label.getShadow(),
                    label.getX(),
                    label.getX() + width
            );
            return;
        }

        long elapsed = System.currentTimeMillis() - marqueeStartTime;
        if (!marqueeActive) {
            marqueeActive = true;
            marqueeStartTime = System.currentTimeMillis();
            elapsed = 0;
        }

        float scrollOffset = getScrollOffset(elapsed, textWidth, width);
        guiGraphics.drawStringClipped(
                font,
                label.getText(),
                Math.round(label.getX() - scrollOffset),
                label.getY(),
                label.getColor(),
                label.getShadow(),
                label.getX(),
                label.getX() + width
        );
    }

    private static float getScrollOffset(
            long elapsed,
            int textWidth,
            int width
    ) {
        long duration = 2000;
        long pause = 1000;

        long cycle = duration + pause + duration + pause;
        long time = elapsed % cycle;
        int overflow = textWidth - width;
        if (time <= duration) {
            float progress = (float) time / duration;
            return overflow * progress;
        }
        if (time <= duration + pause)
            return overflow;
        if (time <= duration + pause + duration) {
            float progress = (float) (time - (duration + pause)) / duration;
            return overflow * (1.0f - progress);
        }

        return 0.0f;
    }
}
