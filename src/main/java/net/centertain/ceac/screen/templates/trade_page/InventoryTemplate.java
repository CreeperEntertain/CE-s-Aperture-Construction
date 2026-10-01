package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.Container;
import net.centertain.ceac.screen.elements.FlowPanel;
import net.centertain.ceac.screen.elements.StackPanel;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class InventoryTemplate {
    private InventoryTemplate() {}

    private static final int PADDING = 2;
    private static final int SLOT_SIZE = 16;
    private static final int ROW = 9;
    private static final int MAX_SLOT = 35;

    public static int getInventoryWidth() {
        return (SLOT_SIZE * ROW) + (PADDING * (ROW - 1));
    }
    public static int getInventoryHeight() {
        return (SLOT_SIZE * 4) + (PADDING * 2) + GuiConstants.ELEMENT_PADDING;
    }

    private static ItemStack getSlotContents(
            Supplier<Player> playerSupplier,
            int slot
    ) {
        Inventory inventory = playerSupplier.get().getInventory();
        return inventory.getItem(slot);
    }

    public static Container get(
            int x,
            int y,
            int width,
            int height,
            Supplier<Player> playerSupplier,
            Consumer<ItemStack> onSlotPress
    ) {
        List<Element> hotbar = new ArrayList<>();
        for (int i = 0; i < ROW; i++) {
            int iterator = i;
            hotbar.add(Slot.get(
                    0,
                    0,
                    SLOT_SIZE,
                    SLOT_SIZE,
                    () -> getSlotContents(playerSupplier, iterator),
                    onSlotPress
            ));
        }
        StackPanel hotbarPanel = new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                SLOT_SIZE,
                PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                hotbar
        );

        List<Element> inventory = new ArrayList<>();
        for (int i = 9; i < MAX_SLOT + 1; i++) {
            int iterator = i;
            inventory.add(Slot.get(
                    0,
                    0,
                    SLOT_SIZE,
                    SLOT_SIZE,
                    () -> getSlotContents(playerSupplier, iterator),
                    onSlotPress
            ));
        }
        FlowPanel inventoryPanel = new FlowPanel(
                FlowPanel.Alignment.HORIZONTAL,
                getInventoryWidth(),
                PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                inventory
        );

        StackPanel fullInventory = new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                getInventoryWidth(),
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                List.of(inventoryPanel, hotbarPanel)
        );

        return new Container(
                x,
                y,
                width,
                height,
                fullInventory
        );
    }
}
