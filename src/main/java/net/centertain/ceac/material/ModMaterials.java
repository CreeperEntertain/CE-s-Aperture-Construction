package net.centertain.ceac.material;

import net.centertain.ceac.material.materials.backpanel.*;
import net.centertain.ceac.material.materials.concrete.*;
import net.centertain.ceac.material.materials.metal.*;
import net.centertain.ceac.material.materials.plaster.*;
import net.centertain.ceac.material.materials.plastic.BeigePlasticWallMaterial;
import net.centertain.ceac.material.materials.plastic.BeigeReinforcedPlasticWallMaterial;
import net.centertain.ceac.material.materials.plastic.ReinforcedPlasticWallMaterial;
import net.centertain.ceac.material.materials.plastic.YellowReinforcedPlasticWallMaterial;
import net.centertain.ceac.material.materials.tile.*;
import net.centertain.ceac.material.materials.wallpaper.GenericWallpaperMaterial;
import net.centertain.ceac.material.materials.wallpaper.LobbyWallpaperMaterial;
import net.centertain.ceac.material.materials.wood.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModMaterials {
    public static final DeferredRegister<Material> MATERIALS =
            DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(MOD_ID, "materials"), MOD_ID);

    public static final Supplier<IForgeRegistry<Material>> REGISTRY =
            MATERIALS.makeRegistry(RegistryBuilder::new);



    public static final RegistryObject<Material> BARE_BACKPANEL = MATERIALS.register("bare_backpanel", BareBackpanelMaterial::new);
    public static final RegistryObject<Material> BARE_BACKPANEL_LARGE = MATERIALS.register("bare_backpanel_large", BareBackpanelLargeMaterial::new);
    public static final RegistryObject<Material> GREEN_BACKPANEL = MATERIALS.register("green_backpanel", GreenBackpanelMaterial::new);
    public static final RegistryObject<Material> GREEN_BACKPANEL_LARGE = MATERIALS.register("green_backpanel_large", GreenBackpanelLargeMaterial::new);
    public static final RegistryObject<Material> ORANGE_BACKPANEL = MATERIALS.register("orange_backpanel", OrangeBackpanelMaterial::new);
    public static final RegistryObject<Material> ORANGE_BACKPANEL_LARGE = MATERIALS.register("orange_backpanel_large", OrangeBackpanelLargeMaterial::new);
    public static final RegistryObject<Material> RUSTY_BACKPANEL = MATERIALS.register("rusty_backpanel", RustyBackpanelMaterial::new);
    public static final RegistryObject<Material> RUSTY_BACKPANEL_LARGE = MATERIALS.register("rusty_backpanel_large", RustyBackpanelLargeMaterial::new);
    public static final RegistryObject<Material> WEATHERED_BACKPANEL = MATERIALS.register("weathered_backpanel", WeatheredBackpanelMaterial::new);
    public static final RegistryObject<Material> WEATHERED_BACKPANEL_LARGE = MATERIALS.register("weathered_backpanel_large", WeatheredBackpanelLargeMaterial::new);
    public static final RegistryObject<Material> WHITE_BACKPANEL = MATERIALS.register("white_backpanel", WhiteBackpanelMaterial::new);
    public static final RegistryObject<Material> WHITE_BACKPANEL_LARGE = MATERIALS.register("white_backpanel_large", WhiteBackpanelLargeMaterial::new);
    public static final RegistryObject<Material> YELLOW_BACKPANEL = MATERIALS.register("yellow_backpanel", YellowBackpanelMaterial::new);
    public static final RegistryObject<Material> YELLOW_BACKPANEL_LARGE = MATERIALS.register("yellow_backpanel_large", YellowBackpanelLargeMaterial::new);

    public static final RegistryObject<Material> BARE_CONCRETE_CAST = MATERIALS.register("bare_concrete_cast", BareConcreteCastMaterial::new);
    public static final RegistryObject<Material> CHECKERED_CONCRETE_FLOOR_TILES = MATERIALS.register("checkered_concrete_floor_tiles", CheckeredConcreteFloorTilesMaterial::new);
    public static final RegistryObject<Material> CINDERBLOCKS = MATERIALS.register("cinderblocks", CinderblocksMaterial::new);
    public static final RegistryObject<Material> DARK_CONCRETE_FLOOR_TILES = MATERIALS.register("dark_concrete_floor_tiles", DarkConcreteFloorTilesMaterial::new);
    public static final RegistryObject<Material> DIRTY_CONCRETE_FLOOR = MATERIALS.register("dirty_concrete_floor", DirtyConcreteFloorMaterial::new);
    public static final RegistryObject<Material> DIRTY_CONCRETE_FLOOR_TILES = MATERIALS.register("dirty_concrete_floor_tiles", DirtyConcreteFloorTilesMaterial::new);
    public static final RegistryObject<Material> DIRTY_REBAR_CONCRETE_BLOCKS = MATERIALS.register("dirty_rebar_concrete_blocks", DirtyRebarConcreteBlocksMaterial::new);
    public static final RegistryObject<Material> FACTORY_CONCRETE_WALL = MATERIALS.register("factory_concrete_wall", FactoryConcreteWallMaterial::new);
    public static final RegistryObject<Material> GLUED_CONCRETE_PANELS = MATERIALS.register("glued_concrete_panels", GluedConcretePanelsMaterial::new);
    public static final RegistryObject<Material> HEAVY_CONCRETE_CEILING = MATERIALS.register("heavy_concrete_ceiling", HeavyConcreteCeilingMaterial::new);
    public static final RegistryObject<Material> LIGHT_CONCRETE_FLOOR_TILES = MATERIALS.register("light_concrete_floor_tiles", LightConcreteFloorTilesMaterial::new);
    public static final RegistryObject<Material> MODULAR_CONCRETE = MATERIALS.register("modular_concrete", ModularConcreteMaterial::new);
    public static final RegistryObject<Material> OBSERVATION_CONCRETE_WALL = MATERIALS.register("observation_concrete_wall", ObservationConcreteWallMaterial::new);
    public static final RegistryObject<Material> PAINTED_CONCRETE_BLOCK_PILLAR = MATERIALS.register("painted_concrete_block_pillar", PaintedConcreteBlockPillarMaterial::new);
    public static final RegistryObject<Material> PAINTED_CONCRETE_TILES = MATERIALS.register("painted_concrete_tiles", PaintedConcreteTilesMaterial::new);
    public static final RegistryObject<Material> PAINTED_FACTORY_CONCRETE_WALL = MATERIALS.register("painted_factory_concrete_wall", PaintedFactoryConcreteWallMaterial::new);
    public static final RegistryObject<Material> REBAR_CONCRETE_BLOCKS = MATERIALS.register("rebar_concrete_blocks", RebarConcreteBlocksMaterial::new);
    public static final RegistryObject<Material> REBAR_CONCRETE = MATERIALS.register("rebar_concrete", RebarConcreteMaterial::new);
    public static final RegistryObject<Material> RUSTED_REBAR_CONCRETE = MATERIALS.register("rusted_rebar_concrete", RustedRebarConcreteMaterial::new);
    public static final RegistryObject<Material> SMALL_DIRTY_CONCRETE_FLOOR_TILES = MATERIALS.register("small_dirty_concrete_floor_tiles", SmallDirtyConcreteFloorTilesMaterial::new);
    public static final RegistryObject<Material> STAINED_HEAVY_CONCRETE_CEILING = MATERIALS.register("stained_heavy_concrete_ceiling", StainedHeavyConcreteCeilingMaterial::new);
    public static final RegistryObject<Material> STAINED_MODULAR_CONCRETE = MATERIALS.register("stained_modular_concrete", StainedModularConcreteMaterial::new);
    public static final RegistryObject<Material> STAINED_REBAR_CONCRETE = MATERIALS.register("stained_rebar_concrete", StainedRebarConcreteMaterial::new);
    public static final RegistryObject<Material> WEATHERED_REBAR_CONCRETE = MATERIALS.register("weathered_rebar_concrete", WeatheredRebarConcreteMaterial::new);
    public static final RegistryObject<Material> WITHERED_MODULAR_CONCRETE = MATERIALS.register("withered_modular_concrete", WitheredModularConcreteMaterial::new);
    public static final RegistryObject<Material> WITHERED_REBAR_CONCRETE_BLOCKS = MATERIALS.register("withered_rebar_concrete_blocks", WitheredRebarConcreteBlocksMaterial::new);

    public static final RegistryObject<Material> BLACK_METAL_TILES = MATERIALS.register("black_metal_tiles", BlackMetalTilesMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X05 = MATERIALS.register("black_metal_panel_05x05", BlackPanel05x05Material::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X05_DIRTY = MATERIALS.register("black_metal_panel_05x05_dirty", BlackPanel05x05DirtyMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X05_WITHERED = MATERIALS.register("black_metal_panel_05x05_withered", BlackPanel05x05WitheredMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X1 = MATERIALS.register("black_metal_panel_05x1", BlackPanel05x1Material::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X1_DIRTY = MATERIALS.register("black_metal_panel_05x1_dirty", BlackPanel05x1DirtyMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X1_WITHERED = MATERIALS.register("black_metal_panel_05x1_withered", BlackPanel05x1WitheredMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X2 = MATERIALS.register("black_metal_panel_05x2", BlackPanel05x2Material::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X2_DIRTY = MATERIALS.register("black_metal_panel_05x2_dirty", BlackPanel05x2DirtyMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_05X2_WITHERED = MATERIALS.register("black_metal_panel_05x2_withered", BlackPanel05x2WitheredMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_1X1 = MATERIALS.register("black_metal_panel_1x1", BlackPanel1x1Material::new);
    public static final RegistryObject<Material> BLACK_PANEL_1X1_DIRTY = MATERIALS.register("black_metal_panel_1x1_dirty", BlackPanel1x1DirtyMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_1X1_WITHERED = MATERIALS.register("black_metal_panel_1x1_withered", BlackPanel1x1WitheredMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_1X2 = MATERIALS.register("black_metal_panel_1x2", BlackPanel1x2Material::new);
    public static final RegistryObject<Material> BLACK_PANEL_1X2_DIRTY = MATERIALS.register("black_metal_panel_1x2_dirty", BlackPanel1x2DirtyMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_1X2_WITHERED = MATERIALS.register("black_metal_panel_1x2_withered", BlackPanel1x2WitheredMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_2X2 = MATERIALS.register("black_metal_panel_2x2", BlackPanel2x2Material::new);
    public static final RegistryObject<Material> BLACK_PANEL_2X2_DIRTY = MATERIALS.register("black_metal_panel_2x2_dirty", BlackPanel2x2DirtyMaterial::new);
    public static final RegistryObject<Material> BLACK_PANEL_2X2_WITHERED = MATERIALS.register("black_metal_panel_2x2_withered", BlackPanel2x2WitheredMaterial::new);
    public static final RegistryObject<Material> BLACK_ROUNDED_METAL_TILES = MATERIALS.register("black_rounded_metal_tiles", BlackRoundedMetalTilesMaterial::new);
    public static final RegistryObject<Material> BLUE_METAL_PANELS = MATERIALS.register("blue_metal_panels", BlueMetalPanelsMaterial::new);
    public static final RegistryObject<Material> BLUE_RECTANGULAR_METAL_PANELS = MATERIALS.register("blue_rectangular_metal_panels", BlueRectangularMetalPanelsMaterial::new);
    public static final RegistryObject<Material> BLUE_SQUARE_METAL_PANELS = MATERIALS.register("blue_square_metal_panels", BlueSquareMetalPanelsMaterial::new);
    public static final RegistryObject<Material> BOLTED_METAL_WALL = MATERIALS.register("bolted_metal_wall", BoltedMetalWallMaterial::new);
    public static final RegistryObject<Material> CABLE_WALL = MATERIALS.register("cable_wall", CableWallMaterial::new);
    public static final RegistryObject<Material> CORRUGATED_METAL = MATERIALS.register("corrugated_metal", CorrugatedMetalMaterial::new);
    public static final RegistryObject<Material> DARK_METAL_FLOOR_TILES = MATERIALS.register("dark_metal_floor_tiles", DarkMetalFloorTilesMaterial::new);
    public static final RegistryObject<Material> DARK_ROUNDED_METAL_TILES = MATERIALS.register("dark_rounded_metal_tiles", DarkRoundedMetalTilesMaterial::new);
    public static final RegistryObject<Material> DARK_RUSTED_METAL_PANELS = MATERIALS.register("dark_rusted_metal_panels", DarkRustedMetalPanelsMaterial::new);
    public static final RegistryObject<Material> DARK_WITHERED_PAINTED_METAL_PANEL = MATERIALS.register("dark_withered_painted_metal_panel", DarkWitheredPaintedMetalPanelMaterial::new);
    public static final RegistryObject<Material> DIRTY_RUSTED_METAL_PANELS = MATERIALS.register("dirty_rusted_metal_panels", DirtyRustedMetalPanelsMaterial::new);
    public static final RegistryObject<Material> FACTORY_CONTAINER = MATERIALS.register("factory_container", FactoryContainerMaterial::new);
    public static final RegistryObject<Material> FACTORY_CONTAINER_SIDE = MATERIALS.register("factory_container_side", FactoryContainerSideMaterial::new);
    public static final RegistryObject<Material> FRAMED_GREEN_METAL_PANELS = MATERIALS.register("framed_green_metal_panels", FramedGreenMetalPanelsMaterials::new);
    public static final RegistryObject<Material> GREEN_METAL_PANELS = MATERIALS.register("green_metal_panels", GreenMetalPanelsMaterials::new);
    public static final RegistryObject<Material> LACQUERED_IRREGULAR_METAL_PANELS = MATERIALS.register("lacquered_irregular_metal_panels", LacqueredIrregularMetalPanelsMaterial::new);
    public static final RegistryObject<Material> LACQUERED_RECTANGULAR_METAL_PANELS = MATERIALS.register("lacquered_rectangular_metal_panels", LacqueredRectangularMetalPanelsMaterial::new);
    public static final RegistryObject<Material> LARGE_BLACK_METAL_TILES = MATERIALS.register("large_black_metal_tiles", LargeBlackMetalTilesMaterial::new);
    public static final RegistryObject<Material> LARGE_BLUE_METAL_PANEL = MATERIALS.register("large_blue_metal_panel", LargeBlueMetalPanelMaterial::new);
    public static final RegistryObject<Material> METAL_BEAM_CONNECTOR = MATERIALS.register("metal_beam_connector", MetalBeamConnectorMaterial::new);
    public static final RegistryObject<Material> METAL_FLOOR_TILES = MATERIALS.register("metal_floor_tiles", MetalFloorTilesMaterial::new);
    public static final RegistryObject<Material> PAINTED_METAL_WALL = MATERIALS.register("painted_metal_wall", PaintedMetalWallMaterial::new);
    public static final RegistryObject<Material> PAINTED_SHEET_METAL = MATERIALS.register("painted_sheet_metal", PaintedSheetMetalMaterial::new);
    public static final RegistryObject<Material> REINFORCED_GREEN_METAL_PANELS = MATERIALS.register("reinforced_green_metal_panels", ReinforcedGreenMetalPanelsMaterial::new);
    public static final RegistryObject<Material> RUSTED_RECTANGULAR_METAL_PANELS = MATERIALS.register("rusted_rectangular_metal_panels", RustedRectangularMetalPanelsMaterial::new);
    public static final RegistryObject<Material> RUSTED_SHEET_METAL = MATERIALS.register("rusted_sheet_metal", RustedSheetMetalMaterial::new);
    public static final RegistryObject<Material> SMALL_BLACK_METAL_TILES = MATERIALS.register("small_black_metal_tiles", SmallBlackMetalTilesMaterial::new);
    public static final RegistryObject<Material> WEATHERED_BLUE_RECTANGULAR_METAL_PANELS = MATERIALS.register("weathered_blue_rectangular_metal_panels", WeatheredBlueRectangularMetalPanelsMaterial::new);
    public static final RegistryObject<Material> WEATHERED_CORRUGATED_METAL = MATERIALS.register("weathered_corrugated_metal", WeatheredCorrugatedMetalMaterial::new);
    public static final RegistryObject<Material> WITHERED_CABLE_WALL = MATERIALS.register("withered_cable_wall", WitheredCableWallMaterial::new);
    public static final RegistryObject<Material> WITHERED_PAINTED_METAL_PANEL = MATERIALS.register("withered_painted_metal_panel", WitheredPaintedMetalPanelMaterial::new);

    public static final RegistryObject<Material> AGED_PLASTER = MATERIALS.register("aged_plaster", AgedPlasterMaterial::new);
    public static final RegistryObject<Material> BARE_PLASTER = MATERIALS.register("bare_plaster", BarePlasterMaterial::new);
    public static final RegistryObject<Material> CLEAN_PLASTER = MATERIALS.register("clean_plaster", CleanPlasterMaterial::new);
    public static final RegistryObject<Material> DARK_AGED_PLASTER = MATERIALS.register("dark_aged_plaster", DarkAgedPlasterMaterial::new);
    public static final RegistryObject<Material> DARK_CLEAN_PLASTER = MATERIALS.register("dark_clean_plaster", DarkCleanPlasterMaterial::new);
    public static final RegistryObject<Material> MIXED_AGED_PLASTER = MATERIALS.register("mixed_aged_plaster", MixedAgedPlasterMaterial::new);
    public static final RegistryObject<Material> MIXED_CLEAN_PLASTER = MATERIALS.register("mixed_clean_plaster", MixedCleanPlasterMaterial::new);

    public static final RegistryObject<Material> BEIGE_PLASTIC_WALL = MATERIALS.register("beige_plastic_wall", BeigePlasticWallMaterial::new);
    public static final RegistryObject<Material> BEIGE_REINFORCED_PLASTIC_WALL = MATERIALS.register("beige_reinforced_plastic_wall", BeigeReinforcedPlasticWallMaterial::new);
    public static final RegistryObject<Material> REINFORCED_PLASTIC_WALL = MATERIALS.register("reinforced_plastic_wall", ReinforcedPlasticWallMaterial::new);
    public static final RegistryObject<Material> YELLOW_REINFORCED_PLASTIC_WALL = MATERIALS.register("yellow_reinforced_plastic_wall", YellowReinforcedPlasticWallMaterial::new);

    public static final RegistryObject<Material> CHECKERED_DARK_CERAMIC_TILES = MATERIALS.register("checkered_dark_ceramic_tiles", CheckeredDarkCeramicTilesMaterial::new);
    public static final RegistryObject<Material> CHECKERED_DARK_CERAMIC_TILES_DIRTY = MATERIALS.register("checkered_dark_ceramic_tiles_dirty", CheckeredDarkCeramicTilesDirtyMaterial::new);
    public static final RegistryObject<Material> CHECKERED_ORANGE_TILES = MATERIALS.register("checkered_orange_tiles", CheckeredOrangeTilesMaterial::new);
    public static final RegistryObject<Material> CHECKERED_WHITE_CERAMIC_TILES = MATERIALS.register("checkered_white_ceramic_tiles", CheckeredWhiteCeramicTilesMaterial::new);
    public static final RegistryObject<Material> DARK_CERAMIC_TILES = MATERIALS.register("dark_ceramic_tiles", DarkCeramicTilesMaterial::new);
    public static final RegistryObject<Material> DARK_CERAMIC_TILES_DIRTY = MATERIALS.register("dark_ceramic_tiles_dirty", DarkCeramicTilesDirtyMaterial::new);
    public static final RegistryObject<Material> DARK_OFFICE_FLOOR_TILES = MATERIALS.register("dark_office_floor_tiles", DarkOfficeFloorTilesMaterial::new);
    public static final RegistryObject<Material> DARK_PAINTED_BRICKS = MATERIALS.register("dark_painted_bricks", DarkPaintedBricksMaterial::new);
    public static final RegistryObject<Material> DARK_PIN_HOLE_CEILING_TILES = MATERIALS.register("dark_pin-hole_ceiling_tiles", DarkPinHoleCeilingTilesMaterial::new);
    public static final RegistryObject<Material> DECAYED_WHITE_PAINTED_BRICKS = MATERIALS.register("decayed_white_painted_bricks", DecayedWhitePaintedBricksMaterial::new);
    public static final RegistryObject<Material> DIRTY_FLOOR_TILES = MATERIALS.register("dirty_floor_tiles", DirtyFloorTilesMaterial::new);
    public static final RegistryObject<Material> ORANGE_TILES = MATERIALS.register("orange_tiles", OrangeTilesMaterial::new);
    public static final RegistryObject<Material> PATTERNED_WHITE_PANELS = MATERIALS.register("patterned_white_panels", PatternedWhitePanelsMaterial::new);
    public static final RegistryObject<Material> WHITE_CERAMIC_TILES = MATERIALS.register("white_ceramic_tiles", WhiteCeramicTilesMaterial::new);
    public static final RegistryObject<Material> WHITE_OFFICE_FLOOR_TILES = MATERIALS.register("white_office_floor_tiles", WhiteOfficeFloorTilesMaterial::new);
    public static final RegistryObject<Material> WHITE_PAINTED_BRICKS = MATERIALS.register("white_painted_bricks", WhitePaintedBricksMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X05 = MATERIALS.register("white_panel_05x05", WhitePanel05x05Material::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X05_DIRTY = MATERIALS.register("white_panel_05x05_dirty", WhitePanel05x05DirtyMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X05_WITHERED = MATERIALS.register("white_panel_05x05_withered", WhitePanel05x05WitheredMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X1 = MATERIALS.register("white_panel_05x1", WhitePanel05x1Material::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X1_DIRTY = MATERIALS.register("white_panel_05x1_dirty", WhitePanel05x1DirtyMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X1_WITHERED = MATERIALS.register("white_panel_05x1_withered", WhitePanel05x1WitheredMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X2 = MATERIALS.register("white_panel_05x2", WhitePanel05x2Material::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X2_DIRTY = MATERIALS.register("white_panel_05x2_dirty", WhitePanel05x2DirtyMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_05X2_WITHERED = MATERIALS.register("white_panel_05x2_withered", WhitePanel05x2WitheredMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X1 = MATERIALS.register("white_panel_1x1", WhitePanel1x1Material::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X1_DIRTY = MATERIALS.register("white_panel_1x1_dirty", WhitePanel1x1DirtyMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X1_WITHERED = MATERIALS.register("white_panel_1x1_withered", WhitePanel1x1WitheredMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X2 = MATERIALS.register("white_panel_1x2", WhitePanel1x2Material::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X2_VARIATION = MATERIALS.register("white_panel_1x2_variation", WhitePanel1x2VariationMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X2_DIRTY = MATERIALS.register("white_panel_1x2_dirty", WhitePanel1x2DirtyMaterial::new);
    public static final RegistryObject<Material> WHITE_PANEL_1X2_WITHERED = MATERIALS.register("white_panel_1x2_withered", WhitePanel1x2WitheredMaterial::new);
    public static final RegistryObject<Material> WHITE_PIN_HOLE_CEILING_TILES = MATERIALS.register("white_pin-hole_ceiling_tiles", WhitePinHoleCeilingTilesMaterial::new);

    public static final RegistryObject<Material> GENERIC_WALLPAPER = MATERIALS.register("generic_wallpaper", GenericWallpaperMaterial::new);
    public static final RegistryObject<Material> LOBBY_WALLPAPER = MATERIALS.register("lobby_wallpaper", LobbyWallpaperMaterial::new);

    public static final RegistryObject<Material> BARBED_PLYWOOD = MATERIALS.register("barbed_plywood", BarbedPlywoodMaterial::new);
    public static final RegistryObject<Material> CHECKERED_PAINTED_WOODEN_TILES = MATERIALS.register("checkered_painted_wooden_tiles", CheckeredPaintedWoodenTilesMaterial::new);
    public static final RegistryObject<Material> LARGE_PAINTED_WOODEN_TILES = MATERIALS.register("large_painted_wooden_tiles", LargePaintedWoodenTilesMaterial::new);
    public static final RegistryObject<Material> OLD_PLYWOOD = MATERIALS.register("old_plywood", OldPlywoodMaterial::new);
    public static final RegistryObject<Material> PAINTED_WOODEN_TILES = MATERIALS.register("painted_wooden_tiles", PaintedWoodenTilesMaterial::new);
    public static final RegistryObject<Material> SMALL_PAINTED_WOODEN_TILES = MATERIALS.register("small_painted_wooden_tiles", SmallPaintedWoodenTilesMaterial::new);
    public static final RegistryObject<Material> TARGET_PAINTED_WOODEN_TILES = MATERIALS.register("target_painted_wooden_tiles", TargetPaintedWoodenTilesMaterial::new);



    private ModMaterials() {}

    public static void register(IEventBus eventBus) {
        MATERIALS.register(eventBus);
    }
}
