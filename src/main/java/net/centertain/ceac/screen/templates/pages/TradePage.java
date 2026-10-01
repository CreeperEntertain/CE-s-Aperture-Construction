package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.templates.inventory.InventoryTemplate;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public final class TradePage {
    private TradePage() {}

    public static @NotNull Page get(
            int x,
            int y,
            int width,
            int height,
            Supplier<Player> playerSupplier
    ) {
        int inventoryWidth = InventoryTemplate.getInventoryWidth();
        int inventoryHeight = InventoryTemplate.getInventoryHeight();

        int leftHeight = height - inventoryHeight - GuiConstants.ELEMENT_PADDING;

        Container inventory = InventoryTemplate.get(
                x,
                y,
                inventoryWidth,
                inventoryHeight,
                playerSupplier,
                () -> {} // TODO: Figure out how to route it back up into the trade page
        );
        Container leftContainer = new Container(
                0,
                0,
                inventoryWidth,
                leftHeight,
                new Rect(
                        inventoryWidth,
                        leftHeight,
                        GuiConstants.COLOR_TRANSLUCENT_BLACK_75
                )
        );
        StackPanel leftStack = new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                inventoryWidth,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(inventory, leftContainer)
        );


        return new Page(
                "Trade",
                width,
                height,
                leftStack
        );
    }
}
