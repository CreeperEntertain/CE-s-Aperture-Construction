package net.centertain.ceac.screen.elements;

import net.centertain.ceac.font.BitfontManager;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TextBox implements Element {
    private int x;
    private int y;
    private int width;
    private @NotNull String text;
    private float textScale;
    private int lineSpacing;
    private int color;
    private boolean shadow;
    private @NotNull Font font = BitfontManager.FONT;
    private List<FormattedCharSequence> lines = List.of();

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull String> dynamicText = null;
    private @Nullable Supplier<@NotNull Float> dynamicTextScale = null;
    private @Nullable Supplier<@NotNull Integer> dynamicLineSpacing = null;
    private @Nullable Supplier<@NotNull Integer> dynamicColor = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicShadow = null;
    private @Nullable Supplier<@NotNull Font> dynamicFont = null;

    public TextBox(
            int width,
            @NotNull String text,
            float textScale,
            int lineSpacing,
            int color,
            boolean shadow
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.text = text;
        this.textScale = textScale;
        this.lineSpacing = lineSpacing;
        this.color = color;
        this.shadow = shadow;
    }

    public TextBox(
            int width,
            @NotNull String text,
            float textScale,
            int lineSpacing,
            int color,
            boolean shadow,
            @NotNull Font font
    ) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.text = text;
        this.textScale = textScale;
        this.lineSpacing = lineSpacing;
        this.color = color;
        this.shadow = shadow;
        this.font = font;
    }

    public TextBox(
            int x,
            int y,
            int width,
            @NotNull String text,
            float textScale,
            int lineSpacing,
            int color,
            boolean shadow
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.text = text;
        this.textScale = textScale;
        this.lineSpacing = lineSpacing;
        this.color = color;
        this.shadow = shadow;
    }

    public TextBox(
            int x,
            int y,
            int width,
            @NotNull String text,
            float textScale,
            int lineSpacing,
            int color,
            boolean shadow,
            @NotNull Font font
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.text = text;
        this.textScale = textScale;
        this.lineSpacing = lineSpacing;
        this.color = color;
        this.shadow = shadow;
        this.font = font;
    }

    public TextBox(
            @NotNull Element positionSupplier,
            int width,
            @NotNull String text,
            float textScale,
            int lineSpacing,
            int color,
            boolean shadow
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.text = text;
        this.textScale = textScale;
        this.lineSpacing = lineSpacing;
        this.color = color;
        this.shadow = shadow;
    }

    public TextBox(
            @NotNull Element positionSupplier,
            int width,
            @NotNull String text,
            float textScale,
            int lineSpacing,
            int color,
            boolean shadow,
            @NotNull Font font
    ) {
        this.x = positionSupplier.getX();
        this.y = positionSupplier.getY();
        this.width = width;
        this.text = text;
        this.textScale = textScale;
        this.lineSpacing = lineSpacing;
        this.color = color;
        this.shadow = shadow;
        this.font = font;
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
        return (int) Math.ceil(
                (font.lineHeight * textScale * lines.size())
                + (textScale * lineSpacing * (lines.size() - 1))
        );
    }
    public @NotNull String getText() {
        return text;
    }
    public float getTextScale() {
        return textScale;
    }
    public int getLineSpacing() {
        return lineSpacing;
    }
    public int getColor() {
        return color;
    }
    public boolean getShadow() {
        return shadow;
    }
    public @NotNull Font getFont() {
        return font;
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
    public @Nullable Supplier<@NotNull String> getDynamicText() {
        return dynamicText;
    }
    public @Nullable Supplier<@NotNull Float> getDynamicTextScale() {
        return dynamicTextScale;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicLineSpacing() {
        return dynamicLineSpacing;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicColor() {
        return dynamicColor;
    }
    public @Nullable Supplier<@NotNull Boolean> getDynamicShadow() {
        return dynamicShadow;
    }
    public @Nullable Supplier<@NotNull Font> getDynamicFont() {
        return dynamicFont;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
        rebuildLines();
    }
    public void setHeight(int height) {}
    public void setText(@NotNull String text) {
        this.text = text;
        rebuildLines();
    }
    public void setTextScale(float textScale) {
        this.textScale = textScale;
        rebuildLines();
    }
    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }
    public void setFont(@NotNull Font font) {
        this.font = font;
        rebuildLines();
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
    public void setDynamicText(@Nullable Supplier<@NotNull String> dynamicText) {
        this.dynamicText = dynamicText;
    }
    public void setDynamicTextScale(@Nullable Supplier<@NotNull Float> dynamicTextScale) {
        this.dynamicTextScale = dynamicTextScale;
    }
    public void setDynamicLineSpacing(@Nullable Supplier<@NotNull Integer> dynamicLineSpacing) {
        this.dynamicLineSpacing = dynamicLineSpacing;
    }
    public void setDynamicColor(@Nullable Supplier<@NotNull Integer> dynamicColor) {
        this.dynamicColor = dynamicColor;
    }
    public void setDynamicShadow(@Nullable Supplier<@NotNull Boolean> dynamicShadow) {
        this.dynamicShadow = dynamicShadow;
    }
    public void setDynamicFont(@Nullable Supplier<@NotNull Font> dynamicFont) {
        this.dynamicFont = dynamicFont;
    }


    private void rebuildLines() {
        lines = font.split(
                Component.literal(text),
                (int) Math.ceil(width / textScale)
        );
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicText != null) setText(dynamicText.get());
        if (dynamicTextScale != null) setTextScale(dynamicTextScale.get());
        if (dynamicLineSpacing != null) setLineSpacing(dynamicLineSpacing.get());
        if (dynamicColor != null) setColor(dynamicColor.get());
        if (dynamicShadow != null) setShadow(dynamicShadow.get());
        if (dynamicFont != null) setFont(dynamicFont.get());
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

        for (int i = 0; i < lines.size(); i++)
            guiGraphics.drawString(
                    font,
                    lines.get(i),
                    0,
                    (i * font.lineHeight) + ((i - 1) * lineSpacing),
                    color,
                    shadow
            );

        guiGraphics.pose().popPose();
    }
}
