package net.centertain.ceac.screen.templates;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Container;
import net.centertain.ceac.screen.elements.Empty;
import net.centertain.ceac.screen.framework.Element;
import org.jetbrains.annotations.NotNull;

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
        return new Container(
                x,
                y,
                width,
                height,
                new Empty(),
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );
    }
}
