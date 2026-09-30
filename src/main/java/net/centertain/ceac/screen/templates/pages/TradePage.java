package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Page;
import net.centertain.ceac.screen.elements.Rect;
import net.centertain.ceac.screen.elements.ScrollContainer;
import net.centertain.ceac.screen.framework.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TradePage {
    private TradePage() {}

    public static @NotNull Page get(
            int width,
            int height
    ) {
        Rect rect = new Rect(0, 0, GuiConstants.COLOR_TRANSPARENT);
        return new Page(
                "Trade",
                width,
                height,
                new ScrollContainer(
                        0,
                        0,
                        width,
                        height,
                        ScrollContainer.Alignment.VERTICAL,
                        rect,
                        rect.getHeight(),
                        GuiConstants.STACK_SCROLL_SPEED
                )
        );
    }
}
