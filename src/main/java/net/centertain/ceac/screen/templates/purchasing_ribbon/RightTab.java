package net.centertain.ceac.screen.templates.purchasing_ribbon;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Aligner;
import net.centertain.ceac.screen.elements.Container;
import net.centertain.ceac.screen.elements.Empty;
import net.centertain.ceac.screen.elements.Label;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.AlignedLabel;
import net.centertain.ceac.screen.templates.PurchasingRibbon;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public final class RightTab {
    private RightTab() {}

    private static Aligner alignedLabel;

    private static final int PADDING = PurchasingRibbon.PADDING;
    private static final double EPSILON = 1.0e-6;

    public static @NotNull Container get(
            int x,
            int y,
            int height,
            int upperHeight,
            int leftWidth,
            int rightWidth,
            int lowerHeight,
            Supplier<Double> availableCurrency,
            int textHeight
    ) {
        alignedLabel = AlignedLabel.get("$" + String.format("%.2f", availableCurrency.get()), textHeight);
        if (alignedLabel.getElement() instanceof Label label) {
            label.setDynamicText(() -> Component.literal(
                    "$" + String.format("%.2f", availableCurrency.get())
            ));
            label.setDynamicColor(() -> availableCurrency.get() > EPSILON
                    ? GuiConstants.COLOR_MINECRAFT_GREEN
                    : availableCurrency.get() < -EPSILON
                            ? GuiConstants.COLOR_MINECRAFT_RED
                            : GuiConstants.COLOR_MINECRAFT_GRAY
            );
        }
        Aligner alignedCurrencyDisplay = new Aligner(
                alignedLabel.getWidth(),
                lowerHeight,
                Aligner.Alignment.CENTER,
                alignedLabel
        );
        alignedCurrencyDisplay.setX(GuiConstants.ELEMENT_PADDING);
        Container lower = new Container(
                0,
                upperHeight,
                rightWidth,
                lowerHeight,
                alignedCurrencyDisplay,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        Label title = new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal("Available Currency"),
                0.5f,
                false
        );
        Aligner titleAligner = new Aligner(
                title.getWidth() + (PADDING * 2),
                upperHeight,
                Aligner.Alignment.CENTER,
                title
        );
        Container upper = new Container(
                0,
                0,
                titleAligner.getWidth(),
                upperHeight,
                titleAligner,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        return new Container(
                x + leftWidth + PADDING,
                y,
                rightWidth,
                height,
                List.of(upper, lower)
        );
    }
}
