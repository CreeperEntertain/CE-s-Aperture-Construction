package net.centertain.ceac.screen.templates;

import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PurchaseButton {
    private PurchaseButton() {}

    public static Button create(
            MaterialShape materialShape,
            Runnable onPress,
            int width
    ) {
        ItemStack stack = materialShape.getItemStack(1);
        return constructButton(
                stack,
                onPress,
                width,
                materialShape.getName().getString(),
                materialShape.getPrice(),
                materialShape.hashCode()
        );
    }

    public static Button create(
            MatItem matItem,
            Runnable onPress,
            int width
    ) {
        ItemStack stack = matItem.getItemStack(1);
        return constructButton(
                stack,
                onPress,
                width,
                matItem.getName(new ItemStack(matItem)).getString(),
                matItem.getPrice(),
                matItem.hashCode()
        );
    }

    private static Button constructButton(
            ItemStack stack,
            Runnable onPress,
            int width,
            String title,
            double price,
            int id
    ) {
        ModelDisplay display = new ModelDisplay(
                GuiConstants.PURCHASE_BUTTON_HEIGHT - (GuiConstants.ELEMENT_PADDING * 2),
                GuiConstants.PURCHASE_BUTTON_HEIGHT - (GuiConstants.ELEMENT_PADDING * 2),
                stack
        );
        List<Element> horizontalElements = List.of(
                display,
                getDisplay(title, price, id, width)
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
            int width
    ) {
        int canonicalWidth = width - GuiConstants.PURCHASE_BUTTON_HEIGHT + GuiConstants.ELEMENT_PADDING;
        List<Element> labels = List.of(
                getTitle(title, canonicalWidth),
                getPrice(price),
                getId(id)
        );
        return new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                canonicalWidth,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                labels
        );
    }

    private static @NotNull MarqueeLabel getTitle(String title, int width) {
        return new MarqueeLabel(
                width,
                Component.literal(title),
                GuiConstants.COLOR_SOLID_WHITE,
                1.0f,
                false
        );
    }

    private static @NotNull Label getPrice(double price) {
        String display = String.format("%.2f", price);
        return new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal(display),
                false
        );
    }

    private static @NotNull Label getId(int id) {
        String display = "#" + id;
        return new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal(display),
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
