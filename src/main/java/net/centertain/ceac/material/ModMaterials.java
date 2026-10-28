package net.centertain.ceac.material;

import net.centertain.ceac.material.materials.backpanel.*;
import net.centertain.ceac.material.materials.concrete.*;
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


    private ModMaterials() {}

    public static void register(IEventBus eventBus) {
        MATERIALS.register(eventBus);
    }
}
