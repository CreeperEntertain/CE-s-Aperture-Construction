package net.centertain.ceac.screen.elements;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.font.BitfontManager;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.element_types.Hoverable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class TextInput implements Element, Hoverable, GuiEventListener {
    private static final long DOUBLE_CLICK_TIME = 250L;
    private static final double DOUBLE_CLICK_DISTANCE = 4.0;

    private int x;
    private int y;
    private int width;
    private int height;

    private @NotNull String text;
    private @Nullable String placeholder;

    private int textColor;
    private float textScale;
    private boolean shadow;
    private @NotNull Font font;

    private @NotNull Runnable onEnter;

    private int outlineColor = GuiConstants.COLOR_SOLID_LIGHT_GRAY;
    private int placeholderColor = GuiConstants.COLOR_SOLID_DARK_GRAY;
    private int selectionColor = GuiConstants.COLOR_TEXT_SELECTION;
    private int cursorColor = GuiConstants.COLOR_SOLID_WHITE;

    private final @NotNull TextFieldHelper textHelper;

    private double displayOffset;

    private boolean focused;
    private boolean hovered;
    private boolean dragging;

    private long lastClickTime;
    private double lastClickX;
    private double lastClickY;
    private int clickCount;

    private int wordAnchorStart;
    private int wordAnchorEnd;

    private DragMode dragMode = DragMode.NONE;

    private long lastCursorActivity;
    private long lastAutoScrollTime;

    private @Nullable Supplier<@NotNull Integer> dynamicX = null;
    private @Nullable Supplier<@NotNull Integer> dynamicY = null;
    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;
    private @Nullable Supplier<@NotNull String> dynamicText = null;
    private @Nullable Supplier<@Nullable String> dynamicPlaceholder = null;
    private @Nullable Supplier<@NotNull Integer> dynamicTextColor = null;
    private @Nullable Supplier<@NotNull Float> dynamicTextScale = null;
    private @Nullable Supplier<@NotNull Boolean> dynamicShadow = null;
    private @Nullable Supplier<@NotNull Font> dynamicFont = null;
    private @Nullable Supplier<@NotNull Runnable> dynamicOnEnter = null;
    private @Nullable Supplier<@NotNull Integer> dynamicOutlineColor = null;
    private @Nullable Supplier<@NotNull Integer> dynamicPlaceholderColor = null;
    private @Nullable Supplier<@NotNull Integer> dynamicSelectionColor = null;
    private @Nullable Supplier<@NotNull Integer> dynamicCursorColor = null;

    private enum DragMode {
        NONE,
        NORMAL,
        WORD
    }

    public TextInput(
            @NotNull Element dimensionSupplier,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Runnable onEnter
    ) {
        this(
                dimensionSupplier.getX(),
                dimensionSupplier.getY(),
                dimensionSupplier.getWidth(),
                dimensionSupplier.getHeight(),
                placeholder,
                textColor,
                textScale,
                shadow,
                BitfontManager.FONT,
                onEnter
        );
    }

    public TextInput(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Runnable onEnter
    ) {
        this(
                positionSupplier.getX(),
                positionSupplier.getY(),
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                BitfontManager.FONT,
                onEnter
        );
    }

    public TextInput(
            int x,
            int y,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Runnable onEnter
    ) {
        this(
                x,
                y,
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                BitfontManager.FONT,
                onEnter
        );
    }

    public TextInput(
            @NotNull Element dimensionSupplier,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Font font,
            @NotNull Runnable onEnter
    ) {
        this(
                dimensionSupplier.getX(),
                dimensionSupplier.getY(),
                dimensionSupplier.getWidth(),
                dimensionSupplier.getHeight(),
                placeholder,
                textColor,
                textScale,
                shadow,
                font,
                onEnter
        );
    }

    public TextInput(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Font font,
            @NotNull Runnable onEnter
    ) {
        this(
                positionSupplier.getX(),
                positionSupplier.getY(),
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                font,
                onEnter
        );
    }

    public TextInput(
            @NotNull Element dimensionSupplier,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Runnable onEnter,
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this(
                dimensionSupplier.getX(),
                dimensionSupplier.getY(),
                dimensionSupplier.getWidth(),
                dimensionSupplier.getHeight(),
                placeholder,
                textColor,
                textScale,
                shadow,
                BitfontManager.FONT,
                onEnter
        );
        setAdditionalColors(outlineColor, placeholderColor, selectionColor, cursorColor);
    }

    public TextInput(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Runnable onEnter,
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this(
                positionSupplier.getX(),
                positionSupplier.getY(),
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                BitfontManager.FONT,
                onEnter
        );
        setAdditionalColors(outlineColor, placeholderColor, selectionColor, cursorColor);
    }

    public TextInput(
            int x,
            int y,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Runnable onEnter,
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this(
                x,
                y,
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                BitfontManager.FONT,
                onEnter
        );
        setAdditionalColors(outlineColor, placeholderColor, selectionColor, cursorColor);
    }

    public TextInput(
            @NotNull Element dimensionSupplier,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Font font,
            @NotNull Runnable onEnter,
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this(
                dimensionSupplier.getX(),
                dimensionSupplier.getY(),
                dimensionSupplier.getWidth(),
                dimensionSupplier.getHeight(),
                placeholder,
                textColor,
                textScale,
                shadow,
                font,
                onEnter
        );
        setAdditionalColors(outlineColor, placeholderColor, selectionColor, cursorColor);
    }

    public TextInput(
            @NotNull Element positionSupplier,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Font font,
            @NotNull Runnable onEnter,
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this(
                positionSupplier.getX(),
                positionSupplier.getY(),
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                font,
                onEnter
        );
        setAdditionalColors(outlineColor, placeholderColor, selectionColor, cursorColor);
    }

    public TextInput(
            int x,
            int y,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Font font,
            @NotNull Runnable onEnter,
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this(
                x,
                y,
                width,
                height,
                placeholder,
                textColor,
                textScale,
                shadow,
                font,
                onEnter
        );
        setAdditionalColors(outlineColor, placeholderColor, selectionColor, cursorColor);
    }

    private void setAdditionalColors(
            int outlineColor,
            int placeholderColor,
            int selectionColor,
            int cursorColor
    ) {
        this.outlineColor = outlineColor;
        this.placeholderColor = placeholderColor;
        this.selectionColor = selectionColor;
        this.cursorColor = cursorColor;
    }

    public TextInput(
            int x,
            int y,
            int width,
            int height,
            @Nullable String placeholder,
            int textColor,
            float textScale,
            boolean shadow,
            @NotNull Font font,
            @NotNull Runnable onEnter
    ) {
        if (textScale <= 0.0f)
            throw new IllegalArgumentException("textScale must be greater than zero");

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.text = "";
        this.placeholder = placeholder;

        this.textColor = textColor;
        this.textScale = textScale;
        this.shadow = shadow;
        this.font = font;

        this.onEnter = onEnter;

        this.textHelper = new TextFieldHelper(
                () -> text,
                value -> {
                    text = value;
                    clampDisplayOffset();
                },
                TextFieldHelper.createClipboardGetter(Minecraft.getInstance()),
                TextFieldHelper.createClipboardSetter(Minecraft.getInstance()),
                TextInput::isValidText
        );

        lastCursorActivity = System.currentTimeMillis();
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
    public @NotNull String getText() {
        return text;
    }
    public @Nullable String getPlaceholder() {
        return placeholder;
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
    public @NotNull Font getFont() {
        return font;
    }
    public @NotNull Runnable getOnEnter() {
        return onEnter;
    }
    public int getOutlineColor() {
        return outlineColor;
    }
    public int getPlaceholderColor() {
        return placeholderColor;
    }
    public int getSelectionColor() {
        return selectionColor;
    }
    public int getCursorColor() {
        return cursorColor;
    }
    public int getCursorPosition() {
        return textHelper.getCursorPos();
    }
    public int getSelectionPosition() {
        return textHelper.getSelectionPos();
    }
    public boolean isSelecting() {
        return textHelper.isSelecting();
    }
    public @NotNull String getSelectedText() {
        int start = Math.min(textHelper.getCursorPos(), textHelper.getSelectionPos());
        int end = Math.max(textHelper.getCursorPos(), textHelper.getSelectionPos());
        return text.substring(start, end);
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
    public @Nullable Supplier<@NotNull Integer> getDynamicHeight() {
        return dynamicHeight;
    }
    public @Nullable Supplier<@NotNull String> getDynamicText() {
        return dynamicText;
    }
    public @Nullable Supplier<@Nullable String> getDynamicPlaceholder() {
        return dynamicPlaceholder;
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
    public @Nullable Supplier<@NotNull Font> getDynamicFont() {
        return dynamicFont;
    }
    public @Nullable Supplier<@NotNull Runnable> getDynamicOnEnter() {
        return dynamicOnEnter;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicOutlineColor() {
        return dynamicOutlineColor;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicPlaceholderColor() {
        return dynamicPlaceholderColor;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicSelectionColor() {
        return dynamicSelectionColor;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicCursorColor() {
        return dynamicCursorColor;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
        clampDisplayOffset();
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setText(@NotNull String text) {
        if (!isValidText(text))
            throw new IllegalArgumentException("TextInput does not support line breaks");

        this.text = text;

        textHelper.setCursorPos(text.length());
        textHelper.setSelectionPos(text.length());

        resetCursorActivity();
        clampDisplayOffset();
    }
    public void setPlaceholder(@Nullable String placeholder) {
        this.placeholder = placeholder;
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    public void setTextScale(float textScale) {
        this.textScale = textScale;
        clampDisplayOffset();
    }
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }
    public void setFont(@NotNull Font font) {
        this.font = font;
        clampDisplayOffset();
    }
    public void setOnEnter(@NotNull Runnable onEnter) {
        this.onEnter = onEnter;
    }
    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }
    public void setPlaceholderColor(int placeholderColor) {
        this.placeholderColor = placeholderColor;
    }
    public void setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
    }
    public void setCursorColor(int cursorColor) {
        this.cursorColor = cursorColor;
    }
    public void setDimensions(@NotNull Element dimensionSupplier) {
        this.x = dimensionSupplier.getX();
        this.y = dimensionSupplier.getY();
        this.width = dimensionSupplier.getWidth();
        this.height = dimensionSupplier.getHeight();

        clampDisplayOffset();
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
    public void setDynamicHeight(@Nullable Supplier<@NotNull Integer> dynamicHeight) {
        this.dynamicHeight = dynamicHeight;
    }
    public void setDynamicText(@Nullable Supplier<@NotNull String> dynamicText) {
        this.dynamicText = dynamicText;
    }
    public void setDynamicPlaceholder(@Nullable Supplier<@Nullable String> dynamicPlaceholder) {
        this.dynamicPlaceholder = dynamicPlaceholder;
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
    public void setDynamicFont(@Nullable Supplier<@NotNull Font> dynamicFont) {
        this.dynamicFont = dynamicFont;
    }
    public void setDynamicOnEnter(@Nullable Supplier<@NotNull Runnable> dynamicOnEnter) {
        this.dynamicOnEnter = dynamicOnEnter;
    }
    public void setDynamicOutlineColor(@Nullable Supplier<@NotNull Integer> dynamicOutlineColor) {
        this.dynamicOutlineColor = dynamicOutlineColor;
    }
    public void setDynamicPlaceholderColor(@Nullable Supplier<@NotNull Integer> dynamicPlaceholderColor) {
        this.dynamicPlaceholderColor = dynamicPlaceholderColor;
    }
    public void setDynamicSelectionColor(@Nullable Supplier<@NotNull Integer> dynamicSelectionColor) {
        this.dynamicSelectionColor = dynamicSelectionColor;
    }
    public void setDynamicCursorColor(@Nullable Supplier<@NotNull Integer> dynamicCursorColor) {
        this.dynamicCursorColor = dynamicCursorColor;
    }


    private static boolean isValidText(
            @NotNull String text
    ) {
        return text.indexOf('\n') < 0 && text.indexOf('\r') < 0;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(
            boolean focused
    ) {
        this.focused = focused;
        this.dragging = false;
        this.dragMode = DragMode.NONE;

        if (focused)
            resetCursorActivity();
    }

    @Override
    public boolean getIsHovered() {
        return hovered;
    }

    @Override
    public void updateHover(
            double mouseX,
            double mouseY
    ) {
        hovered = isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return false;
        if (!isMouseOver(mouseX, mouseY))
            return false;

        long now = System.currentTimeMillis();

        if (
                now - lastClickTime <= DOUBLE_CLICK_TIME &&
                Math.abs(mouseX - lastClickX) <= DOUBLE_CLICK_DISTANCE &&
                Math.abs(mouseY - lastClickY) <= DOUBLE_CLICK_DISTANCE
        )
            clickCount++;
        else
            clickCount = 1;

        lastClickTime = now;
        lastClickX = mouseX;
        lastClickY = mouseY;

        int cursorPosition = getCursorPosition(mouseX);

        if (clickCount == 3) {
            textHelper.selectAll();

            dragging = true;
            dragMode = DragMode.NONE;
            clickCount = 0;

            resetCursorActivity();
            ensureCursorVisible();

            return true;
        }

        if (clickCount == 2) {
            selectWord(cursorPosition);

            dragging = true;
            dragMode = DragMode.WORD;

            resetCursorActivity();
            ensureCursorVisible();

            return true;
        }

        textHelper.setCursorPos(cursorPosition, Screen.hasShiftDown());

        dragging = true;
        dragMode = DragMode.NORMAL;

        resetCursorActivity();
        ensureCursorVisible();

        return true;
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (!dragging)
            return false;
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return false;

        lastAutoScrollTime = 0L;

        updateDrag(mouseX);

        return true;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return false;
        if (!dragging)
            return false;

        dragging = false;
        dragMode = DragMode.NONE;
        lastAutoScrollTime = 0L;

        return true;
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (!focused)
            return false;
        if (
                keyCode == GLFW.GLFW_KEY_ENTER ||
                keyCode == GLFW.GLFW_KEY_KP_ENTER
        ) {
            onEnter.run();
            resetCursorActivity();
            return true;
        }

        boolean handled = textHelper.keyPressed(keyCode);
        if (!handled)
            return false;

        resetCursorActivity();
        ensureCursorVisible();

        dragging = false;
        dragMode = DragMode.NONE;

        return true;
    }

    @Override
    public boolean charTyped(
            char codePoint,
            int modifiers
    ) {
        if (!focused)
            return false;
        if (Character.isISOControl(codePoint))
            return false;

        boolean handled = textHelper.charTyped(codePoint);
        if (!handled)
            return false;

        resetCursorActivity();
        ensureCursorVisible();

        dragging = false;
        dragMode = DragMode.NONE;

        return true;
    }

    @Override
    public boolean isMouseOver(
            double mouseX,
            double mouseY
    ) {
        return
                mouseX >= x &&
                mouseY >= y &&
                mouseX < x + width &&
                mouseY < y + height;
    }

    private void updateDrag(double mouseX) {
        autoScroll(mouseX);

        int cursorPosition = getCursorPosition(mouseX);

        switch (dragMode) {
            case NORMAL -> textHelper.setCursorPos(cursorPosition, true);
            case WORD -> updateWordSelection(cursorPosition);
            case NONE -> {}
        }

        ensureCursorVisible();
        resetCursorActivity();
    }

    private void selectWord(int cursorPosition) {
        if (text.isEmpty()) {
            textHelper.setCursorPos(0);
            wordAnchorStart = 0;
            wordAnchorEnd = 0;
            return;
        }

        int start = getWordStart(cursorPosition);
        int end = getWordEnd(cursorPosition);

        wordAnchorStart = start;
        wordAnchorEnd = end;

        textHelper.setSelectionRange(end, start);
    }

    private void updateWordSelection(int cursorPosition) {
        if (cursorPosition >= wordAnchorEnd) {
            int end = getWordEnd(cursorPosition);
            textHelper.setSelectionRange(end, wordAnchorStart);
            return;
        }
        if (cursorPosition <= wordAnchorStart) {
            int start = getWordStart(cursorPosition);
            textHelper.setSelectionRange(start, wordAnchorEnd);
            return;
        }

        textHelper.setSelectionRange(wordAnchorEnd, wordAnchorStart);
    }

    private int getWordStart(int cursorPosition) {
        if (text.isEmpty())
            return 0;

        int index = Math.max(0, Math.min(cursorPosition, text.length()));
        if (index == text.length())
            index--;

        boolean whitespace = Character.isWhitespace(text.charAt(index));

        while (
                index > 0 &&
                Character.isWhitespace(text.charAt(index - 1)) == whitespace
        )
            index--;

        return index;
    }

    private int getWordEnd(int cursorPosition) {
        if (text.isEmpty())
            return 0;

        int index = Math.max(0, Math.min(cursorPosition, text.length()));
        if (index == text.length())
            return index;

        boolean whitespace = Character.isWhitespace(text.charAt(index));

        while (
                index < text.length() &&
                Character.isWhitespace(text.charAt(index)) == whitespace
        )
            index++;

        return index;
    }

    private int getCursorPosition(double mouseX) {
        double localX = (mouseX - getContentX()) / textScale + displayOffset;
        if (localX <= 0.0)
            return 0;

        int textWidth = font.width(text);
        if (localX >= textWidth)
            return text.length();

        int low = 0;
        int high = text.length();

        while (low < high) {
            int middle = (low + high) >>> 1;
            int middleWidth = font.width(text.substring(0, middle));

            if (middleWidth < localX)
                low = middle + 1;
            else
                high = middle;
        }

        int index = Math.min(low, text.length());
        if (index == 0)
            return 0;
        if (index >= text.length())
            return text.length();

        int previousWidth = font.width(text.substring(0, index - 1));
        int nextWidth = font.width(text.substring(0, index));

        return Math.abs(localX - previousWidth) <= Math.abs(nextWidth - localX)
                ? index - 1
                : index;
    }

    private void autoScroll(double mouseX) {
        int left = getContentX();
        int right = left + getContentWidth();
        if (mouseX >= left && mouseX <= right) {
            lastAutoScrollTime = 0L;
            return;
        }

        long now = System.nanoTime();
        if (lastAutoScrollTime == 0L) {
            lastAutoScrollTime = now;
            return;
        }

        double deltaTime = Math.min(0.05, Math.max(0.0, (now - lastAutoScrollTime) / 1_000_000_000.0));

        lastAutoScrollTime = now;

        double distance;
        if (mouseX < left)
            distance = mouseX - left;
        else
            distance = mouseX - right;

        displayOffset += (distance * 8.0 * deltaTime) / textScale;

        clampDisplayOffset();
    }

    private void ensureCursorVisible() {
        int cursor = textHelper.getCursorPos();
        double cursorX = font.width(text.substring(0, cursor));
        double visibleWIdth = getContentWidth() / textScale;
        double cursorMargin = 1.0 / textScale;

        if (cursorX < displayOffset)
            displayOffset = cursorX;
        else if (cursorX > displayOffset + visibleWIdth - cursorMargin)
            displayOffset = cursorX - visibleWIdth + cursorMargin;

        clampDisplayOffset();
    }

    private void clampDisplayOffset() {
        double visibleWidth = getContentWidth() / textScale;
        double textWidth = font.width(text);
        double maxOffset = Math.max(0.0, textWidth - visibleWidth);

        displayOffset = Math.max(0.0, Math.min(displayOffset, maxOffset));
    }

    private int getPadding() {
        return Math.max(0, (height - getTextHeight()) / 2);
    }

    private int getContentX() {
        return x + getPadding();
    }

    private int getContentY() {
        return y + getPadding();
    }

    private int getContentWidth() {
        return Math.max(0, width - getPadding() * 2);
    }

    private int getContentHeight() {
        return Math.max(0, height - getPadding() * 2);
    }

    private int getTextHeight() {
        return Math.round(font.lineHeight * textScale);
    }

    private int getTextY() {
        return getContentY() + Math.max(0, (getContentHeight() - getTextHeight()) / 2);
    }

    private int getTextScreenX(int characterIndex) {
        return Math.toIntExact(Math.round(
                getContentX()
                + (font.width(text.substring(0, characterIndex)) - displayOffset)
                * textScale
        ));
    }

    private void resetCursorActivity() {
        lastCursorActivity = System.currentTimeMillis();
    }

    private boolean isCursorVisible() {
        if (!focused)
            return false;
        return (System.currentTimeMillis() - lastCursorActivity) % 1000 < 500;
    }

    private void applyDynamics() {
        if (dynamicX != null) setX(dynamicX.get());
        if (dynamicY != null) setY(dynamicY.get());
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
        if (dynamicText != null) setText(dynamicText.get());
        if (dynamicPlaceholder != null) setPlaceholder(dynamicPlaceholder.get());
        if (dynamicTextColor != null) setTextColor(dynamicTextColor.get());
        if (dynamicShadow != null) setShadow(dynamicShadow.get());
        if (dynamicFont != null) setFont(dynamicFont.get());
        if (dynamicOnEnter != null) setOnEnter(dynamicOnEnter.get());
        if (dynamicOutlineColor != null) setOutlineColor(dynamicOutlineColor.get());
        if (dynamicPlaceholderColor != null) setPlaceholderColor(dynamicPlaceholderColor.get());
        if (dynamicSelectionColor != null) setSelectionColor(dynamicSelectionColor.get());
        if (dynamicCursorColor != null) setCursorColor(dynamicCursorColor.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();

        if (hovered)
            ClientEvents.setIBeamCursor();
        if (hovered || focused)
            renderOutline(guiGraphics);

        int contentX = getContentX();
        int contentY = getContentY();
        int contentWidth = getContentWidth();
        int contentHeight = getContentHeight();
        int textY = getTextY();
        int textHeight = getTextHeight();

        var pose = guiGraphics.pose().last().pose();

        int scissorX = Math.round(contentX + pose.m30());
        int scissorY = Math.round(contentY + pose.m31());

        guiGraphics.enableScissor(
                scissorX,
                scissorY,
                scissorX + contentWidth,
                scissorY + contentHeight
        );

        int selectionStart = Math.min(textHelper.getCursorPos(), textHelper.getSelectionPos());
        int selectionEnd = Math.max(textHelper.getCursorPos(), textHelper.getSelectionPos());

        if (selectionStart != selectionEnd)
            renderSelection(
                    guiGraphics,
                    selectionStart,
                    selectionEnd,
                    textY,
                    textHeight
            );

        if (text.isEmpty() && placeholder != null)
            renderText(
                    guiGraphics,
                    placeholder,
                    contentX - displayOffset * textScale,
                    textY,
                    placeholderColor
            );
        else
            renderText(
                    guiGraphics,
                    text,
                    contentX - displayOffset * textScale,
                    textY,
                    textColor
            );

        guiGraphics.disableScissor();

        if (isCursorVisible())
            renderCursor(
                    guiGraphics,
                    getTextScreenX(textHelper.getCursorPos()),
                    textY,
                    textHeight
            );
    }

    private void renderOutline(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(
                x,
                y,
                x + width,
                y + 1,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y + height - 1,
                x + width,
                y + height,
                outlineColor
        );
        guiGraphics.fill(
                x,
                y + 1,
                x + 1,
                y + height - 1,
                outlineColor
        );
        guiGraphics.fill(
                x + width - 1,
                y + 1,
                x + width,
                y + height - 1,
                outlineColor
        );
    }

    private void renderSelection(
            @NotNull GuiGraphics guiGraphics,
            int start,
            int end,
            int textY,
            int textHeight
    ) {
        int selectionX1 = getTextScreenX(start);
        int selectionX2 = getTextScreenX(end);

        guiGraphics.fill(
                selectionX1,
                textY,
                selectionX2,
                textY + textHeight,
                selectionColor
        );
    }

    private void renderCursor(
            @NotNull GuiGraphics guiGraphics,
            int cursorX,
            int textY,
            int textHeight
    ) {
        guiGraphics.fill(
                cursorX,
                textY,
                cursorX + 1,
                textY + textHeight,
                cursorColor
        );
    }

    private void renderText(
            @NotNull GuiGraphics guiGraphics,
            @NotNull String text,
            double x,
            int y,
            int color
    ) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0);
        guiGraphics.pose().scale(textScale, textScale, 1.0f);

        guiGraphics.drawString(
                font,
                text,
                0,
                0,
                color,
                shadow
        );

        guiGraphics.pose().popPose();
    }


    @Mod.EventBusSubscriber(
            modid = MOD_ID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static final class ClientEvents {
        private static long arrowCursor;
        private static long iBeamCursor;


        @SubscribeEvent
        public static void onScreenRender(ScreenEvent.Render.Pre event) {
            setArrowCursor();
        }

        @SubscribeEvent
        public static void onScreenClosing(ScreenEvent.Closing event) {
            setArrowCursor();
        }


        private static void setArrowCursor() {
            Minecraft minecraft = Minecraft.getInstance();
            ensureCursors();
            GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), arrowCursor);
        }

        private static void setIBeamCursor() {
            Minecraft minecraft = Minecraft.getInstance();
            ensureCursors();
            GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), iBeamCursor);
        }

        private static void ensureCursors() {
            if (arrowCursor == 0L)
                arrowCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            if (iBeamCursor == 0L)
                iBeamCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
        }
    }
}
