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
        int innerX = GuiConstants.ELEMENT_PADDING; // Container local space
        int innerY = GuiConstants.ELEMENT_PADDING;
        int innerWidth = width - (GuiConstants.ELEMENT_PADDING * 2);
        int innerHeight = height - (GuiConstants.ELEMENT_PADDING * 2);

        Spacer spacer = new Spacer(innerWidth, GuiConstants.ELEMENT_PADDING);

        Label title = new Label(
                GuiConstants.COLOR_MINECRAFT_WHITE,
                Component.literal("Item Values:"),
                1.0f,
                false
        );

        Element listings = getItemListings(innerWidth);
        ScrollContainer listingScroll = new ScrollContainer(
                innerX,
                innerY,
                innerWidth,
                innerHeight - spacer.getHeight() - title.getHeight(),
                ScrollContainer.Alignment.VERTICAL,
                listings,
                listings.getHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        );

        List<Element> displayElements = List.of(
                title,
                spacer,
                listingScroll
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
        Padder mainPadder = new Padder(
                width,
                height,
                GuiConstants.ELEMENT_PADDING,
                display
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
                    32,
                    16,
                    entry.getKey(),
                    entry.getValue()
            ));

        return new FlowPanel(
                0,
                0,
                FlowPanel.Alignment.HORIZONTAL,
                width,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                entries
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static Element getItemListing(
            int width,
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
        if (valueDisplay.getElement() instanceof Label label) {
            label.setColor(GuiConstants.COLOR_SOLID_GRAY);
            label.setTextScale(0.75f);
        }
        valueDisplay.setWidth(width);
        valueDisplay.setAlignment(Aligner.Alignment.LEFT);

        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                height,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(itemDisplay, valueDisplay)
        );
    }
}
