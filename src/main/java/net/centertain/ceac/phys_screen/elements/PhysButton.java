package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.centertain.ceac.screen.elements.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhysButton extends Button implements PhysElement {
    private boolean isHovered;

    private record TextureDimensions(
            int width,
            int height
    ) {}

    public PhysButton(
            int width,
            int height,
            Component text,
            int textColor,
            float textScale,
            int backgroundColor,
            int outlineColor,
            @Nullable ResourceLocation texture,
            Runnable onPress
    ) {
        super (
                0,
                0,
                width,
                height,
                text,
                textColor,
                textScale,
                backgroundColor,
                outlineColor,
                texture,
                onPress
        );
    }
    public PhysButton(
            int x,
            int y,
            int width,
            int height,
            Component text,
            int textColor,
            float textScale,
            int backgroundColor,
            int outlineColor,
            @Nullable ResourceLocation texture,
            Runnable onPress
    ) {
        super (
                x,
                y,
                width,
                height,
                text,
                textColor,
                textScale,
                backgroundColor,
                outlineColor,
                texture,
                onPress
        );
    }

    @Override
    protected void renderWidget(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
    }


    public void updatePhysicalHover(
            double mouseX,
            double mouseY
    ) {
        isHovered =
                mouseX >= getX() &&
                mouseY >= getY() &&
                mouseX < getX() + getWidth() &&
                mouseY < getY() + getHeight();
    }


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        renderBackground(guiGraphics);

        Font font = Minecraft.getInstance().font;

        int textWidth = font.width(getMessage());
        int textHeight = 8;

        int scaledTextWidth = Math.round(textWidth * textScale);
        int scaledTextHeight = Math.round(textHeight * textScale);

        TextureDimensions textureDimensions = getTextureDimensions(scaledTextHeight);

        int contentHeight = getContentHeight(textureDimensions.height(), scaledTextHeight);

        int contentTop = getY() + (getHeight() - contentHeight) / 2;

        contentTop = renderTexture(
                guiGraphics,
                textureDimensions,
                contentTop
        );

        renderText(
                guiGraphics,
                font,
                scaledTextWidth,
                contentTop
        );

        renderHoverOutline(guiGraphics);
    }

    private void renderBackground(PhysGuiGraphics guiGraphics) {
        guiGraphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                backgroundColor
        );
    }

    private TextureDimensions getTextureDimensions(int scaledTextHeight) {
        if (texture == null)
            return new TextureDimensions(0, 0);

        int availableTextureWidth = getWidth();
        int availableTextureHeight = getHeight() - scaledTextHeight - GuiConstants.ELEMENT_PADDING;

        if (availableTextureWidth <= 0 || availableTextureHeight <= 0)
            return new TextureDimensions(0, 0);

        float scale = Math.min(
                (float) availableTextureWidth / textureWidth,
                (float) availableTextureHeight / textureHeight
        );

        int drawTextureWidth = Math.max(1, Math.round(textureWidth * scale));
        int drawTextureHeight = Math.max(1, Math.round(textureHeight * scale));

        return new TextureDimensions(drawTextureWidth, drawTextureHeight);
    }

    private int getContentHeight(
            int drawTextureHeight,
            int scaledTextHeight
    ) {
        int contentHeight = drawTextureHeight;

        if (drawTextureHeight > 0)
            contentHeight += GuiConstants.ELEMENT_PADDING;

        contentHeight += scaledTextHeight;

        return contentHeight;
    }

    private int renderTexture(
            PhysGuiGraphics guiGraphics,
            TextureDimensions textureDimensions,
            int contentTop
    ) {
        if (textureDimensions.height() <= 0)
            return contentTop;

        int textureX = getX() + (getWidth() - textureDimensions.width()) / 2;

        guiGraphics.blit(
                texture,
                textureX,
                contentTop,
                textureDimensions.width(),
                textureDimensions.height()
        );

        return contentTop + textureDimensions.height() + GuiConstants.ELEMENT_PADDING;
    }

    private void renderText(
            PhysGuiGraphics guiGraphics,
            Font font,
            int scaledTextWidth,
            int contentTop
    ) {
        int textAreaLeft = getX() + GuiConstants.ELEMENT_PADDING;
        int textAreaRight = getX() + getWidth() - GuiConstants.ELEMENT_PADDING;
        int textAreaWidth = textAreaRight - textAreaLeft;

        if (scaledTextWidth <= textAreaWidth)
            renderNormalText(
                    guiGraphics,
                    font,
                    contentTop,
                    textAreaLeft,
                    textAreaWidth
            );
        else if (!isHovered)
            renderTruncatedText(
                    guiGraphics,
                    font,
                    contentTop,
                    textAreaLeft,
                    textAreaWidth
            );
        else
            renderMarquee(
                    guiGraphics,
                    font,
                    contentTop,
                    textAreaLeft,
                    textAreaWidth,
                    scaledTextWidth
            );

        if (!isHovered)
            marqueeActive = false;
    }

    private void renderNormalText(
            PhysGuiGraphics guiGraphics,
            Font font,
            int contentTop,
            int textAreaLeft,
            int textAreaWidth
    ) {
        int scaledTextWidth = Math.round(font.width(getMessage()) * textScale);
        int textX = textAreaLeft + (textAreaWidth - scaledTextWidth) / 2;

        drawScaledText(
                guiGraphics,
                font,
                getMessage(),
                textX,
                contentTop
        );
    }

    private void renderTruncatedText(
            PhysGuiGraphics guiGraphics,
            Font font,
            int contentTop,
            int textAreaLeft,
            int textAreaWidth
    ) {
        String truncatedText = getTruncatedText(font, textAreaWidth);

        int truncatedTextWidth = Math.round(font.width(truncatedText) * textScale);
        int textX = textAreaLeft + (textAreaWidth - truncatedTextWidth) / 2;

        drawScaledText(
                guiGraphics,
                font,
                Component.literal(truncatedText),
                textX,
                contentTop
        );
    }

    private void renderMarquee(
            PhysGuiGraphics guiGraphics,
            Font font,
            int contentTop,
            int textAreaLeft,
            int textAreaWidth,
            int scaledTextWidth
    ) {
        long elapsed = System.currentTimeMillis() - marqueeStartTime;

        if (!marqueeActive) {
            marqueeActive = true;
            marqueeStartTime = System.currentTimeMillis();
            elapsed = 0;
        }

        float scrollOffset = getScrollOffset(
                elapsed,
                scaledTextWidth,
                textAreaWidth
        );

        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(
                textAreaLeft - scrollOffset,
                contentTop,
                0.0
        );
        guiGraphics.pose().scale(
                textScale,
                textScale,
                1.0f
        );
        guiGraphics.drawStringClipped(
                font,
                getMessage(),
                0,
                0,
                textColor,
                false,
                scrollOffset / textScale,
                (scrollOffset + textAreaWidth) / textScale
        );

        guiGraphics.pose().popPose();
    }

    private void drawScaledText(
            PhysGuiGraphics guiGraphics,
            Font font,
            Component text,
            int textX,
            int textY
    ) {
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(
                textX,
                textY,
                0.0
        );
        guiGraphics.pose().scale(
                textScale,
                textScale,
                1.0f
        );
        guiGraphics.drawString(
                font,
                text,
                0,
                0,
                textColor,
                false
        );

        guiGraphics.pose().popPose();
    }

    private void renderHoverOutline(PhysGuiGraphics guiGraphics) {
        if (!isHovered)
            return;

        guiGraphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + 1,
                outlineColor
        );
        guiGraphics.fill(
                getX(),
                getY() + getHeight() - 1,
                getX() + getWidth(),
                getY() + getHeight(),
                outlineColor
        );
        guiGraphics.fill(
                getX(),
                getY(),
                getX() + 1,
                getY() + getHeight(),
                outlineColor
        );
        guiGraphics.fill(
                getX() + getWidth() - 1,
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                outlineColor
        );
    }

    private String getTruncatedText(
            Font font,
            int availableWidth
    ) {
        String text = getMessage().getString();
        String ellipsis = "...";

        if (Math.round(font.width(text) * textScale) <= availableWidth)
            return text;

        int ellipsisWidth = Math.round(font.width(ellipsis) * textScale);
        int availableTextWidth = availableWidth - ellipsisWidth;

        if (availableTextWidth <= 0)
            return ellipsis;

        int characterCount = 0;

        while(
                characterCount < text.length() &&
                Math.round(font.width(text.substring(0, characterCount + 1)) * textScale) <= availableWidth
        )
            characterCount++;

        return text.substring(0, characterCount) + ellipsis;
    }

    private static float getScrollOffset(
            long elapsed,
            int scaledTextWidth,
            int textAreaWidth
    ) {
        float scrollOffset;

        long duration = 2000;
        long pause = 1000;

        long cycle = duration + pause + duration + pause;

        long time = elapsed % cycle;

        int overflow = scaledTextWidth - textAreaWidth;

        if (time <= duration) {
            float progress = (float) time / duration;
            scrollOffset = overflow * progress;
        } else if (time <= duration + pause)
            scrollOffset = overflow;
        else if (time <= duration + pause + duration) {
            float progress = (float) (time - (duration + pause)) / duration;
            scrollOffset = overflow * (1.0f - progress);
        } else
            scrollOffset = 0.0f;

        return scrollOffset;
    }
}
