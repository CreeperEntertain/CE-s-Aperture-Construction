package net.centertain.ceac.material;

import net.centertain.ceac.material.materials.backpanel.*;
import net.centertain.ceac.material.materials.concrete.ObservationConcreteWallMaterial;
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

    public static final RegistryObject<Material> OBSERVATION_CONCRETE_WALL = MATERIALS.register("observation_concrete_wall", ObservationConcreteWallMaterial::new);


    private ModMaterials() {}

    public static void register(IEventBus eventBus) {
        MATERIALS.register(eventBus);
    }
}
