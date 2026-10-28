package net.centertain.ceac.item;

import net.centertain.ceac.item.custom.*;
import net.centertain.ceac.item.custom.materials.backpanel.*;
import net.centertain.ceac.item.custom.materials.concrete.ObservationConcreteWallMatItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);


    public static final RegistryObject<Item> PROP_PLACER = ITEMS.register("prop_placer", () -> new PropPlacerItem(new Item.Properties()));

    public static final RegistryObject<Item> DECAL = ITEMS.register("basics/decal", () -> new DecalItem(new Item.Properties()));
    public static final RegistryObject<Item> SCRAPER = ITEMS.register("basics/scraper", () -> new ScraperItem(new Item.Properties()));
    public static final RegistryObject<Item> WRENCH = ITEMS.register("basics/wrench", () -> new WrenchItem(new Item.Properties()));
    public static final RegistryObject<Item> CROWBAR = ITEMS.register("basics/crowbar", () -> new CrowbarItem(new Item.Properties()));


    public static final RegistryObject<Item> BARE_BACKPANEL = ITEMS.register("materials/backpanel/bare_backpanel", () -> new BareBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BARE_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/bare_backpanel_large", () -> new BareBackpanelLargeMatItem(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_BACKPANEL = ITEMS.register("materials/backpanel/green_backpanel", () -> new GreenBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/green_backpanel_large", () -> new GreenBackpanelLargeMatItem(new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_BACKPANEL = ITEMS.register("materials/backpanel/orange_backpanel", () -> new OrangeBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/orange_backpanel_large", () -> new OrangeBackpanelLargeMatItem(new Item.Properties()));
    public static final RegistryObject<Item> RUSTY_BACKPANEL = ITEMS.register("materials/backpanel/rusty_backpanel", () -> new RustyBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> RUSTY_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/rusty_backpanel_large", () -> new RustyBackpanelLargeMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WEATHERED_BACKPANEL = ITEMS.register("materials/backpanel/weathered_backpanel", () -> new WeatheredBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WEATHERED_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/weathered_backpanel_large", () -> new WeatheredBackpanelLargeMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_BACKPANEL = ITEMS.register("materials/backpanel/white_backpanel", () -> new WhiteBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/white_backpanel_large", () -> new WhiteBackpanelLargeMatItem(new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_BACKPANEL = ITEMS.register("materials/backpanel/yellow_backpanel", () -> new YellowBackpanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_BACKPANEL_LARGE = ITEMS.register("materials/backpanel/yellow_backpanel_large", () -> new YellowBackpanelLargeMatItem(new Item.Properties()));

    public static final RegistryObject<Item> OBSERVATION_CONCRETE_WALL = ITEMS.register("materials/concrete/observation_concrete_wall", () -> new ObservationConcreteWallMatItem(new Item.Properties()));


    private ModItems() {}

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
