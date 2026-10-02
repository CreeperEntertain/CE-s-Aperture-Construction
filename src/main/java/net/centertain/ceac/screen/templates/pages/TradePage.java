package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.trade_page.InventoryTemplate;
import net.centertain.ceac.screen.templates.trade_page.LeftContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TradePage extends Page {
    private @Nullable ItemStack stack;

    public void setStack(@Nullable ItemStack stack) {
        this.stack = stack == null || stack.getItem() == Items.AIR ? null : stack;
    }
    private @Nullable ItemStack getStack() {
        return stack;
    }

    public TradePage(
            int x,
            int y,
            int width,
            int height,
            Supplier<Player> playerSupplier,
            Consumer<Double> increaseCurrency
    ) {
        super("Trade", width, height, new Empty());

        int inventoryWidth = InventoryTemplate.getInventoryWidth();
        int inventoryHeight = InventoryTemplate.getInventoryHeight();

        int leftHeight = height - inventoryHeight - GuiConstants.ELEMENT_PADDING;

        Element leftStack = LeftContainer.get(
                x,
                y,
                inventoryWidth,
                inventoryHeight,
                leftHeight,
                playerSupplier,
                this::setStack,
                this::getStack,
                increaseCurrency
        );

        setX(x);
        setY(y);
        setElement(leftStack);
    }
}