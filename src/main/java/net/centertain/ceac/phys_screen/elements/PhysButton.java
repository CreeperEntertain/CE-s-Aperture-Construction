package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.screen.elements.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhysButton extends Button {
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

        int textX = getX() + (getWidth() - scaledTextWidth) / 2;
        int textY = getY() + (getHeight() - scaledTextHeight) / 2;

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
                getMessage(),
                0,
                0,
                textColor,
                false
        );

        guiGraphics.pose().popPose();

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
}
