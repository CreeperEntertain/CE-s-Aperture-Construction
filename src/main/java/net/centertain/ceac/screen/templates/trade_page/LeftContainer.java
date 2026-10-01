package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LeftContainer {
    private LeftContainer() {}

    private static final int ITEM_DISPLAY_SIZE = 32;
    private static final int ITEM_BOUNDS = ITEM_DISPLAY_SIZE + (GuiConstants.ELEMENT_PADDING * 2);

    public static Element get(
            int x,
            int y,
            int inventoryWidth,
            int inventoryHeight,
            int leftHeight,
            Supplier<Player> playerSupplier,
            Consumer<ItemStack> setStack,
            Supplier<ItemStack> getStack
    ) {
        Container inventory = InventoryTemplate.get(
                x,
                y,
                inventoryWidth,
                inventoryHeight,
                playerSupplier,
                setStack
        );
        List<Element> containerContents = List.of(
                new Rect(
                        ITEM_BOUNDS,
                        0,
                        inventoryWidth - ITEM_BOUNDS,
                        leftHeight,
                        GuiConstants.COLOR_TRANSLUCENT_BLACK_75
                ),
                new Rect(
                        ITEM_BOUNDS,
                        ITEM_BOUNDS,
                        GuiConstants.COLOR_TRANSLUCENT_BLACK_75
                ),
                getDisplay(inventoryWidth, leftHeight, getStack, playerSupplier)
        );

        Container leftContainer = new Container(
                x,
                y,
                inventoryWidth,
                leftHeight,
                containerContents
        );
        return new StackPanel(
                x,
                y,
                StackPanel.Alignment.VERTICAL,
                inventoryWidth,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(inventory, leftContainer)
        );
    }

    private static Element getDisplay(
            int width,
            int height,
            Supplier<ItemStack> getStack,
            Supplier<Player> playerSupplier
    ) {
        ItemDisplay item = new ItemDisplay(
                ITEM_DISPLAY_SIZE,
                ITEM_DISPLAY_SIZE,
                getStack.get() == null
                        ? new ItemStack(Items.AIR)
                        : getStack.get(),
                true
        );
        item.setDynamicStack(() -> getStack.get() == null
                ? new ItemStack(Items.AIR)
                : getStack.get()
        );

        Label itemCount = new Label(
                GuiConstants.COLOR_MINECRAFT_WHITE,
                Component.literal(""),
                0.5f,
                false
        );
        itemCount.setDynamicText(() -> Component.literal(getStack.get() == null
                ? ""
                : "In possession: " + playerSupplier.get().getInventory().countItem(getStack.get().getItem()
        )));

        StackPanel horizontal = new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                height - (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.ELEMENT_PADDING * 2,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(item, itemCount)
        );
        return new Padder(
                width,
                height,
                GuiConstants.ELEMENT_PADDING,
                horizontal
        );
    }
}
