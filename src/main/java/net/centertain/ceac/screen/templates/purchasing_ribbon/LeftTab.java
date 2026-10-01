package net.centertain.ceac.screen.templates.purchasing_ribbon;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.AlignedLabel;
import net.centertain.ceac.screen.templates.PurchasingRibbon;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LeftTab {
    private LeftTab() {}

    private static Aligner alignedLabel;

    private static final int PADDING = PurchasingRibbon.PADDING;

    public static @NotNull Container get(
            int x,
            int y,
            int height,
            int upperHeight,
            int leftWidth,
            int lowerHeight,
            Supplier<Integer> purchaseMultipler,
            int multiplierHeight,
            int buttonWidth,
            Consumer<Integer> decreasePurchaseMultiplier,
            Consumer<Integer> increasePurchaseMultiplier
    ) {
        alignedLabel = AlignedLabel.get(purchaseMultipler.get() + "x", multiplierHeight);
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
                lowerHeight,
                Aligner.Alignment.CENTER,
                multiplier
        );
        alignedMultiplier.setX(alignedMultiplier.getTopSpan());
        Container lower = new Container(
                0,
                upperHeight,
                leftWidth,
                lowerHeight,
                alignedMultiplier,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        Label title = new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal("Purchase Multiplier"),
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
                x,
                y,
                leftWidth,
                height,
                List.of(upper, lower)
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
}
