package net.centertain.ceac.screen.templates;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public final class PurchasingRibbon {
    private PurchasingRibbon() {}

    public static @NotNull Element get(
            int x,
            int y,
            int width,
            int height,
            Consumer<Integer> increasePurchaseMultiplier,
            Consumer<Integer> decreasePurchaseMultiplier,
            int purchaseMultipler
    ) {
        int multiplierHeight = 20;
        int buttonWidth = 15;
        List<Element> multiplierContents = List.of(
                getTextButton("<<", buttonWidth, multiplierHeight, decreasePurchaseMultiplier, 10),
                getTextButton("<", buttonWidth, multiplierHeight, decreasePurchaseMultiplier, 1),
                getAlignedLabel(purchaseMultipler + "x", multiplierHeight),
                getTextButton(">", buttonWidth, multiplierHeight, increasePurchaseMultiplier, 1),
                getTextButton(">>", buttonWidth, multiplierHeight, increasePurchaseMultiplier, 10)
        );
        StackPanel multiplier = new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                multiplierHeight,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                multiplierContents
        );
        Aligner alignedMultiplier = new Aligner(
                multiplier.getWidth(),
                height,
                Aligner.Alignment.CENTER,
                multiplier
        );
        alignedMultiplier.setX(alignedMultiplier.getTopSpan());

        List<Element> elements = List.of(
            alignedMultiplier
        );
        return new Container(
                x,
                y,
                width,
                height,
                elements,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );
    }

    private static @NotNull Element getTextButton(
            String text,
            int width,
            int height,
            Consumer<Integer> callback,
            int callbackParam
    ) {
        Label label = new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal(text),
                1.0f,
                false
        );
        Aligner aligner = new Aligner(
                width,
                height,
                Aligner.Alignment.CENTER,
                label
        );
        return new Button(
                0,
                0,
                aligner,
                () -> callback.accept(callbackParam),
                GuiConstants.COLOR_TRANSPARENT,
                GuiConstants.COLOR_SOLID_WHITE
        );
    }

    private static @NotNull Element getAlignedLabel(String text, int height) {
        Label label = new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal(text),
                1.0f,
                false
        );
        return new Aligner(
                label.getWidth(),
                height,
                Aligner.Alignment.CENTER,
                label
        );
    }
}
