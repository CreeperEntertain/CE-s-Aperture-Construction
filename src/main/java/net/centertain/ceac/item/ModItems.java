package net.centertain.ceac.item;

import net.centertain.ceac.item.custom.*;
import net.centertain.ceac.item.custom.materials.backpanel.*;
import net.centertain.ceac.item.custom.materials.concrete.ObservationConcreteWallMatItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.InvocationTargetException;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final Item.Properties PROPERTIES = new Item.Properties();


    public static final RegistryObject<Item> PROP_PLACER = ITEMS.register("prop_placer", () -> new PropPlacerItem(PROPERTIES));

    public static final RegistryObject<Item> DECAL = ITEMS.register("decal", () -> new DecalItem(PROPERTIES));
    public static final RegistryObject<Item> SCRAPER = ITEMS.register("scraper", () -> new ScraperItem(PROPERTIES));
    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(PROPERTIES));
    public static final RegistryObject<Item> CROWBAR = ITEMS.register("crowbar", () -> new CrowbarItem(PROPERTIES));


    public static final RegistryObject<Item> BARE_BACKPANEL = ITEMS.register("bare_backpanel", () -> new BareBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> BARE_BACKPANEL_LARGE = ITEMS.register("bare_backpanel_large", () -> new BareBackpanelLargeMatItem(PROPERTIES));
    public static final RegistryObject<Item> GREEN_BACKPANEL = ITEMS.register("green_backpanel", () -> new GreenBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> GREEN_BACKPANEL_LARGE = ITEMS.register("green_backpanel_large", () -> new GreenBackpanelLargeMatItem(PROPERTIES));
    public static final RegistryObject<Item> ORANGE_BACKPANEL = ITEMS.register("orange_backpanel", () -> new OrangeBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> ORANGE_BACKPANEL_LARGE = ITEMS.register("orange_backpanel_large", () -> new OrangeBackpanelLargeMatItem(PROPERTIES));
    public static final RegistryObject<Item> RUSTY_BACKPANEL = ITEMS.register("rusty_backpanel", () -> new RustyBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> RUSTY_BACKPANEL_LARGE = ITEMS.register("rusty_backpanel_large", () -> new RustyBackpanelLargeMatItem(PROPERTIES));
    public static final RegistryObject<Item> WEATHERED_BACKPANEL = ITEMS.register("weathered_backpanel", () -> new WeatheredBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> WEATHERED_BACKPANEL_LARGE = ITEMS.register("weathered_backpanel_large", () -> new WeatheredBackpanelLargeMatItem(PROPERTIES));
    public static final RegistryObject<Item> WHITE_BACKPANEL = ITEMS.register("white_backpanel", () -> new WhiteBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> WHITE_BACKPANEL_LARGE = ITEMS.register("white_backpanel_large", () -> new WhiteBackpanelLargeMatItem(PROPERTIES));
    public static final RegistryObject<Item> YELLOW_BACKPANEL = ITEMS.register("yellow_backpanel", () -> new YellowBackpanelMatItem(PROPERTIES));
    public static final RegistryObject<Item> YELLOW_BACKPANEL_LARGE = ITEMS.register("yellow_backpanel_large", () -> new YellowBackpanelLargeMatItem(PROPERTIES));

    public static final RegistryObject<Item> OBSERVATION_CONCRETE_WALL = ITEMS.register("observation_concrete_wall", () -> new ObservationConcreteWallMatItem(PROPERTIES));


    private ModItems() {}

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
