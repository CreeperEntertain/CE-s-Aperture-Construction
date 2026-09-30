package net.centertain.ceac.screen;

import net.centertain.ceac.block.ModBlocks;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.Screen;
import net.centertain.ceac.screen.templates.PurchaseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PurchasingTermialScreen extends Screen {
    public PurchasingTermialScreen() {
        super(Component.empty());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void example() {

    }

    @Override
    protected void init() {
        super.init();

        List<Element> buttons = List.of(
                PurchaseButton.create(
                        (MaterialShape) ModBlocks.MATERIAL_SHAPE_BlOCK.get(),
                        this::example,
                        width
                ),
                PurchaseButton.create(
                        (MatItem) ModItems.OBSERVATION_CONCRETE_WALL.get(),
                        this::example,
                        width
                )
        );

        StackPanel stack = new StackPanel(
                0,
                0,
                StackPanel.Alignment.VERTICAL,
                width,
                0,
                GuiConstants.COLOR_TRANSPARENT,
                buttons
        );

        Foldout foldout = new Foldout(
                Component.literal("HELLO!!"),
                width,
                true,
                stack
        );

        Rect rect = new Rect(
                100,
                100,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );

        List<Page> pages = List.of(
                new Page(
                        "Elements",
                        width,
                        height,
                        foldout
                ),
                new Page(
                        "Rect",
                        width,
                        height,
                        foldout
                )
        );

        addElement(new PageList(
                width,
                height,
                pages,
                PageList.TabPosition.LEFT,
                50,
                true
        ));
    }
}
