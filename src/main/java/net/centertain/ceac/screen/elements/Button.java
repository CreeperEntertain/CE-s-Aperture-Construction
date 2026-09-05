package net.centertain.ceac.screen.elements;

import com.mojang.blaze3d.platform.NativeImage;
import net.centertain.ceac.GuiConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class Button extends AbstractWidget {
    private final int textColor;
    private final float textScale;
    private final int backgroundColor;
    private final int outlineColor;
    private final @Nullable ResourceLocation texture;
    private final int textureWidth;
    private final int textureHeight;
    private final Runnable onPress;

    private long marqueeStartTime;
    private boolean marqueeActive;

    public Button(
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
        super(x, y, width, height, text);

        this.textColor = textColor;
        this.textScale = textScale;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.texture = texture;
        this.onPress = onPress;

        if (texture != null) {
            try {
                Resource resource = Minecraft.getInstance()
                        .getResourceManager()
                        .getResource(texture)
                        .orElseThrow();
                try (InputStream stream = resource.open()) {
                    NativeImage image = NativeImage.read(stream);
                    this.textureWidth = image.getWidth();
                    this.textureHeight = image.getHeight();
                    image.close();
                }
            } catch (IOException exception) {
                throw new RuntimeException(
                        "Failed to load button texture " + texture,
                        exception
                );
            }
        } else {
            this.textureWidth = 0;
            this.textureHeight = 0;
        }
    }

    private String getTruncatedText(Font font, int availableWidth) {
        String text = getMessage().getString();
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

    @Override
    protected void renderWidget(
            @NotNull GuiGraphics guiGraphics,
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
            int availableTextureHeight = getHeight() - scaledTextHeight - GuiConstants.ELEMENT_PADDING;

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
            float textureScale = (float) drawTextureWidth / textureWidth;
            int textureX = getX() + (getWidth() - drawTextureWidth) / 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textureX, contentTop, 0.0);
            guiGraphics.pose().scale(textureScale, textureScale, 1.0f);
            guiGraphics.blit(
                    texture,
                    0,
                    0,
                    0,
                    0,
                    textureWidth,
                    textureHeight,
                    textureWidth,
                    textureHeight
            );
            guiGraphics.pose().popPose();

            contentTop += drawTextureHeight + GuiConstants.ELEMENT_PADDING;
        }

        int textAreaLeft = getX() + GuiConstants.ELEMENT_PADDING;
        int textAreaRight = getX() + getWidth() - GuiConstants.ELEMENT_PADDING;
        int textAreaWidth = textAreaRight - textAreaLeft;

        if (scaledTextWidth <= textAreaWidth) {
            int textX = textAreaLeft + (textAreaWidth - scaledTextWidth) / 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textX, contentTop, 0.0);
            guiGraphics.pose().scale(textScale, textScale, 1.0f);
            guiGraphics.drawString(
                    font,
                    getMessage(),
                    0,
                    0,
                    textColor,
                    false
            );
            guiGraphics.pose().popPose();
        } else if (!isHovered()) {
            String truncatedText = getTruncatedText(font, textAreaWidth);
            int truncatedTextWidth = Math.round(font.width(truncatedText) * textScale);
            int textX = textAreaLeft + (textAreaWidth - truncatedTextWidth) / 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textX, contentTop, 0.0);
            guiGraphics.pose().scale(textScale, textScale, 1.0f);
            guiGraphics.drawString(
                    font,
                    truncatedText,
                    0,
                    0,
                    textColor,
                    false
            );
            guiGraphics.pose().popPose();
        } else {
            long elapsed = System.currentTimeMillis() - marqueeStartTime;

            if (!marqueeActive) {
                marqueeActive = true;
                marqueeStartTime = System.currentTimeMillis();
                elapsed = 0;
            }

            float scrollOffset = 0.0f;

            long duration = 2000;
            long pause = 1000;
            long cycle = duration + pause + duration + pause;
            long time = elapsed % cycle;

            int overflow = scaledTextWidth - textAreaWidth;

            if (time <= duration) {
                float progress = (float) time / duration;
                scrollOffset = overflow * progress;
            } else if (time <= duration + pause) {
                scrollOffset = overflow;
            } else if (time <= duration + pause + duration) {
                float progress = (float) (time - (duration + pause)) / duration;
                scrollOffset = overflow * (1.0f - progress);
            } else {
                scrollOffset = 0.0f;
            }

            guiGraphics.enableScissor(
                    textAreaLeft,
                    contentTop,
                    textAreaRight,
                    contentTop + scaledTextHeight
            );
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(textAreaLeft - scrollOffset, contentTop, 0.0);
            guiGraphics.pose().scale(textScale, textScale, 1.0f);
            guiGraphics.drawString(
                    font,
                    getMessage(),
                    0,
                    0,
                    textColor,
                    false
            );
            guiGraphics.pose().popPose();
            guiGraphics.disableScissor();
        }

        if (!isHovered())
            marqueeActive = false;

        if (isHovered()) {
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

    @Override
    public void onClick(double mouseX, double mouseY) {
        onPress.run();
    }

    @Override
    protected void updateWidgetNarration(
            @NotNull NarrationElementOutput narrationElementOutput
    ) {
        narrationElementOutput.add(
                NarratedElementType.TITLE,
                getMessage()
        );
    }
}
