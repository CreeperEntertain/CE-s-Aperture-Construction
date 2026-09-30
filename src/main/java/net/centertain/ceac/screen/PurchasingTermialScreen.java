package net.centertain.ceac.screen;

import net.centertain.ceac.block.ModBlocks;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.screen.framework.Screen;
import net.centertain.ceac.screen.templates.PurchaseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

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

        addElement(PurchaseButton.create(
                (MaterialShape) ModBlocks.MATERIAL_SHAPE_BlOCK.get(),
                this::example,
                width
        ));
        addElement(PurchaseButton.create(
                (MatItem) ModItems.OBSERVATION_CONCRETE_WALL.get(),
                this::example,
                width
        ));
    }
}
