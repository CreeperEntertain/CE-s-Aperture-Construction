package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Container;
import net.centertain.ceac.screen.elements.Label;
import net.centertain.ceac.screen.elements.Rect;
import net.centertain.ceac.screen.elements.StackPanel;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LeftContainer {
    private LeftContainer() {}

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
        List<Element> containerContents = new ArrayList<>();
        containerContents.add(new Rect(inventoryWidth, leftHeight, GuiConstants.COLOR_TRANSLUCENT_BLACK_75));

        Label reactive = new Label(
                GuiConstants.COLOR_MINECRAFT_WHITE,
                Component.literal(Integer.toString(getStack.get().getCount())),
                1.0f,
                false
        );
        reactive.setDynamicText(() -> Component.literal(Integer.toString(getStack.get().getCount())));
        containerContents.add(reactive);

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
}
