package net.centertain.ceac.screen.templates;

import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.purchasing_ribbon.LeftTab;
import net.centertain.ceac.screen.templates.purchasing_ribbon.RightTab;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PurchasingRibbon {
    private PurchasingRibbon() {}

    public static final int PADDING = 2;

    public static @NotNull Element get(
            int x,
            int y,
            int width,
            int height,
            Consumer<Integer> increasePurchaseMultiplier,
            Consumer<Integer> decreasePurchaseMultiplier,
            Supplier<Integer> purchaseMultipler,
            Supplier<Double> availableCurrency
    ) {
        int lowerHeight = height / 4 * 3;
        int upperHeight = height - lowerHeight;

        int leftWidth = width / 2;
        int rightWidth = width - leftWidth - PADDING;

        int multiplierHeight = lowerHeight - (PADDING * 2);
        int buttonWidth = 15;

        Container leftTab = LeftTab.get(
                0,
                0,
                height,
                upperHeight,
                leftWidth,
                lowerHeight,
                purchaseMultipler,
                multiplierHeight,
                buttonWidth,
                decreasePurchaseMultiplier,
                increasePurchaseMultiplier
        );

        Container rightTab = RightTab.get(
                0,
                0,
                height,
                upperHeight,
                leftWidth,
                rightWidth,
                lowerHeight,
                availableCurrency,
                multiplierHeight
        );

        return new Container(
                x,
                y,
                width,
                height,
                List.of(leftTab, rightTab)
        );
    }
}
