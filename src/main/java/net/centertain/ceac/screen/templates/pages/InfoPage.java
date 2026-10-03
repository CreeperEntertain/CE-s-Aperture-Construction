package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.templates.FromTextFile;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class InfoPage {
    private InfoPage() {}

    public static @NotNull Page get(
            int x,
            int y,
            int width,
            int height
    ) {
        int innerX = GuiConstants.ELEMENT_PADDING; // Container local space
        int innerY = GuiConstants.ELEMENT_PADDING;
        int innerWidth = width - (GuiConstants.ELEMENT_PADDING * 2);
        int innerHeight = height - (GuiConstants.ELEMENT_PADDING * 2);

        Label title = new Label(
                innerX,
                innerY,
                GuiConstants.COLOR_MINECRAFT_WHITE,
                Component.literal("About This Terminal"),
                1.0f,
                false
        );

        StackPanel contents = getContents(innerX, innerY, innerWidth);

        ScrollContainer mainScroll = new ScrollContainer(
                innerX,
                innerY,
                innerWidth,
                innerHeight,
                ScrollContainer.Alignment.VERTICAL,
                contents,
                contents.getHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        );
        StackPanel main = new StackPanel(
                innerX,
                innerY,
                StackPanel.Alignment.VERTICAL,
                innerWidth,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(title, mainScroll)
        );

        Container panel = new Container(
                x,
                y,
                width,
                height,
                main,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        return new Page(
                "Information",
                width,
                height,
                new ScrollContainer(
                        x,
                        y,
                        width,
                        height,
                        ScrollContainer.Alignment.VERTICAL,
                        panel,
                        height,
                        GuiConstants.STACK_SCROLL_SPEED
                )
        );
    }

    private static StackPanel getContents(
            int x,
            int y,
            int width
    ) {
        int white = GuiConstants.COLOR_SOLID_WHITE;
        String files = "assets/ceac/textboxes/info_page/";
        Spacer spacer = new Spacer(0, 0);

        Label firstTitle = new Label(white, Component.literal(
                "Basics"
        ), 1.0f, false);
        TextBox first = FromTextFile.get(width, files + "1.txt");

        return new StackPanel(
                x,
                y,
                StackPanel.Alignment.VERTICAL,
                width,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(
                        firstTitle,
                        first,
                        spacer
                )
        );
    }
}