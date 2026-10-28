package net.centertain.ceac.item;

import net.centertain.ceac.item.custom.*;
import net.centertain.ceac.item.custom.materials.backpanel.*;
import net.centertain.ceac.item.custom.materials.concrete.*;
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

    public static final RegistryObject<Item> BARE_CONCRETE_CAST = ITEMS.register("materials/concrete/bare_concrete_cast", () -> new BareConcreteCastMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CHECKERED_CONCRETE_FLOOR_TILES = ITEMS.register("materials/concrete/checkered_concrete_floor_tiles", () -> new CheckeredConcreteFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CINDERBLOCKS = ITEMS.register("materials/concrete/cinderblocks", () -> new CinderblocksMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_CONCRETE_FLOOR_TILES = ITEMS.register("materials/concrete/dark_concrete_floor_tiles", () -> new DarkConcreteFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DIRTY_CONCRETE_FLOOR = ITEMS.register("materials/concrete/dirty_concrete_floor", () -> new DirtyConcreteFloorMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DIRTY_CONCRETE_FLOOR_TILES = ITEMS.register("materials/concrete/dirty_concrete_floor_tiles", () -> new DirtyConcreteFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DIRTY_REBAR_CONCRETE_BLOCKS = ITEMS.register("materials/concrete/dirty_rebar_concrete_blocks", () -> new DirtyRebarConcreteBlocksMatItem(new Item.Properties()));
    public static final RegistryObject<Item> FACTORY_CONCRETE_WALL = ITEMS.register("materials/concrete/factory_concrete_wall", () -> new FactoryConcreteWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> GLUED_CONCRETE_PANELS = ITEMS.register("materials/concrete/glued_concrete_panels", () -> new GluedConcretePanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> HEAVY_CONCRETE_CEILING = ITEMS.register("materials/concrete/heavy_concrete_ceiling", () -> new HeavyConcreteCeilingMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_CONCRETE_FLOOR_TILES = ITEMS.register("materials/concrete/light_concrete_floor_tiles", () -> new LightConcreteFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> MODULAR_CONCRETE = ITEMS.register("materials/concrete/modular_concrete", () -> new ModularConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> OBSERVATION_CONCRETE_WALL = ITEMS.register("materials/concrete/observation_concrete_wall", () -> new ObservationConcreteWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PAINTED_CONCRETE_BLOCK_PILLAR = ITEMS.register("materials/concrete/painted_concrete_block_pillar", () -> new PaintedConcreteBlockPillarMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PAINTED_CONCRETE_TILES = ITEMS.register("materials/concrete/painted_concrete_tiles", () -> new PaintedConcreteTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PAINTED_FACTORY_CONCRETE_WALL = ITEMS.register("materials/concrete/painted_factory_concrete_wall", () -> new PaintedFactoryConcreteWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> REBAR_CONCRETE = ITEMS.register("materials/concrete/rebar_concrete", () -> new RebarConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> REBAR_CONCRETE_BLOCKS = ITEMS.register("materials/concrete/rebar_concrete_blocks", () -> new RebarConcreteBlocksMatItem(new Item.Properties()));
    public static final RegistryObject<Item> RUSTED_REBAR_CONCRETE = ITEMS.register("materials/concrete/rusted_rebar_concrete", () -> new RustedRebarConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> SMALL_DIRTY_CONCRETE_FLOOR_TILES = ITEMS.register("materials/concrete/small_dirty_concrete_floor_tiles", () -> new SmallDirtyConcreteFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> STAINED_HEAVY_CONCRETE_CEILING = ITEMS.register("materials/concrete/stained_heavy_concrete_ceiling", () -> new StainedHeavyConcreteCeilingMatItem(new Item.Properties()));
    public static final RegistryObject<Item> STAINED_MODULAR_CONCRETE = ITEMS.register("materials/concrete/stained_modular_concrete", () -> new StainedModularConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> STAINED_REBAR_CONCRETE = ITEMS.register("materials/concrete/stained_rebar_concrete", () -> new StainedRebarConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WEATHERED_REBAR_CONCRETE = ITEMS.register("materials/concrete/weathered_rebar_concrete", () -> new StainedRebarConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WITHERED_MODULAR_CONCRETE = ITEMS.register("materials/concrete/withered_modular_concrete", () -> new WitheredModularConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WITHERED_REBAR_CONCRETE_BLOCKS = ITEMS.register("materials/concrete/withered_rebar_concrete_blocks", () -> new WitheredRebarConcreteBlocksMatItem(new Item.Properties()));


    private ModItems() {}

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
