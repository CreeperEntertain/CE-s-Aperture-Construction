package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;

import java.util.List;

public final class RightContainer {
    private RightContainer() {}

    private static final int PADDING = 2;

    public static Element get(
            int x,
            int y,
            int width,
            int height
    ) {

        Rect background = new Rect(
                width,
                height,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );
        return new Container(
                x,
                y,
                width,
                height,
                List.of(
                        background
                )
        );
    }
}
