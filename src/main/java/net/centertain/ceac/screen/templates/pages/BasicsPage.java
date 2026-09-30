package net.centertain.ceac.screen.templates.pages;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.item.custom.BasicItem;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.centertain.ceac.screen.elements.FlowPanel;
import net.centertain.ceac.screen.elements.Page;
import net.centertain.ceac.screen.elements.ScrollContainer;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.templates.PurchaseButton;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public final class BasicsPage {
    private BasicsPage() {}

    public static Page get(
            int width,
            int height
    ) {
        List<Element> buttons = new ArrayList<>();

        for (RegistryObject<Item> item : ModItems.ITEMS.getEntries())
            if (item.get() instanceof BasicItem basicItem)
                if (!(basicItem instanceof MatItem))
                    buttons.add(PurchaseButton.create(
                            basicItem,
                            () -> PurchasingTermialScreen.purchase(basicItem),
                            GuiConstants.PURCHASE_BUTTON_WIDTH
                    ));

        FlowPanel panel = new FlowPanel(
                0,
                0,
                FlowPanel.Alignment.HORIZONTAL,
                width,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                buttons
        );

        return new Page(
                "Basics",
                width,
                height,
                new ScrollContainer(
                        0,
                        0,
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
