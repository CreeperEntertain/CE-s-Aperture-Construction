package net.centertain.ceac.client;

import net.centertain.ceac.decal.ScraperXray;
import net.centertain.ceac.decal.client.DecalPlacement;
import net.centertain.ceac.item.custom.DecalItem;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.item.custom.ScraperItem;
import net.centertain.ceac.material.MaterialPlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class MenuSelector {
    private static final List<Menu> MENUS = List.of(
            new Menu(
                    DecalPlacement::getPrecisePlacement,
                    DecalPlacement::setPrecisePlacement,
                    player ->
                            player.getMainHandItem().getItem() instanceof DecalItem ||
                            player.getOffhandItem().getItem() instanceof DecalItem
            ), new Menu(
                    MaterialPlacement::getAdjustOffset,
                    MaterialPlacement::setAdjustOffset,
                    player ->
                            player.getMainHandItem().getItem() instanceof MatItem ||
                            player.getOffhandItem().getItem() instanceof MatItem
            ), new Menu(
                    ScraperXray::getXrayViewActive,
                    ScraperXray::setXrayViewActive,
                    player ->
                            player.getMainHandItem().getItem() instanceof ScraperItem ||
                            player.getOffhandItem().getItem() instanceof ScraperItem
            )
    );

    private record Menu(
            BooleanSupplier active,
            Consumer<Boolean> setActive,
            Predicate<Player> available
    ) {}

    private MenuSelector() {}


    public static void setMenuState(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
            return;
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null)
            return;
        Player player = minecraft.player;
        if (player == null)
            return;

        int activeIndex = -1;

        for (int i = 0; i < MENUS.size(); i++) {
            Menu menu = MENUS.get(i);
            if (!menu.available().test(player))
                continue;
            if (!menu.active().getAsBoolean())
                continue;
            activeIndex = i;
            break;
        }

        if (activeIndex == -1) {
            for (Menu menu : MENUS) {
                if (!menu.available().test(player))
                    continue;
                menu.setActive().accept(true);
                event.setCanceled(true);
            }
            return;
        }

        MENUS.get(activeIndex).setActive().accept(false);

        for (int offset = 1; offset < MENUS.size(); offset++) {
            Menu menu = MENUS.get((activeIndex + offset) % MENUS.size());
            if (!menu.available().test(player))
                continue;
            menu.setActive().accept(true);
            break;
        }

        event.setCanceled(true);
    }
}
