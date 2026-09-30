package net.centertain.ceac.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.decal.DecalDefinition;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.DecalPack;
import net.centertain.ceac.decal.network.SyncDecalItemPacket;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.screen.elements.Button;
import net.centertain.ceac.screen.elements.FlowPanel;
import net.centertain.ceac.screen.elements.ScrollContainer;
import net.centertain.ceac.screen.elements.StackPanel;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.Screen;
import net.centertain.ceac.screen.framework.element_types.Reactable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DecalItemScreen extends Screen {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private ScrollContainer tabScroll;
    private ScrollContainer decalScroll;

    private final InteractionHand hand;

    public DecalItemScreen(InteractionHand hand) {
        super(Component.empty());
        this.hand = hand;
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void setPack(DecalPack pack) {
        if (decalScroll != null)
            removeElement(decalScroll);

        List<Element> decals = new ArrayList<>();
        for (DecalDefinition decal : pack.getDecals()) {
            Button button = createButton(
                    GuiConstants.IMAGE_BUTTON_WIDTH,
                    GuiConstants.IMAGE_BUTTON_HEIGHT,
                    Component.literal(decal.getName()),
                    GuiConstants.COLOR_SOLID_WHITE,
                    0.5f,
                    GuiConstants.COLOR_TRANSPARENT,
                    GuiConstants.COLOR_SOLID_WHITE,
                    decal.getResourceLocation(),
                    () -> setDecal(decal)
            );
            decals.add(button);
        }

        FlowPanel decalPanel = new FlowPanel(
                left,
                top,
                FlowPanel.Alignment.HORIZONTAL,
                right - left,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                decals
        );
        decalScroll = new ScrollContainer(
                left,
                top,
                right - left,
                bottom - top,
                ScrollContainer.Alignment.VERTICAL,
                decalPanel,
                decalPanel.getHeight(),
                GuiConstants.FLOW_SCROLL_SPEED
        );

        addElement(decalScroll);
    }

    private Button createButton(
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
        Button button = new Button(
                0,
                0,
                new ButtonContent(
                        width,
                        height,
                        text,
                        textColor,
                        textScale,
                        texture
                ),
                onPress,
                backgroundColor,
                outlineColor
        );
        button.setWidth(width);
        button.setHeight(height);
        return button;
    }

    private void setDecal(DecalDefinition decal) {
        ResourceLocation texture = decal.getResourceLocation();
        ModNetworking.CHANNEL.sendToServer(new SyncDecalItemPacket(hand, texture));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    protected void init() {
        super.init();

        left = GuiConstants.SCREEN_PADDING + GuiConstants.STACK_PANEL_WIDTH + GuiConstants.ELEMENT_PADDING;
        top = GuiConstants.SCREEN_PADDING;
        right = width - GuiConstants.SCREEN_PADDING;
        bottom = height - GuiConstants.SCREEN_PADDING;

        List<DecalPack> decalPacks = DecalLoader.getPacks();
        List<Element> tabs = new ArrayList<>();
        for (DecalPack decalPack : decalPacks) {
            Button button = createButton(
                    GuiConstants.STACK_PANEL_WIDTH,
                    GuiConstants.TAB_BUTTON_HEIGHT,
                    Component.literal(decalPack.getName()),
                    GuiConstants.COLOR_SOLID_WHITE,
                    1.0f,
                    GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                    GuiConstants.COLOR_SOLID_WHITE,
                    null,
                    () -> setPack(decalPack)
            );

            tabs.add(button);
        }

        StackPanel tabStack = new StackPanel(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                StackPanel.Alignment.VERTICAL,
                GuiConstants.STACK_PANEL_WIDTH,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                tabs
        );
        tabScroll = new ScrollContainer(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                GuiConstants.STACK_PANEL_WIDTH,
                height - GuiConstants.SCREEN_PADDING * 2,
                ScrollContainer.Alignment.VERTICAL,
                tabStack,
                tabStack.getHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        );
        addElement(tabScroll);

        setPack(decalPacks.get(0));
    }


    private static class ButtonContent implements Element, Reactable {
        private int x;
        private int y;
        private int width;
        private int height;

        private final Component text;
        private final int textColor;
        private final float textScale;
        private final @Nullable ResourceLocation texture;
        private final int textureWidth;
        private final int textureHeight;

        private Supplier<Boolean> scrollCondition;

        private long marqueeStartTime;
        private boolean marqueeActive;

        private ButtonContent(
                int width,
                int height,
                Component text,
                int textColor,
                float textScale,
                @Nullable ResourceLocation texture
        ) {
            this.x = 0;
            this.y = 0;
            this.width = width;
            this.height = height;
            this.text = text;
            this.textColor = textColor;
            this.textScale = textScale;
            this.texture = texture;

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
                    throw new RuntimeException("Failed to load button texture " + texture, exception);
                }
            } else {
                this.textureWidth = 0;
                this.textureHeight = 0;
            }
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

        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }
        public void setWidth(int width) {
            this.width = width;
        }
        public void setHeight(int height) {
            this.height = height;
        }
        public void setDimensions(@NotNull Element dimensionSupplier) {
            this.x = dimensionSupplier.getX();
            this.y = dimensionSupplier.getY();
            this.width = dimensionSupplier.getWidth();
            this.height = dimensionSupplier.getHeight();
        }


        @Override
        public void parentHover(@NotNull Supplier<Boolean> parentHoverState) {
            scrollCondition = parentHoverState;
        }

        private String getTruncatedText(Font font, int availableWidth) {
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

        @Override
        public void render(
                @NotNull GuiGraphics guiGraphics,
                int mouseX,
                int mouseY,
                float partialTick
        ) {
            Font font = Minecraft.getInstance().font;

            int textWidth = font.width(text);
            int textHeight = 8;

            int scaledTextWidth = Math.round(textWidth * textScale);
            int scaledTextHeight = Math.round(textHeight * textScale);

            int drawTextureWidth = 0;
            int drawTextureHeight = 0;

            if (texture != null) {
                int availableTextureWidth = width;
                int availableTextureHeight = height - scaledTextHeight - GuiConstants.ELEMENT_PADDING;

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

            int contentTop = y + (height - contentHeight) / 2;

            if (drawTextureHeight > 0) {
                float textureScale = (float) drawTextureWidth / textureWidth;
                int textureX = x + (width - drawTextureWidth) / 2;

                RenderSystem.enableBlend();

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

                RenderSystem.disableBlend();

                contentTop += drawTextureHeight + GuiConstants.ELEMENT_PADDING;
            }

            int textAreaLeft = x + GuiConstants.ELEMENT_PADDING;
            int textAreaRight = x + width - GuiConstants.ELEMENT_PADDING;
            int textAreaWidth = textAreaRight - textAreaLeft;

            boolean shouldScroll = scrollCondition != null && scrollCondition.get();

            if (scaledTextWidth <= textAreaWidth) {
                marqueeActive = false;

                int textX = textAreaLeft + (textAreaWidth - scaledTextWidth) / 2;

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(textX, contentTop, 0.0);
                guiGraphics.pose().scale(textScale, textScale, 1.0f);
                guiGraphics.drawString(
                        font,
                        text,
                        0,
                        0,
                        textColor,
                        false
                );
                guiGraphics.pose().popPose();
            } else if (!shouldScroll) {
                marqueeActive = false;

                String truncatedText = getTruncatedText(
                        font,
                        textAreaWidth
                );
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

                float scrollOffset = getScrollOffset(
                        elapsed,
                        scaledTextWidth,
                        textAreaWidth
                );

                guiGraphics.enableScissor(
                        textAreaLeft,
                        contentTop,
                        textAreaRight,
                        contentTop + scaledTextHeight
                );
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(
                        textAreaLeft - scrollOffset,
                        contentTop,
                        0.0
                );
                guiGraphics.pose().scale(textScale, textScale, 1.0f);
                guiGraphics.drawString(
                        font,
                        text,
                        0,
                        0,
                        textColor,
                        false
                );
                guiGraphics.pose().popPose();
                guiGraphics.disableScissor();
            }

            if (!shouldScroll)
                marqueeActive = false;
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
}
