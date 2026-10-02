package net.centertain.ceac.screen.elements;

import net.centertain.ceac.font.BitfontManager;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Label implements Element {
    private int x;
    private int y;
    private int color;
    private @NotNull Component text;
    private float textScale;
    private boolean shadow;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicColor = null;
    private @Nullable Supplier<@NotNull Component> dynamicText = null;
    private @Nullable Supplier<@NotNull Float> dynamicTextScale = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicShadow = null;

    public Label(
            int color,
            @NotNull Component text,
            float textScale,
            boolean shadow
    ) {
        this.x = 0;
        this.y = 0;
        this.color = color;
        this.text = text;
        this.textScale = textScale;
        this.shadow = shadow;
    }

    public Label(
            int x,
            int y,
            int color,
            @NotNull Component text,
            float textScale,
            boolean shadow
    ) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.text = text;
        this.textScale = textScale;
        this.shadow = shadow;
    }

    public Label(
            @NotNull Element positionSupplier,
            int color,
            @NotNull Component text,
            float textScale,
            boolean shadow
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.color = color;
        this.text = text;
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
        return Math.round(Minecraft.getInstance().font.width(text) * textScale);
    }
    public int getHeight() {
        return Math.round(Minecraft.getInstance().font.lineHeight * textScale);
    }
    public int getColor() {
        return color;
    }
    public @NotNull Component getText() {
        return text;
    }
    public float getTextScale() {
        return textScale;
    }
    public boolean getShadow() {
        return shadow;
    }

    public @Nullable Supplier<@NotNull Integer> getDynamicX() {
        return dynamicX;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicY() {
        return dynamicY;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicColor() {
        return dynamicColor;
    }
    public @Nullable Supplier<@NotNull Component> getDynamicText() {
        return dynamicText;
    }
    public @Nullable Supplier<@NotNull Float> getDynamicTextScale() {
        return dynamicTextScale;
    }
    public @Nullable Supplier<@NotNull Boolean> getDynamicShadow() {
        return dynamicShadow;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {}
    public void setHeight(int height) {}
    public void setColor(int color) {
        this.color = color;
    }
    public void setText(@NotNull Component text) {
        this.text = text;
    }
    public void setTextScale(float textScale) {
        this.textScale = textScale;
    }
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }
    public void setDimensions(@NotNull Element positionSupplier) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
    }

    public void setDynamicX(@Nullable Supplier<@NotNull Integer> dynamicX) {
        this.dynamicX = dynamicX;
    }
    public void setDynamicY(@Nullable Supplier<@NotNull Integer> dynamicY) {
        this.dynamicY = dynamicY;
    }
    public void setDynamicColor(@Nullable Supplier<@NotNull Integer> dynamicColor) {
        this.dynamicColor = dynamicColor;
    }
    public void setDynamicText(@Nullable Supplier<@NotNull Component> dynamicText) {
        this.dynamicText = dynamicText;
    }
    public void setDynamicTextScale(@Nullable Supplier<@NotNull Float> dynamicTextScale) {
        this.dynamicTextScale = dynamicTextScale;
    }
    public void setDynamicShadow(@Nullable Supplier<@NotNull Boolean> dynamicShadow) {
        this.dynamicShadow = dynamicShadow;
    }


    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicColor != null) setColor(dynamicColor.get());
        if (dynamicText != null) setText(dynamicText.get());
        if (dynamicTextScale != null) setTextScale(dynamicTextScale.get());
        if (dynamicShadow != null) setShadow(dynamicShadow.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(x, y, 0.0);
        guiGraphics.pose().scale(textScale, textScale, 1.0f);

        guiGraphics.drawString(
                BitfontManager.FONT,
                text,
                0,
                0,
                color,
                shadow
        );

        guiGraphics.pose().popPose();
    }
}
