package net.centertain.ceac.screen.templates;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.TextBox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public final class FromTextFile {
    private FromTextFile() {}

    public static TextBox get(
            int width,
            String file
    ) {
        String text = new Scanner(
                Objects.requireNonNull(FromTextFile.class.getResourceAsStream("/" + file)),
                StandardCharsets.UTF_8
        )
                .useDelimiter("\\A")
                .next()
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\n", "\\n");

        return new TextBox(
                width,
                text,
                0.75f,
                0,
                GuiConstants.COLOR_SOLID_GRAY,
                false
        );
    }
}
