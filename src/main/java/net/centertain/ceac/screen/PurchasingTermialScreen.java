package net.centertain.ceac.screen;

import net.centertain.ceac.block.custom.BasicBlock;
import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.item.custom.BasicItem;
import net.centertain.ceac.material.PlayerCurrency;
import net.centertain.ceac.material.network.BuyItemPacket;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Screen;
import net.centertain.ceac.screen.templates.PurchasingRibbon;
import net.centertain.ceac.screen.templates.pages.BasicsPage;
import net.centertain.ceac.screen.templates.pages.MaterialsPage;
import net.centertain.ceac.screen.templates.pages.ShapesPage;
import net.centertain.ceac.screen.templates.pages.TradePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PurchasingTermialScreen extends Screen {

    private final Player player;
    private int purchaseMultiplier = 1;

    public PurchasingTermialScreen(@NotNull Player player) {
        super(Component.empty());
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }
    public int getPurchaseMultiplier() {
        return purchaseMultiplier;
    }

    public double getCurrency() {
        return PlayerCurrency.get(player);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
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

    private void increasePurchaseMultiplier(int amount) {
        purchaseMultiplier = Math.min(64, purchaseMultiplier + amount);
    }

    private void decreasePurchaseMultiplier(int amount) {
        purchaseMultiplier = Math.max(1, purchaseMultiplier - amount);
    }

    @Override
    protected void init() {
        super.init();

        int ribbonHeight = 30;

        addElement(PurchasingRibbon.get(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                width - (GuiConstants.SCREEN_PADDING * 2),
                ribbonHeight,
                this::increasePurchaseMultiplier,
                this::decreasePurchaseMultiplier,
                this::getPurchaseMultiplier,
                this::getCurrency
        ));

        int x = GuiConstants.PAGE_TAB_VERTICALLY_ALIGNED_WIDTH + GuiConstants.PAGE_TAB_SPACING + GuiConstants.SCREEN_PADDING;
        int y = GuiConstants.SCREEN_PADDING + ribbonHeight + GuiConstants.ELEMENT_PADDING;
        int width = this.width - x - GuiConstants.SCREEN_PADDING;
        int height = this.height - y - GuiConstants.SCREEN_PADDING;

        List<Page> pages = List.of(
                new TradePage(x, y, width, height, this::getPlayer),
                BasicsPage.get(x, y, width, height, this::getPurchaseMultiplier, this::getCurrency, this::purchase),
                ShapesPage.get(x, y, width, height, this::getPurchaseMultiplier, this::getCurrency, this::purchase),
                MaterialsPage.get(x, y, width, height, this::getPurchaseMultiplier, this::getCurrency, this::purchase)
        );

        addElement(new PageList(
                GuiConstants.SCREEN_PADDING,
                y,
                this.width - GuiConstants.SCREEN_PADDING,
                this.height - y - GuiConstants.SCREEN_PADDING,
                pages,
                PageList.TabPosition.LEFT,
                GuiConstants.PAGE_TAB_VERTICALLY_ALIGNED_WIDTH,
                true
        ));
    }

    public void purchase(BasicItem item) {
        processItemPurchase(item);
    }
    public void purchase(BasicBlock block) {
        processItemPurchase(block.asItem());
    }

    private void processItemPurchase(Item item) {
        ModNetworking.CHANNEL.sendToServer(new BuyItemPacket(item, purchaseMultiplier));
    }
}
