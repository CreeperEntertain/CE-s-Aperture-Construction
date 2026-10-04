package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.constants.PriceConstants;
import net.centertain.ceac.material.network.SellItemPacket;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.AlignedLabel;
import net.centertain.ceac.screen.templates.DynamicLabel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LeftContainer {
    private LeftContainer() {}

    private static final int PADDING = 2;
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
            Supplier<ItemStack> getStack,
            BlockPos pos
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
                getDisplay(inventoryWidth, leftHeight, getStack, playerSupplier, pos)
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
            Supplier<Player> playerSupplier,
            BlockPos pos
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

        List<Element> panelContents = new ArrayList<>();
        panelContents.add(getItemAndButtons(
                ITEM_DISPLAY_SIZE,
                height,
                playerSupplier,
                getStack,
                pos
        ));
        StackPanel description = getDescription(
                width - ITEM_BOUNDS,
                playerSupplier,
                getStack
        );
        Padder descriptionPadder = new Padder(
                width - ITEM_BOUNDS,
                description.getHeight() + (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.ELEMENT_PADDING,
                description
        );
        panelContents.add(new ScrollContainer(
                0,
                0,
                descriptionPadder.getRealWidth(),
                height,
                ScrollContainer.Alignment.VERTICAL,
                descriptionPadder,
                descriptionPadder.getRealHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        ));
        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                height - (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.ELEMENT_PADDING * 2,
                GuiConstants.COLOR_TRANSPARENT,
                panelContents
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static Container getItemAndButtons(
            int width,
            int height,
            Supplier<Player> player,
            Supplier<ItemStack> stack,
            BlockPos pos
    ) {
        ItemDisplay item = new ItemDisplay(
                ITEM_DISPLAY_SIZE,
                ITEM_DISPLAY_SIZE,
                stack.get() == null
                        ? new ItemStack(Items.AIR)
                        : stack.get(),
                true
        );
        item.setDynamicStack(() -> stack.get() == null
                ? new ItemStack(Items.AIR)
                : stack.get()
        );
        Padder padder = new Padder(
                ITEM_BOUNDS,
                ITEM_BOUNDS,
                GuiConstants.ELEMENT_PADDING,
                item
        );

        Button sellOne = getSalesButton(ITEM_BOUNDS, "Sell", 1, player, stack, () ->
                sellItem(1, player, stack, pos)
        );
        Button sellTen = getSalesButton(ITEM_BOUNDS, "Sell 10", 10, player, stack, () ->
                sellItem(10, player, stack, pos)
        );
        Button sellHundred = getSalesButton(ITEM_BOUNDS, "Sell 100", 100, player, stack, () ->
                sellItem(100, player, stack, pos)
        );
        StackPanel buttonStack = new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                width,
                PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(sellOne, sellTen, sellHundred)
        );
        Aligner buttonAligner = new Aligner(
                width,
                height,
                Aligner.Alignment.BOTTOM_LEFT,
                buttonStack
        );

        return new Container(
                0,
                0,
                width,
                height,
                List.of(padder, buttonAligner)
        );
    }

    private static void sellItem(
            int amount,
            Supplier<Player> player,
            Supplier<ItemStack> stack,
            BlockPos pos
    ) {
        ItemStack selected = stack.get();

        if (selected == null)
            return;
        if (player.get().getInventory().countItem(selected.getItem()) < amount)
            return;
        Double worth = PriceConstants.get(selected);
        if (worth == null)
            return;

        ModNetworking.CHANNEL.sendToServer(new SellItemPacket(selected.getItem(), amount, pos));
    }

    @SuppressWarnings("SameParameterValue")
    private static Button getSalesButton(
            int width,
            String text,
            int multiplier,
            Supplier<Player> player,
            Supplier<ItemStack> stack,
            Runnable onPress
    ) {
        Aligner alignedLabel = AlignedLabel.get(text, GuiConstants.TAB_BUTTON_HEIGHT);
        if (alignedLabel.getElement() instanceof Label label) {
            label.setDynamicColor(() -> stack.get() == null || PriceConstants.get(stack.get()) == null
                    ? GuiConstants.COLOR_MINECRAFT_DARK_GRAY
                    : player.get().getInventory().countItem(stack.get().getItem()) >= multiplier
                            ? GuiConstants.COLOR_MINECRAFT_GREEN
                            : GuiConstants.COLOR_MINECRAFT_GRAY
            );
            label.setTextScale(0.75f);
        }
        alignedLabel.setWidth(width - PADDING);
        return new Button(
                0,
                0,
                alignedLabel,
                onPress,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                GuiConstants.COLOR_SOLID_WHITE
        );
    }

    private static StackPanel getDescription(
            int width,
            Supplier<Player> player,
            Supplier<ItemStack> stack
    ) {
        int white = GuiConstants.COLOR_MINECRAFT_WHITE;
        int green = GuiConstants.COLOR_MINECRAFT_GREEN;
        int gray = GuiConstants.COLOR_MINECRAFT_GRAY;
        int darkGray = GuiConstants.COLOR_MINECRAFT_DARK_GRAY;
        int darkRed = GuiConstants.COLOR_MINECRAFT_DARK_RED;

        float scale = 0.5f;

        Spacer spacer = new Spacer(0, GuiConstants.ELEMENT_PADDING);


        Label itemName = DynamicLabel.get(white, scale, () -> stack.get() == null || stack.get().getItem() == Items.AIR ? "" :
                "Name: " + stack.get().getHoverName().getString()
        );

        Label itemCount = DynamicLabel.get(white, scale, () -> stack.get() == null || stack.get().getItem() == Items.AIR ? "" :
                "In possession: " + player.get().getInventory().countItem(stack.get().getItem())
        );

        Label itemWorth = DynamicLabel.get(white, scale, () -> stack.get() == null || stack.get().getItem() == Items.AIR ? "" :
                "Sells for: ▲" + (PriceConstants.get(stack.get()) == null
                        ? 0 : String.format("%.2f", PriceConstants.get(stack.get())))
        );
        itemWorth.setDynamicColor(() -> PriceConstants.get(stack.get()) == null ? gray : green);

        Label itemHash = DynamicLabel.get(gray, scale, () -> stack.get() == null || stack.get().getItem() == Items.AIR ? "" :
                "Code: #" + stack.get().getItem().hashCode()
        );

        @SuppressWarnings("DataFlowIssue")
        Label itemSellTen = DynamicLabel.get(gray, scale, () -> PriceConstants.get(stack.get()) == null
                ? "" : "If sold x10: ▲" + String.format("%.2f", PriceConstants.get(stack.get()) * 10.0)
        );
        itemSellTen.setDynamicColor(() -> stack.get() == null ? gray :
                player.get().getInventory().countItem(stack.get().getItem()) >= 10 ? gray : darkRed
        );

        @SuppressWarnings("DataFlowIssue")
        Label itemSellHundred = DynamicLabel.get(gray, scale, () -> PriceConstants.get(stack.get()) == null
                ? "" : "If sold x100: ▲" + String.format("%.2f", PriceConstants.get(stack.get()) * 100.0)
        );
        itemSellHundred.setDynamicColor(() -> stack.get() == null ? gray :
                player.get().getInventory().countItem(stack.get().getItem()) >= 100 ? gray : darkRed
        );

        Label itemRefundable = DynamicLabel.get(darkGray, scale, () -> PriceConstants.get(stack.get()) == null
                ? "" : "* No refunds."
        );

        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                width,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(
                        itemName,
                        itemCount,
                        itemWorth,
                        itemHash,
                        spacer,
                        itemSellTen,
                        itemSellHundred,
                        spacer,
                        itemRefundable
                )
        );
    }
}
