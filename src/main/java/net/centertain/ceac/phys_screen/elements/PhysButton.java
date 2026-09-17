package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.screen.elements.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhysButton extends Button {
    private boolean isHovered;

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


    public void renderPhysical(
            PhysGuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                backgroundColor
        );

        Font font = Minecraft.getInstance().font;

        int textWidth = font.width(getMessage());
        int textHeight = 8;

        int scaledTextWidth = Math.round(textWidth * textScale);
        int scaledTextHeight = Math.round(textHeight * textScale);

        int drawTextureWidth = 0;
        int drawTextureHeight = 0;

        if (texture != null) {
            int availableTextureWidth = getWidth();
            int availableTextureHeight = getHeight()
                    - scaledTextHeight
                    - GuiConstants.ELEMENT_PADDING;

            if (availableTextureWidth > 0 && availableTextureHeight > 0) {
                float scale = Math.min(
                        (float) availableTextureWidth / textureWidth,
                        (float) availableTextureHeight / textureHeight
                );

                drawTextureWidth = Math.max(1, Math.round(textureWidth * scale));
                drawTextureHeight = Math.max(1, Math.round(textureHeight * scale));
            }
        }

        int contentHeight = drawTextureHeight;

        if (drawTextureHeight > 0)
            contentHeight += GuiConstants.ELEMENT_PADDING;

        contentHeight += scaledTextHeight;

        int contentTop = getY() + (getHeight() - contentHeight) / 2;

        if (drawTextureHeight > 0) {
            int textureX = getX() + (getHeight() - contentHeight) / 2;

            guiGraphics.blit(
                    texture,
                    textureX,
                    contentTop,
                    drawTextureWidth,
                    drawTextureHeight
            );

            contentTop += drawTextureHeight + GuiConstants.ELEMENT_PADDING;
        }

        int textAreaLeft = getX() + GuiConstants.ELEMENT_PADDING;
        int textAreaRight = getX() + getWidth() - GuiConstants.ELEMENT_PADDING;
        int textAreaWidth = textAreaRight - textAreaLeft;

        if (scaledTextWidth <= textAreaWidth) {
            int textX = textAreaLeft + (textAreaWidth - scaledTextWidth) / 2;

            guiGraphics.pose().pushPose();

            guiGraphics.pose().translate(
                    textX,
                    contentTop,
                    0.0
            );
            guiGraphics.pose().scale(
                    textScale,
                    textScale,
                    1.0f
            );
            guiGraphics.drawString(
                    font,
                    getMessage(),
                    0,
                    0,
                    textColor,
                    false
            );

            guiGraphics.pose().popPose();
        } else if (!isHovered) {
            String truncatedText = getTruncatedText(font, textAreaWidth);

            int truncatedTextWidth = Math.round(font.width(truncatedText) * textScale);
            int textX = textAreaLeft + (textAreaWidth - truncatedTextWidth) / 2;

            guiGraphics.pose().pushPose();

            guiGraphics.pose().translate(
                    textX,
                    contentTop,
                    0.0
            );
            guiGraphics.pose().scale(
                    textScale,
                    textScale,
                    1.0f
            );
            guiGraphics.drawString(
                    font,
                    Component.literal(truncatedText),
                    0,
                    0,
                    textColor,
                    false
            );

            guiGraphics.pose().popPose();
        } else { // Marquee
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

            // Physical screen is billboarded

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

         if (!isHovered)
             marqueeActive = false;
         else {
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
