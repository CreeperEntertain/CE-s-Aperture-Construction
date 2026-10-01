package net.centertain.ceac.screen.templates;

import net.centertain.ceac.screen.elements.Empty;
import net.centertain.ceac.screen.framework.Element;
import org.jetbrains.annotations.NotNull;

public final class PurchasingRibbon {
    private PurchasingRibbon() {}

    public static @NotNull Element get(
            int x,
            int y,
            int width,
            int height
    ) {
        return new Empty();
    }
}
