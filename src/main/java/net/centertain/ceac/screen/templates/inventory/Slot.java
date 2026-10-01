package net.centertain.ceac.screen.templates.inventory;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.elements.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
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
            Runnable onPress
    ) {
        ItemDisplay display = new ItemDisplay(
                width,
                height,
                stackSupplier.get(),
                true
        );
        display.setDynamicStack(stackSupplier);

        Label amount = new Label(
                GuiConstants.COLOR_MINECRAFT_WHITE,
                Component.literal(getStackAmount(stackSupplier.get())),
                0.5f,
                true
        );
        amount.setDynamicText(() -> Component.literal(getStackAmount(stackSupplier.get())));
        Aligner amountAligner = new Aligner(
                width,
                height,
                Aligner.Alignment.BOTTOM_RIGHT,
                amount
        );

        Container slotContainer = new Container(
                x,
                y,
                width,
                height,
                List.of(display, amountAligner),
                GuiConstants.COLOR_TRANSPARENT
        );
        return new Button(
                x,
                y,
                slotContainer,
                onPress,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                GuiConstants.COLOR_SOLID_WHITE
        );
    }
}
