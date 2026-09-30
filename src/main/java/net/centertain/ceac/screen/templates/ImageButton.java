package net.centertain.ceac.screen.templates;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Button;
import net.centertain.ceac.screen.elements.Image;
import net.centertain.ceac.screen.elements.MarqueeLabel;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.ElementLister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class ImageButton {
    private ImageButton() {}

    public static Button create(
            int width,
            int height,
            Component text,
            int textColor,
            float textScale,
            int backgroundColor,
            int outlineColor,
            @Nullable ResourceLocation texture,
            @NotNull Runnable onPress
    ) {
        MarqueeLabel marquee = new MarqueeLabel(
                width - GuiConstants.ELEMENT_PADDING * 2,
                text,
                MarqueeLabel.Alignment.CENTER,
                textColor,
                textScale,
                false
        );
        ButtonContent content = new ButtonContent(
                width,
                height,
                texture,
                marquee
        );
        Button button = new Button(
                0,
                0,
                content,
                onPress,
                backgroundColor,
                outlineColor
        );
        button.setWidth(width);
        button.setHeight(height);
        return button;
    }


    private static class ButtonContent implements Element, ElementLister {
        private int x;
        private int y;
        private int width;
        private int height;

        private Image image;
        @SuppressWarnings("FieldMayBeFinal")
        private MarqueeLabel marquee;

        private final @Nullable ResourceLocation texture;
        private final int textureWidth;
        private final int textureHeight;
        private final List<Element> elements;

        private ButtonContent(
                int width,
                int height,
                @Nullable ResourceLocation texture,
                @NotNull MarqueeLabel marquee
        ) {
            this.x = 0;
            this.y = 0;
            this.width = width;
            this.height = height;
            this.texture = texture;
            this.marquee = marquee;

            if (texture != null) {
                try {
                    Resource resource = Minecraft.getInstance()
                            .getResourceManager()
                            .getResource(texture)
                            .orElseThrow();
                    try (InputStream stream = resource.open()) {
                        NativeImage nativeImage = NativeImage.read(stream);
                        this.textureWidth = nativeImage.getWidth();
                        this.textureHeight = nativeImage.getHeight();
                        nativeImage.close();
                    }
                } catch (IOException exception) {
                    throw new RuntimeException(
                            "Failed to load button texture " + texture,
                            exception
                    );
                }
                image = new Image(
                        textureWidth,
                        textureHeight,
                        texture
                );
                elements = List.of(image, marquee);
            } else {
                this.textureWidth = 0;
                this.textureHeight = 0;
                elements = List.of(
                        marquee
                );
            }

            layout();
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
            return height;
        }
        public List<Element> getElements() {
            return elements;
        }

        public void setX(int x) {
            this.x = x;
            layout();
        }
        public void setY(int y) {
            this.y = y;
            layout();
        }
        public void setWidth(int width) {
            this.width = width;
            layout();
        }
        public void setHeight(int height) {
            this.height = height;
            layout();
        }
        public void setDimensions(@NotNull Element dimensionSupplier) {
            this.x = dimensionSupplier.getX();
            this.y = dimensionSupplier.getY();
            this.width = dimensionSupplier.getWidth();
            this.height = dimensionSupplier.getHeight();
            layout();
        }

        private void layout() {
            int drawTextureWidth = 0;
            int drawTextureHeight = 0;

            if (texture != null) {
                int availableTextureWidth = width;
                int availableTextureHeight = height - marquee.getHeight() - GuiConstants.ELEMENT_PADDING;

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

            contentHeight += marquee.getHeight();

            int contentTop = y + (height - contentHeight) / 2;

            if (drawTextureHeight > 0) {
                int textureX = x + (width - drawTextureWidth) / 2;

                image.setX(textureX);
                image.setY(contentTop);
                image.setWidth(drawTextureWidth);
                image.setHeight(drawTextureHeight);

                contentTop += drawTextureHeight + GuiConstants.ELEMENT_PADDING;
            }

            marquee.setX(x + GuiConstants.ELEMENT_PADDING);
            marquee.setY(contentTop);
            marquee.setWidth(width - GuiConstants.ELEMENT_PADDING * 2);
        }

        @Override
        public void render(
                @NotNull GuiGraphics guiGraphics,
                int mouseX,
                int mouseY,
                float partialTick
        ) {
            if (image != null) {
                RenderSystem.enableBlend();
                image.render(
                        guiGraphics,
                        mouseX,
                        mouseY,
                        partialTick
                );
                RenderSystem.disableBlend();
            }

            marquee.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );
        }
    }
}
