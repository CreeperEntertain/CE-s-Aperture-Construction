package net.centertain.ceac.screen.elements;

import com.mojang.blaze3d.platform.NativeImage;
import net.centertain.ceac.GuiConstants;
import net.minecraft.client.Minecraft;
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
    private final int backgroundColor;
    private final int outlineColor;
    private final @Nullable ResourceLocation texture;
    private final int textureWidth;
    private final int textureHeight;
    private final Runnable onPress;

    public Button(
            int x,
            int y,
            int width,
            int height,
            Component text,
            int textColor,
            int backgroundColor,
            int outlineColor,
            @Nullable ResourceLocation texture,
            Runnable onPress
    ) {
        super(x, y, width, height, text);

        this.textColor = textColor;
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

        int textWidth = Minecraft.getInstance().font.width(getMessage());
        int textHeight = 8;

        int drawTextureWidth = 0;
        int drawTextureHeight = 0;

        if (texture != null) {
            int availableTextureWidth = getWidth();
            int availableTextureHeight = getHeight() - textHeight - GuiConstants.ELEMENT_PADDING;

            if (availableTextureWidth > 0 && availableTextureHeight > 0) {
                float scale = Math.min(
                        1.0f,
                        Math.min(
                                (float) availableTextureWidth / textureWidth,
                                (float) availableTextureHeight / textureHeight
                        )
                );

                drawTextureWidth = Math.max(1, Math.round(textureWidth * scale));
                drawTextureHeight = Math.max(1, Math.round(textureHeight * scale));
            }
        }

        int contentHeight = drawTextureHeight;

        if (drawTextureHeight > 0)
            contentHeight += GuiConstants.ELEMENT_PADDING;

        contentHeight += textHeight;

        int contentTop = getY() + (getHeight() - contentHeight) / 2;

        if (drawTextureHeight > 0) {
            int textureX =
                    getX() + (getWidth() - drawTextureWidth) / 2;

            guiGraphics.blit(
                    texture,
                    textureX,
                    contentTop,
                    0,
                    0,
                    drawTextureWidth,
                    drawTextureHeight,
                    textureWidth,
                    textureHeight
            );

            contentTop += drawTextureHeight + GuiConstants.ELEMENT_PADDING;
        }

        int textX = getX() + (getWidth() - textWidth) / 2;

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                getMessage(),
                textX,
                contentTop,
                textColor,
                false
        );

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
