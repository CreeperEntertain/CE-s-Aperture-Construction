package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.Reactable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public class MarqueeLabel implements Element, Reactable {
    private int x;
    private int y;
    private int width;
    private @NotNull Component text;
    private @NotNull Alignment alignment;
    private int textColor;
    private float textScale;
    private boolean shadow;
    private @Nullable Supplier<@NotNull Boolean> scrollCondition;
    private long marqueeStartTime;
    private boolean marqueeActive;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Component> dynamicText = null;
    private @Nullable Supplier<@NotNull Alignment> dynamicAlignment = null;
    private @Nullable Supplier<@NotNull Integer> dynamicTextColor = null;
    private @Nullable Supplier<@NotNull Float> dynamicTextScale = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicShadow = null;
    private @Nullable Supplier<@Nullable Supplier<@NotNull Boolean>> dynamicScrollCondition = null;

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public MarqueeLabel(
            int width,
            @NotNull Component text,
            @NotNull Alignment alignment,
            int textColor,
            float textScale,
            boolean shadow
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.text = text;
        this.alignment = alignment;
        this.textColor = textColor;
        this.textScale = textScale;
        this.shadow = shadow;
    }

    public MarqueeLabel(
            @NotNull Element positionSupplier,
            int width,
            @NotNull Component text,
            @NotNull Alignment alignment,
            int textColor,
            float textScale,
            boolean shadow
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.text = text;
        this.alignment = alignment;
        this.textColor = textColor;
        this.textScale = textScale;
        this.shadow = shadow;
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
        return Math.round(8 * textScale);
    }
    public @NotNull Component getText() {
        return text;
    }
    public @NotNull Alignment getAlignment() {
        return alignment;
    }
    public int getTextColor() {
        return textColor;
    }
    public float getTextScale() {
        return textScale;
    }
    public boolean getShadow() {
        return shadow;
    }
    public @Nullable Supplier<@NotNull Boolean> getScrollCondition() {
        return scrollCondition;
    }

    public @Nullable Supplier<@NotNull Integer> getDynamicX() {
        return dynamicX;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicY() {
        return dynamicY;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicWidth() {
        return dynamicWidth;
    }
    public @Nullable Supplier<@NotNull Component> getDynamicText() {
        return dynamicText;
    }
    public @Nullable Supplier<@NotNull Alignment> getDynamicAlignment() {
        return dynamicAlignment;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicTextColor() {
        return dynamicTextColor;
    }
    public @Nullable Supplier<@NotNull Float> getDynamicTextScale() {
        return dynamicTextScale;
    }
    public @Nullable Supplier<@NotNull Boolean> getDynamicShadow() {
        return dynamicShadow;
    }
    public @Nullable Supplier<@Nullable Supplier<@NotNull Boolean>> getDynamicScrollCondition() {
        return dynamicScrollCondition;
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
    public void setText(Component text) {
        this.text = text;
        marqueeActive = false;
    }
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        marqueeActive = false;
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    public void setTextScale(float textScale) {
        this.textScale = textScale;
        marqueeActive = false;
    }
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }
    public void setScrollCondition(@Nullable Supplier<@NotNull Boolean> scrollCondition) {
        this.scrollCondition = scrollCondition;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
    }

    public void setDynamicX(@Nullable Supplier<@NotNull Integer> dynamicX) {
        this.dynamicX = dynamicX;
    }
    public void setDynamicY(@Nullable Supplier<@NotNull Integer> dynamicY) {
        this.dynamicY = dynamicY;
    }
    public void setDynamicWidth(@Nullable Supplier<@NotNull Integer> dynamicWidth) {
        this.dynamicWidth = dynamicWidth;
    }
    public void setDynamicText(@Nullable Supplier<@NotNull Component> dynamicText) {
        this.dynamicText = dynamicText;
    }
    public void setDynamicAlignment(@Nullable Supplier<@NotNull Alignment> dynamicAlignment) {
        this.dynamicAlignment = dynamicAlignment;
    }
    public void setDynamicTextColor(@Nullable Supplier<@NotNull Integer> dynamicTextColor) {
        this.dynamicTextColor = dynamicTextColor;
    }
    public void setDynamicTextScale(@Nullable Supplier<@NotNull Float> dynamicTextScale) {
        this.dynamicTextScale = dynamicTextScale;
    }
    public void setDynamicShadow(@Nullable Supplier<@NotNull Boolean> dynamicShadow) {
        this.dynamicShadow = dynamicShadow;
    }
    public void setDynamicScrollCondition(@Nullable Supplier<@Nullable Supplier<@NotNull Boolean>> dynamicScrollCondition) {
        this.dynamicScrollCondition = dynamicScrollCondition;
    }


    public void parentHover(@NotNull Supplier<Boolean> parentHoverState) {
        this.scrollCondition = parentHoverState;
    }

    private String getTruncatedText(
            Font font,
            int availableWidth
    ) {
        String text = this.text.getString();
        String ellipsis = "...";
        if (Math.round(font.width(text) * textScale) <= availableWidth)
            return text;
        int ellipsisWidth = Math.round(font.width(ellipsis) * textScale);
        int availableTextWidth = availableWidth - ellipsisWidth;
        if (availableTextWidth <= 0)
            return ellipsis;
        int characterCount = 0;
        while (
                characterCount < text.length() &&
                Math.round(font.width(text.substring(0, characterCount + 1)) * textScale) <= availableTextWidth
        )
            characterCount++;
        return text.substring(0, characterCount) + ellipsis;
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicText != null) setText(dynamicText.get());
        if (dynamicTextScale != null) setTextScale(dynamicTextScale.get());
        if (dynamicTextColor != null) setTextColor(dynamicTextColor.get());
        if (dynamicShadow != null) setShadow(dynamicShadow.get());
        if (dynamicScrollCondition != null) setScrollCondition(dynamicScrollCondition.get());
        if (dynamicAlignment != null) setAlignment(dynamicAlignment.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        Font font = Minecraft.getInstance().font;
        int textWidth = Math.round(font.width(text) * textScale);

        if (textWidth <= width) {
            marqueeActive = false;
            renderText(
                    guiGraphics,
                    text,
                    getAlignedTextX(textWidth),
                    y
            );
            return;
        }

        boolean shouldScroll = scrollCondition != null && scrollCondition.get();
        if (!shouldScroll) {
            marqueeActive = false;
            String truncatedText = getTruncatedText(font, width);
            int truncatedTextWidth = Math.round(font.width(truncatedText) * textScale);
            renderText(
                    guiGraphics,
                    Component.literal(truncatedText),
                    x + (width - truncatedTextWidth) / 2.0f,
                    y
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

        Matrix4f pose = guiGraphics.pose().last().pose();

        int scissorX = Math.round(x + pose.m30());
        int scissorY = Math.round(y + pose.m31());

        guiGraphics.enableScissor(
                scissorX,
                scissorY,
                scissorX + width,
                scissorY + getHeight()
        );
        renderText(
                guiGraphics,
                text,
                x - scrollOffset,
                y
        );

        guiGraphics.disableScissor();
    }

    private float getAlignedTextX(int textWidth) {
        return switch (alignment) {
            case LEFT -> x;
            case CENTER -> x + (width - textWidth) / 2.0f;
            case RIGHT -> x + width - textWidth;
        };
    }

    private void renderText(
            GuiGraphics guiGraphics,
            Component text,
            float x,
            int y
    ) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0);
        guiGraphics.pose().scale(textScale, textScale, 1.0f);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                text,
                0,
                0,
                textColor,
                shadow
        );
        guiGraphics.pose().popPose();
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