package net.centertain.ceac.screen.templates;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PurchasingRibbon {
    private PurchasingRibbon() {}

    private static Aligner alignedLabel;

    public static @NotNull Element get(
            int x,
            int y,
            int width,
            int height,
            Consumer<Integer> increasePurchaseMultiplier,
            Consumer<Integer> decreasePurchaseMultiplier,
            Supplier<Integer> purchaseMultipler
    ) {
        int multiplierHeight = 20;
        int buttonWidth = 15;
        alignedLabel = getAlignedLabel(purchaseMultipler.get() + "x", multiplierHeight);
        List<Element> multiplierContents = List.of(
                getTextButton("<<", buttonWidth, multiplierHeight, decreasePurchaseMultiplier, 10, purchaseMultipler),
                getTextButton("<", buttonWidth, multiplierHeight, decreasePurchaseMultiplier, 1, purchaseMultipler),
                new Spacer(GuiConstants.ELEMENT_PADDING, multiplierHeight),
                alignedLabel,
                new Spacer(GuiConstants.ELEMENT_PADDING, multiplierHeight),
                getTextButton(">", buttonWidth, multiplierHeight, increasePurchaseMultiplier, 1, purchaseMultipler),
                getTextButton(">>", buttonWidth, multiplierHeight, increasePurchaseMultiplier, 10, purchaseMultipler)
        );
        StackPanel multiplier = new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                multiplierHeight,
                0,
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
            int callbackParam,
            Supplier<Integer> purchaseMultiplier
    ) {
        Label label = new Label(
                GuiConstants.COLOR_SOLID_GRAY,
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
                () -> {
                    callback.accept(callbackParam);
                    if (alignedLabel.getElement() instanceof Label multiplier)
                        multiplier.setText(Component.literal(purchaseMultiplier.get() + "x"));
                },
                GuiConstants.COLOR_TRANSPARENT,
                GuiConstants.COLOR_SOLID_GRAY
        );
    }

    private static @NotNull Aligner getAlignedLabel(String text, int height) {
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
