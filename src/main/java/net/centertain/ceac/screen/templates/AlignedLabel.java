package net.centertain.ceac.screen.templates;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Aligner;
import net.centertain.ceac.screen.elements.Label;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public final class AlignedLabel {
    private AlignedLabel() {}

    public static @NotNull Aligner get(String text, int height) {
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
