package net.centertain.ceac.screen.templates.trade_page;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Slot {
    private Slot() {}

    private static String getStackAmount(ItemStack stack) {
        return stack.getCount() == 0 || stack.getCount() == 1
                ? ""
                : Integer.toString(stack.getCount());
    }

    public static Button get(
            int x,
            int y,
            int width,
            int height,
            Supplier<ItemStack> stackSupplier,
            Consumer<ItemStack> onPress
    ) {
        ItemDisplay display = new ItemDisplay(
                width,
                height,
                stackSupplier.get(),
                true
        );
        display.setDynamicStack(stackSupplier);
        return new Button(
                x,
                y,
                display,
                () -> onPress.accept(stackSupplier.get()),
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                GuiConstants.COLOR_SOLID_WHITE
        );
    }
}
