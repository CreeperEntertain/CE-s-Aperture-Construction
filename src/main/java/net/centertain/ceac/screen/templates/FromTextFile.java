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
        Scanner scanner = new Scanner(
                Objects.requireNonNull(FromTextFile.class.getResourceAsStream("/" + file)),
                StandardCharsets.UTF_8
        ).useDelimiter("\\A");
        String text = scanner.hasNext() ? scanner.next() : "";
        text = text
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        return new TextBox(
                width,
                text,
                0.75f,
                2,
                GuiConstants.COLOR_SOLID_LIGHT_GRAY,
                false
        );
    }
}
