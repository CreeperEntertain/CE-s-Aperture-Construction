package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.templates.AlignedLabel;
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
        Aligner title = AlignedLabel.get(
                "About This Mod",
                GuiConstants.TAB_BUTTON_HEIGHT + GuiConstants.ELEMENT_PADDING
        );
        title.setX(title.getTopSpan());
        Container titleContainer = new Container(
                x,
                y,
                width,
                title.getHeight(),
                title,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        StackPanel contents = getContents(width);

        ScrollContainer mainScroll = new ScrollContainer(
                0,
                0,
                width,
                height - titleContainer.getHeight() - GuiConstants.ELEMENT_PADDING,
                ScrollContainer.Alignment.VERTICAL,
                contents,
                contents.getHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        );
        StackPanel main = new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                width,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(titleContainer, mainScroll)
        );

        return new Page(
                "Information",
                width,
                height,
                main
        );
    }

    private static StackPanel getContents(int width) {
        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                width,
                2,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(
                        getSection(width, "Basics", "basics.txt"),
                        getSection(width, "Trading", "trading.txt"),
                        getSection(width, "Purchasing", "purchasing.txt"),
                        getSection(width, "Selling Back", "selling_back.txt"),
                        getSection(width, "Decals", "decals.txt"),
                        getSection(width, "Shapes", "shapes.txt"),
                        getSection(width, "Materials", "materials.txt"),
                        getSection(width, "Tools", "tools.txt"),
                        getSection(width, "Attribution", "attribution.txt")
                )
        );
    }

    private static Foldout getSection(
            int width,
            String title,
            String filename
    ) {
        TextBox text = FromTextFile.get(
                width - (GuiConstants.ELEMENT_PADDING * 2),
                "assets/ceac/textboxes/info_page/" + filename
        );
        Padder textPadder = new Padder(
                width,
                text.getHeight() + (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.ELEMENT_PADDING,
                text
        );
        Container textWrapper = new Container(
                0,
                0,
                width,
                textPadder.getRealHeight(),
                textPadder,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );
        return new Foldout(
                Component.literal(title),
                width,
                1,
                false,
                textWrapper
        );
    }
}