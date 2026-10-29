package net.centertain.ceac.item;

import net.centertain.ceac.item.custom.*;
import net.centertain.ceac.item.custom.materials.backpanel.*;
import net.centertain.ceac.item.custom.materials.concrete.*;
import net.centertain.ceac.item.custom.materials.metal.*;
import net.centertain.ceac.item.custom.materials.plaster.*;
import net.centertain.ceac.item.custom.materials.plastic.BeigePlasticWallMatItem;
import net.centertain.ceac.item.custom.materials.plastic.BeigeReinforcedPlasticWallMatItem;
import net.centertain.ceac.item.custom.materials.plastic.ReinforcedPlasticWallMatItem;
import net.centertain.ceac.item.custom.materials.plastic.YellowReinforcedPlasticWallMatItem;
import net.centertain.ceac.item.custom.materials.tile.*;
import net.centertain.ceac.item.custom.materials.wallpaper.GenericWallpaperMatItem;
import net.centertain.ceac.item.custom.materials.wallpaper.LobbyWallpaperMatItem;
import net.centertain.ceac.item.custom.materials.wood.*;
import net.centertain.ceac.material.materials.tile.CheckeredDarkCeramicTilesDirtyMaterial;
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
    public static final RegistryObject<Item> WEATHERED_REBAR_CONCRETE = ITEMS.register("materials/concrete/weathered_rebar_concrete", () -> new WeatheredRebarConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WITHERED_MODULAR_CONCRETE = ITEMS.register("materials/concrete/withered_modular_concrete", () -> new WitheredModularConcreteMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WITHERED_REBAR_CONCRETE_BLOCKS = ITEMS.register("materials/concrete/withered_rebar_concrete_blocks", () -> new WitheredRebarConcreteBlocksMatItem(new Item.Properties()));

    public static final RegistryObject<Item> BLACK_METAL_TILES = ITEMS.register("materials/metal/black_metal_tiles", () -> new BlackMetalTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X05 = ITEMS.register("materials/metal/black_panel_05x05", () -> new BlackPanel05x05MatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X05_DIRTY = ITEMS.register("materials/metal/black_panel_05x05_dirty", () -> new BlackPanel05x05DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X05_WITHERED = ITEMS.register("materials/metal/black_panel_05x05_withered", () -> new BlackPanel05x05WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X1 = ITEMS.register("materials/metal/black_panel_05x1", () -> new BlackPanel05x1MatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X1_DIRTY = ITEMS.register("materials/metal/black_panel_05x1_dirty", () -> new BlackPanel05x1DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X1_WITHERED = ITEMS.register("materials/metal/black_panel_05x1_withered", () -> new BlackPanel05x1WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X2 = ITEMS.register("materials/metal/black_panel_05x2", () -> new BlackPanel05x2MatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X2_DIRTY = ITEMS.register("materials/metal/black_panel_05x2_dirty", () -> new BlackPanel05x2DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_05X2_WITHERED = ITEMS.register("materials/metal/black_panel_05x2_withered", () -> new BlackPanel05x2WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_1X1 = ITEMS.register("materials/metal/black_panel_1x1", () -> new BlackPanel1x1MatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_1X1_DIRTY = ITEMS.register("materials/metal/black_panel_1x1_dirty", () -> new BlackPanel1x1DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_1X1_WITHERED = ITEMS.register("materials/metal/black_panel_1x1_withered", () -> new BlackPanel1x1WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_1X2 = ITEMS.register("materials/metal/black_panel_1x2", () -> new BlackPanel1x2MatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_1X2_DIRTY = ITEMS.register("materials/metal/black_panel_1x2_dirty", () -> new BlackPanel1x2DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_1X2_WITHERED = ITEMS.register("materials/metal/black_panel_1x2_withered", () -> new BlackPanel1x2WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_2X2 = ITEMS.register("materials/metal/black_panel_2x2", () -> new BlackPanel2x2MatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_2X2_DIRTY = ITEMS.register("materials/metal/black_panel_2x2_dirty", () -> new BlackPanel2x2DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_PANEL_2X2_WITHERED = ITEMS.register("materials/metal/black_panel_2x2_withered", () -> new BlackPanel2x2WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_ROUNDED_METAL_TILES = ITEMS.register("materials/metal/black_rounded_metal_tiles", () -> new BlackRoundedMetalTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLUE_METAL_PANELS = ITEMS.register("materials/metal/blue_metal_panels", () -> new BlueMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLUE_RECTANGULAR_METAL_PANELS = ITEMS.register("materials/metal/blue_rectangular_metal_panels", () -> new BlueRectangularMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BLUE_SQUARE_METAL_PANELS = ITEMS.register("materials/metal/blue_square_metal_panels", () -> new BlueSquareMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BOLTED_METAL_WALL = ITEMS.register("materials/metal/bolted_metal_wall", () -> new BoltedMetalWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CABLE_WALL = ITEMS.register("materials/metal/cable_wall", () -> new CableWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CORRUGATED_METAL = ITEMS.register("materials/metal/corrugated_metal", () -> new CorrugatedMetalMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_METAL_FLOOR_TILES = ITEMS.register("materials/metal/dark_metal_floor_tiles", () -> new DarkMetalFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_ROUNDED_METAL_TILES = ITEMS.register("materials/metal/dark_rounded_metal_tiles", () -> new DarkRoundedMetalTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_RUSTED_METAL_PANELS = ITEMS.register("materials/metal/dark_rusted_metal_panels", () -> new DarkRustedMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_WITHERED_PAINTED_METAL_PANEL = ITEMS.register("materials/metal/dark_withered_painted_metal_panel", () -> new DarkWitheredPaintedMetalPanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DIRTY_RUSTED_METAL_PANELS = ITEMS.register("materials/metal/dirty_rusted_metal_panels", () -> new DirtyRustedMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> FACTORY_CONTAINER = ITEMS.register("materials/metal/factory_container", () -> new FactoryContainerMatItem(new Item.Properties()));
    public static final RegistryObject<Item> FACTORY_CONTAINER_SIDE = ITEMS.register("materials/metal/factory_container_side", () -> new FactoryContainerSideMatItem(new Item.Properties()));
    public static final RegistryObject<Item> FRAMED_GREEN_METAL_PANELS = ITEMS.register("materials/metal/framed_green_metal_panels", () -> new FramedGreenMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_METAL_PANELS = ITEMS.register("materials/metal/green_metal_panels", () -> new GreenMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LACQUERED_IRREGULAR_METAL_PANELS = ITEMS.register("materials/metal/lacquered_irregular_metal_panels", () -> new LacqueredIrregularMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LACQUERED_RECTANGULAR_METAL_PANELS = ITEMS.register("materials/metal/lacquered_rectangular_metal_panels", () -> new LacqueredRectangularMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LARGE_BLACK_METAL_TILES = ITEMS.register("materials/metal/large_black_metal_tiles", () -> new LargeBlackMetalTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LARGE_BLUE_METAL_PANEL = ITEMS.register("materials/metal/large_blue_metal_panel", () -> new LargeBlueMetalPanelMatItem(new Item.Properties()));
    public static final RegistryObject<Item> METAL_BEAM_CONNECTOR = ITEMS.register("materials/metal/metal_beam_connector", () -> new MetalBeamConnectorMatItem(new Item.Properties()));
    public static final RegistryObject<Item> METAL_FLOOR_TILES = ITEMS.register("materials/metal/metal_floor_tiles", () -> new MetalFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PAINTED_METAL_WALL = ITEMS.register("materials/metal/painted_metal_wall", () -> new PaintedMetalWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PAINTED_SHEET_METAL = ITEMS.register("materials/metal/painted_sheet_metal", () -> new PaintedSheetMetalMatItem(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_GREEN_METAL_PANELS = ITEMS.register("materials/metal/reinforced_green_metal_panels", () -> new ReinforcedGreenMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> RUSTED_RECTANGULAR_METAL_PANELS = ITEMS.register("materials/metal/rusted_rectangular_metal_panels", () -> new RustedRectangularMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> RUSTED_SHEET_METAL = ITEMS.register("materials/metal/rusted_sheet_metal", () -> new RustedSheetMetalMatItem(new Item.Properties()));
    public static final RegistryObject<Item> SMALL_BLACK_METAL_TILES = ITEMS.register("materials/metal/small_black_metal_tiles", () -> new SmallBlackMetalTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WEATHERED_BLUE_RECTANGULAR_METAL_PANELS = ITEMS.register("materials/metal/weathered_blue_rectangular_metal_panels", () -> new WeatheredBlueRectangularMetalPanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WEATHERED_CORRUGATED_METAL = ITEMS.register("materials/metal/weathered_corrugated_metal", () -> new WeatheredCorrugatedMetalMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WITHERED_CABLE_WALL = ITEMS.register("materials/metal/withered_cable_wall", () -> new WitheredCableWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WITHERED_PAINTED_METAL_PANEL = ITEMS.register("materials/metal/withered_painted_metal_panel", () -> new WitheredPaintedMetalPanelMatItem(new Item.Properties()));

    public static final RegistryObject<Item> AGED_PLASTER = ITEMS.register("materials/plaster/aged_plaster", () -> new AgedPlasterMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BARE_PLASTER = ITEMS.register("materials/plaster/bare_plaster", () -> new BarePlasterMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CLEAN_PLASTER = ITEMS.register("materials/plaster/clean_plaster", () -> new CleanPlasterMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_AGED_PLASTER = ITEMS.register("materials/plaster/dark_aged_plaster", () -> new DarkAgedPlasterMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_CLEAN_PLASTER = ITEMS.register("materials/plaster/dark_clean_plaster", () -> new DarkCleanPlasterMatItem(new Item.Properties()));
    public static final RegistryObject<Item> MIXED_AGED_PLASTER = ITEMS.register("materials/plaster/mixed_aged_plaster", () -> new MixedAgedPlasterMatItem(new Item.Properties()));
    public static final RegistryObject<Item> MIXED_CLEAN_PLASTER = ITEMS.register("materials/plaster/mixed_clean_plaster", () -> new MixedCleanPlasterMatItem(new Item.Properties()));

    public static final RegistryObject<Item> BEIGE_PLASTIC_WALL = ITEMS.register("materials/plastic/beige_plastic_wall", () -> new BeigePlasticWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> BEIGE_REINFORCED_PLASTIC_WALL = ITEMS.register("materials/plastic/beige_reinforced_plastic_wall", () -> new BeigeReinforcedPlasticWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_PLASTIC_WALL = ITEMS.register("materials/plastic/reinforced_plastic_wall", () -> new ReinforcedPlasticWallMatItem(new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_REINFORCED_PLASTIC_WALL = ITEMS.register("materials/plastic/yellow_reinforced_plastic_wall", () -> new YellowReinforcedPlasticWallMatItem(new Item.Properties()));

    public static final RegistryObject<Item> CHECKERED_DARK_CERAMIC_TILES = ITEMS.register("materials/tile/checkered_dark_ceramic_tiles", () -> new CheckeredDarkCeramicTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CHECKERED_DARK_CERAMIC_TILES_DIRTY = ITEMS.register("materials/tile/checkered_dark_ceramic_tiles_dirty", () -> new CheckeredDarkCeramicTilesDirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CHECKERED_ORANGE_TILES = ITEMS.register("materials/tile/checkered_orange_tiles", () -> new CheckeredOrangeTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CHECKERED_WHITE_CERAMIC_TILES = ITEMS.register("materials/tile/checkered_white_ceramic_tiles", () -> new CheckeredWhiteCeramicTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_CERAMIC_TILES = ITEMS.register("materials/tile/dark_ceramic_tiles", () -> new DarkCeramicTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_CERAMIC_TILES_DIRTY = ITEMS.register("materials/tile/dark_ceramic_tiles_dirty", () -> new DarkCeramicTilesDirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_OFFICE_FLOOR_TILES = ITEMS.register("materials/tile/dark_office_floor_tiles", () -> new DarkOfficeFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_PAINTED_BRICKS = ITEMS.register("materials/tile/dark_painted_bricks", () -> new DarkPaintedBricksMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DARK_PIN_HOLE_CEILING_TILES = ITEMS.register("materials/tile/dark_pin-hole_ceiling_tiles", () -> new DarkPinHoleCeilingTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DECAYED_WHITE_PAINTED_BRICKS = ITEMS.register("materials/tile/decayed_white_painted_bricks", () -> new DecayedWhitePaintedBricksMatItem(new Item.Properties()));
    public static final RegistryObject<Item> DIRTY_FLOOR_TILES = ITEMS.register("materials/tile/dirty_floor_tiles", () -> new DirtyFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_TILES = ITEMS.register("materials/tile/orange_tiles", () -> new OrangeTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PATTERNED_WHITE_PANELS = ITEMS.register("materials/tile/patterned_white_panels", () -> new PatternedWhitePanelsMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_CERAMIC_TILES = ITEMS.register("materials/tile/white_ceramic_tiles", () -> new WhiteCeramicTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_OFFICE_FLOOR_TILES = ITEMS.register("materials/tile/white_office_floor_tiles", () -> new WhiteOfficeFloorTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PAINTED_BRICKS = ITEMS.register("materials/tile/white_painted_bricks", () -> new WhitePaintedBricksMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X05 = ITEMS.register("materials/tile/white_panel_05x05", () -> new WhitePanel05x05MatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X05_DIRTY = ITEMS.register("materials/tile/white_panel_05x05_dirty", () -> new WhitePanel05x05DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X05_WITHERED = ITEMS.register("materials/tile/white_panel_05x05_withered", () -> new WhitePanel05x05WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X1 = ITEMS.register("materials/tile/white_panel_05x1", () -> new WhitePanel05x1MatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X1_DIRTY = ITEMS.register("materials/tile/white_panel_05x1_dirty", () -> new WhitePanel05x1DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X1_WITHERED = ITEMS.register("materials/tile/white_panel_05x1_withered", () -> new WhitePanel05x1WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X2 = ITEMS.register("materials/tile/white_panel_05x2", () -> new WhitePanel05x2MatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X2_DIRTY = ITEMS.register("materials/tile/white_panel_05x2_dirty", () -> new WhitePanel05x2DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_05X2_WITHERED = ITEMS.register("materials/tile/white_panel_05x2_withered", () -> new WhitePanel05x2WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X1 = ITEMS.register("materials/tile/white_panel_1x1", () -> new WhitePanel1x1MatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X1_DIRTY = ITEMS.register("materials/tile/white_panel_1x1_dirty", () -> new WhitePanel1x1DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X1_WITHERED = ITEMS.register("materials/tile/white_panel_1x1_withered", () -> new WhitePanel1x1WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X2 = ITEMS.register("materials/tile/white_panel_1x2", () -> new WhitePanel1x2MatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X2_VARIATION = ITEMS.register("materials/tile/white_panel_1x2_variation", () -> new WhitePanel1x2VariationMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X2_DIRTY = ITEMS.register("materials/tile/white_panel_1x2_dirty", () -> new WhitePanel1x2DirtyMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PANEL_1X2_WITHERED = ITEMS.register("materials/tile/white_panel_1x2_withered", () -> new WhitePanel1x2WitheredMatItem(new Item.Properties()));
    public static final RegistryObject<Item> WHITE_PIN_HOLE_CEILING_TILES = ITEMS.register("materials/tile/white_pin-hole_ceiling_tiles", () -> new WhitePinHoleCeilingTilesMatItem(new Item.Properties()));

    public static final RegistryObject<Item> GENERIC_WALLPAPER = ITEMS.register("materials/wallpaper/generic_wallpaper", () -> new GenericWallpaperMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LOBBY_WALLPAPER = ITEMS.register("materials/wallpaper/lobby_wallpaper", () -> new LobbyWallpaperMatItem(new Item.Properties()));

    public static final RegistryObject<Item> BARBED_PLYWOOD = ITEMS.register("materials/wood/barbed_plywood", () -> new BarbedPlywoodMatItem(new Item.Properties()));
    public static final RegistryObject<Item> CHECKERED_PAINTED_WOODEN_TILES = ITEMS.register("materials/wood/checkered_painted_wooden_tiles", () -> new CheckeredPaintedWoodenTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> LARGE_PAINTED_WOODEN_TILES = ITEMS.register("materials/wood/large_painted_wooden_tiles", () -> new LargePaintedWoodenTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> OLD_PLYWOOD = ITEMS.register("materials/wood/old_plywood", () -> new OldPlywoodMatItem(new Item.Properties()));
    public static final RegistryObject<Item> PAINTED_WOODEN_TILES = ITEMS.register("materials/wood/painted_wooden_tiles", () -> new PaintedWoodenTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> SMALL_PAINTED_WOODEN_TILES = ITEMS.register("materials/wood/small_painted_wooden_tiles", () -> new SmallPaintedWoodenTilesMatItem(new Item.Properties()));
    public static final RegistryObject<Item> TARGET_PAINTED_WOODEN_TILES = ITEMS.register("materials/wood/target_painted_wooden_tiles", () -> new TargetPaintedWoodenTilesMatItem(new Item.Properties()));



    private ModItems() {}

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
