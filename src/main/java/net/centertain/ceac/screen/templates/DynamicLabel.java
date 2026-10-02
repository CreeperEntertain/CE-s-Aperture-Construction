package net.centertain.ceac.screen.templates;

import net.centertain.ceac.screen.elements.Label;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class DynamicLabel {
    private DynamicLabel() {}

    public static Label get(
            int color,
            float scale,
            @NotNull Supplier<@NotNull String> dynamicText
    ) {
        Label label = new Label(
                color,
                Component.literal(""),
                scale,
                false
        );
        label.setDynamicText(() -> Component.literal(dynamicText.get()));
        return label;
    }
}
