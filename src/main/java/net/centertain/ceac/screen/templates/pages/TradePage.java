package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.templates.trade_page.InventoryTemplate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public final class TradePage {
    private TradePage() {}

    private static @Nullable ItemStack stack = null;

    private static void setStack(@Nullable ItemStack stack) {
        TradePage.stack = stack;
    }
    private static @Nullable ItemStack getStack() {
        return stack;
    }

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
                TradePage::setStack
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
