package net.centertain.ceac.screen.elements;

import net.centertain.ceac.GuiConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ScrollContainer implements Renderable, GuiEventListener {
    private final Renderable content;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int contentHeight;
    private final int scrollSpeed;

    private double scrollOffset;

    public ScrollContainer(
            int x,
            int y,
            int width,
            int height,
            Renderable content,
            int contentHeight,
            int scrollSpeed
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.content = content;
        this.contentHeight = contentHeight;
        this.scrollSpeed = scrollSpeed;
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.enableScissor(x, y, x + width, y + height);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0, -scrollOffset, 0.0);
        content.render(
                guiGraphics,
                mouseX,
                (int) (mouseY + scrollOffset),
                partialTick
        );
        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();

        if (contentHeight > height) {
            int scrollbarWidth = GuiConstants.SCROLL_BAR_WIDTH;
            int scrollbarX = x + width - scrollbarWidth;
            int scrollbarHeight = Math.max(1, (int) ((double) height * height / contentHeight));
            int scrollbarTravel = height - scrollbarHeight;
            double maxScroll = contentHeight - height;
            int scrollbarY = y + (int) (maxScroll > 0 ? scrollbarTravel * (scrollOffset / maxScroll) : 0);

            guiGraphics.fill(
                    scrollbarX,
                    scrollbarY,
                    scrollbarX + scrollbarWidth,
                    scrollbarY + scrollbarHeight,
                    GuiConstants.COLOR_SOLID_LIGHT_GRAY
            );
        }
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollDelta
    ) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height)
            return false;

        double maxScroll = Math.max(0, contentHeight - height);

        scrollOffset = Mth.clamp(
                scrollOffset - scrollDelta * scrollSpeed,
                0.0,
                maxScroll
        );

        return true;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
