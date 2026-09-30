package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.PurchaseButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MaterialsPage {
    private MaterialsPage() {}

    public static @NotNull Page get(
            int x,
            int y,
            int width,
            int height
    ) {
        Map<String, List<Element>> buttons = new HashMap<>();

        for (RegistryObject<Item> item : ModItems.ITEMS.getEntries())
            if (item.get() instanceof MatItem matItem) {
                String subcategory = matItem.getSubcategory();
                if (subcategory == null)
                    subcategory = CategoryConstants.Sub.Mats.MISC;
                if (!buttons.containsKey(subcategory))
                    buttons.put(subcategory, new ArrayList<>());
                buttons.get(subcategory).add(PurchaseButton.create(
                        matItem,
                        () -> PurchasingTermialScreen.purchase(matItem),
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
                "Materials",
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
