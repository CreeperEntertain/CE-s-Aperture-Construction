package net.centertain.ceac.screen;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.custom.BasicItem;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Screen;
import net.centertain.ceac.screen.templates.pages.BasicsPage;
import net.centertain.ceac.screen.templates.pages.MaterialPage;
import net.centertain.ceac.screen.templates.pages.MaterialShapePage;
import net.centertain.ceac.screen.templates.pages.TradePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PurchasingTermialScreen extends Screen {
    private final Player player;

    public PurchasingTermialScreen(@NotNull Player player) {
        super(Component.empty());
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
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

    @Override
    protected void init() {
        super.init();

        int x = GuiConstants.PAGE_TAB_VERTICALLY_ALIGNED_WIDTH + GuiConstants.PAGE_TAB_SPACING;
        int y = 0;
        int width = this.width - x;
        int height = this.height - y;

        List<Page> pages = List.of(
                TradePage.get(x, y, width, height),
                BasicsPage.get(x, y, width, height),
                //MaterialShapePage.get(x, y, width, height),
                MaterialPage.get(x, y, width, height)
        );

        addElement(new PageList(
                this.width,
                this.height,
                pages,
                PageList.TabPosition.LEFT,
                GuiConstants.PAGE_TAB_VERTICALLY_ALIGNED_WIDTH,
                true
        ));

        Label currency = new Label(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal("Example Text"),
                1.0f,
                false
        );

        int currencyLabelWidth = currency.getWidth() + (GuiConstants.ELEMENT_PADDING * 2);
        int currencyLabelHeight = currency.getHeight() + (GuiConstants.ELEMENT_PADDING * 2);

        Rect textBackground = new Rect(
                currencyLabelWidth,
                currencyLabelHeight,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75
        );
        Aligner textAlign = new Aligner(
                currencyLabelWidth,
                currencyLabelHeight,
                Aligner.Alignment.CENTER,
                currency
        );

        addElement(new Aligner(
                this.width,
                this.height,
                Aligner.Alignment.TOP_RIGHT,
                textBackground
        ));
        addElement(new Aligner(
                this.width,
                this.height,
                Aligner.Alignment.TOP_RIGHT,
                textAlign
        ));
    }

    public static void purchase(BasicItem item) {

    }
}
