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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class BasicsPage {
    private BasicsPage() {}

    public static @NotNull Page get(
            int x,
            int y,
            int width,
            int height,
            Supplier<Integer> purchaseMultiplier,
            Supplier<Double> availableCurrency
    ) {
        List<Element> buttons = new ArrayList<>();

        for (RegistryObject<Item> item : ModItems.ITEMS.getEntries())
            if (item.get() instanceof BasicItem basicItem)
                if (!(basicItem instanceof MatItem))
                    buttons.add(PurchaseButton.create(
                            basicItem,
                            () -> PurchasingTermialScreen.purchase(basicItem),
                            GuiConstants.PURCHASE_BUTTON_WIDTH,
                            purchaseMultiplier,
                            availableCurrency
                    ));

        FlowPanel panel = new FlowPanel(
                x,
                y,
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
