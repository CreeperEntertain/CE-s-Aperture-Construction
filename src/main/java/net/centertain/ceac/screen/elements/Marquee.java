package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Marquee implements Element {
    private int x;
    private int y;
    private int width;

    private Label label;
    private Supplier<Boolean> scrollCondition;

    private long marqueeStartTime;
    private boolean marqueeActive;

    public Marquee(
            int width,
            @NotNull Label label
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.label = label;
    }

    public Marquee(
            @NotNull Element positionSupplier,
            int width,
            @NotNull Label label
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.label = label;
    }

    public Marquee(
            int x,
            int y,
            int width,
            @NotNull Label label
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.label = label;
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
    public @NotNull Label getLabel() {
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
    public void setLabel(@NotNull Label label) {
        this.label = label;
        marqueeActive = false;
    }
    public void setScrollCondition(@NotNull Supplier<Boolean> scrollCondition) {
        this.scrollCondition = scrollCondition;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
    }


    public void parentHover(@NotNull Supplier<Boolean> parentHoverState) {
        this.scrollCondition = parentHoverState;
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        label.setX(x);
        label.setY(y);

        Font font = Minecraft.getInstance().font;
        int textWidth = label.getWidth();

        if (textWidth <= width) {
            marqueeActive = false;

            label.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            return;
        }

        boolean shouldScroll = scrollCondition != null && scrollCondition.get();
        if (!shouldScroll) {
            marqueeActive = false;

            guiGraphics.enableScissor(
                    x,
                    y,
                    x + width,
                    y + label.getHeight()
            );

            label.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            guiGraphics.disableScissor();

            return;
        }

        long elapsed = System.currentTimeMillis() - marqueeStartTime;

        if (!marqueeActive) {
            marqueeActive = true;
            marqueeStartTime = System.currentTimeMillis();
            elapsed = 0;
        }

        float scrollOffset = getScrollOffset(
                elapsed,
                textWidth,
                width
        );

        guiGraphics.enableScissor(
                x,
                y,
                x + width,
                y + label.getHeight()
        );

        guiGraphics.drawString(
                font,
                label.getText(),
                Math.round(x - scrollOffset),
                y,
                label.getColor(),
                label.getShadow()
        );

        guiGraphics.disableScissor();
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
