package net.centertain.ceac.screen;

import net.centertain.ceac.block.ModBlocks;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.screen.elements.Foldout;
import net.centertain.ceac.screen.elements.StackPanel;
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

        addElement(new Foldout(
                Component.literal("HELLO!!"),
                width,
                true,
                stack
        ));
    }
}
