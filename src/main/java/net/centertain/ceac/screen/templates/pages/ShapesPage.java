package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.block.ModBlocks;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.PurchaseButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShapesPage {
    private ShapesPage() {}

    public static @NotNull Page get(
            int x,
            int y,
            int width,
            int height
    ) {
        Map<String, List<Element>> buttons = new HashMap<>();

        for (RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries())
            if (block.get() instanceof MaterialShape shape) {
                String subcategory = shape.getSubcategory();
                if (subcategory == null)
                    subcategory = CategoryConstants.Sub.Shapes.MISC;
                if (!buttons.containsKey(subcategory))
                    buttons.put(subcategory, new ArrayList<>());
                buttons.get(subcategory).add(PurchaseButton.create(
                        shape,
                        () -> PurchasingTermialScreen.purchase(shape),
                        GuiConstants.PURCHASE_BUTTON_WIDTH
                ));
            }

        List<Element> foldouts = new ArrayList<>();

        for (String subcategory : buttons.keySet()) {
            FlowPanel panel = new FlowPanel(
                    x,
                    y,
                    FlowPanel.Alignment.HORIZONTAL,
                    width,
                    GuiConstants.ELEMENT_PADDING,
                    GuiConstants.COLOR_TRANSPARENT,
                    buttons.get(subcategory)
            );
            foldouts.add(new Foldout(
                    Component.literal(subcategory),
                    width,
                    GuiConstants.ELEMENT_PADDING,
                    true,
                    panel
            ));
        }

        StackPanel panel = new StackPanel(
                x,
                y,
                StackPanel.Alignment.VERTICAL,
                width,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                foldouts
        );

        return new Page(
                "Shapes",
                width,
                height,
                new ScrollContainer(
                        x,
                        y,
                        width,
                        height,
                        ScrollContainer.Alignment.VERTICAL,
                        panel,
                        panel.getHeight(),
                        GuiConstants.FLOW_SCROLL_SPEED
                )
        );
    }
}
