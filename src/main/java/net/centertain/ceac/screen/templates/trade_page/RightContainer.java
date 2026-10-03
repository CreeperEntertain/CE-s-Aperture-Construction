package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.constants.PriceConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.AlignedLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
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
        int innerX = x + GuiConstants.ELEMENT_PADDING;
        int innerY = y + GuiConstants.ELEMENT_PADDING;
        int innerWidth = width - (GuiConstants.ELEMENT_PADDING * 2);
        int innerHeight = height - (GuiConstants.ELEMENT_PADDING * 2);

        Spacer spacer = new Spacer(innerWidth, GuiConstants.ELEMENT_PADDING);

        List<Element> displayElements = List.of(
                new Label(
                        GuiConstants.COLOR_MINECRAFT_WHITE,
                        Component.literal("Item Values:"),
                        1.0f,
                        false
                ),
                spacer,
                getItemListings(innerWidth)
        );

        StackPanel display = new StackPanel(
                innerX,
                innerY,
                StackPanel.Alignment.VERTICAL,
                innerWidth,
                0,
                GuiConstants.COLOR_TRANSPARENT,
                displayElements
        );
        ScrollContainer scroll = new ScrollContainer(
                innerX,
                innerY,
                innerWidth,
                innerHeight,
                ScrollContainer.Alignment.VERTICAL,
                display,
                display.getHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        );
        Padder mainPadder = new Padder(
                width,
                height,
                GuiConstants.ELEMENT_PADDING,
                scroll
        );
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
                        background,
                        mainPadder
                )
        );
    }

    private static Element getItemListings(int width) {
        var prices = PriceConstants.getSortedEntries();

        List<Element> entries = new ArrayList<>();

        for (var entry : prices)
            entries.add(getItemListing(
                    16,
                    entry.getKey(),
                    entry.getValue()
            ));

        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                width,
                PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                entries
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static Element getItemListing(
            int height,
            Item item,
            double value
    ) {
        @SuppressWarnings("SuspiciousNameCombination") // Fuck off
        ItemDisplay itemDisplay = new ItemDisplay(
                height,
                height,
                new ItemStack(item),
                true
        );

        Aligner valueDisplay = AlignedLabel.get("▲" + String.format("%.2f", value), height);
        if (valueDisplay.getElement() instanceof Label label)
            label.setColor(GuiConstants.COLOR_SOLID_GRAY);

        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                height,
                PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(itemDisplay, valueDisplay)
        );
    }
}
