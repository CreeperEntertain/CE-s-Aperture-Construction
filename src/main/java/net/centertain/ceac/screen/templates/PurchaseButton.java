package net.centertain.ceac.screen.templates;

import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.custom.BasicItem;
import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public final class PurchaseButton {
    private PurchaseButton() {}

    public static Button create(
            MaterialShape materialShape,
            Runnable onPress,
            int width,
            Supplier<Integer> purchaseMultiplier,
            Supplier<Double> availableCurrency
    ) {
        return constructButton(
                materialShape.getItemStack(),
                onPress,
                width,
                materialShape.getName().getString(),
                materialShape.getPrice(),
                materialShape.hashCode(),
                false,
                purchaseMultiplier,
                availableCurrency
        );
    }

    public static Button create(
            BasicItem basicItem,
            Runnable onPress,
            int width,
            Supplier<Integer> purchaseMultiplier,
            Supplier<Double> availanleCurrency
    ) {
        return constructButton(
                basicItem.getItemStack(),
                onPress,
                width,
                basicItem.getName(new ItemStack(basicItem)).getString(),
                basicItem.getPrice(),
                basicItem.hashCode(),
                true,
                purchaseMultiplier,
                availanleCurrency
        );
    }

    private static Button constructButton(
            ItemStack stack,
            Runnable onPress,
            int width,
            String title,
            double price,
            int id,
            boolean flatLighting,
            Supplier<Integer> purchaseMultiplier,
            Supplier<Double> availableCurrency
    ) {
        ItemDisplay display = new ItemDisplay(
                GuiConstants.PURCHASE_BUTTON_HEIGHT - (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.PURCHASE_BUTTON_HEIGHT - (GuiConstants.ELEMENT_PADDING * 2),
                stack,
                flatLighting
        );
        List<Element> horizontalElements = List.of(
                display,
                getDisplay(title, price, id, width, purchaseMultiplier, availableCurrency)
        );
        return new Button(
                0,
                0,
                getPadder(width, horizontalElements),
                onPress,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                GuiConstants.COLOR_SOLID_WHITE
        );
    }

    private static @NotNull StackPanel getDisplay(
            String title,
            double price,
            int id,
            int width,
            Supplier<Integer> purchaseMultiplier,
            Supplier<Double> availableCurrency
    ) {
        int canonicalWidth = width - GuiConstants.PURCHASE_BUTTON_HEIGHT - GuiConstants.ELEMENT_PADDING;
        List<Element> labels = List.of(
                getTitle(title, canonicalWidth),
                getPrice(price, purchaseMultiplier, availableCurrency),
                getId(id)
        );
        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                canonicalWidth,
                3,
                GuiConstants.COLOR_TRANSPARENT,
                labels
        );
    }

    private static @NotNull MarqueeLabel getTitle(String title, int width) {
        return new MarqueeLabel(
                width,
                Component.literal(title),
                MarqueeLabel.Alignment.LEFT,
                GuiConstants.COLOR_SOLID_WHITE,
                0.5f,
                false
        );
    }

    private static @NotNull Label getPrice(
            double price,
            Supplier<Integer> purchaseMultiplier,
            Supplier<Double> availableCurrency
    ) {

        String display = "$" + String.format("%.2f", price * purchaseMultiplier.get());
        Label label = new Label(
                GuiConstants.COLOR_MINECRAFT_GREEN,
                Component.literal(display),
                0.5f,
                false
        );
        label.setDynamicText(() -> Component.literal(
                "$" + String.format("%.2f", price * purchaseMultiplier.get())
        ));
        label.setDynamicColor(() -> availableCurrency.get() >= purchaseMultiplier.get() * price
                ? GuiConstants.COLOR_MINECRAFT_GREEN
                : GuiConstants.COLOR_MINECRAFT_RED
        );
        return label;
    }

    private static @NotNull Label getId(int id) {
        String display = "#" + id;
        return new Label(
                GuiConstants.COLOR_MINECRAFT_DARK_GRAY,
                Component.literal(display),
                0.5f,
                false
        );
    }

    private static @NotNull Padder getPadder(int width, List<Element> horizontalElements) {
        StackPanel horizonal = new StackPanel(
                0,
                0,
                StackPanel.Alignment.HORIZONTAL,
                GuiConstants.PURCHASE_BUTTON_HEIGHT - (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                horizontalElements
        );
        return new Padder(
                width,
                GuiConstants.PURCHASE_BUTTON_HEIGHT,
                GuiConstants.ELEMENT_PADDING,
                horizonal
        );
    }
}
