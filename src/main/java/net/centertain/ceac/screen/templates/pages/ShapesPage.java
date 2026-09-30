package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.screen.elements.Empty;
import net.centertain.ceac.screen.elements.Page;
import org.jetbrains.annotations.NotNull;

public final class ShapesPage {
    private ShapesPage() {}

    public static @NotNull Page get(
            int x,
            int y,
            int width,
            int height
    ) {
        return new Page(
                "Shapes",
                width,
                height,
                new Empty()
        );
    }
}
