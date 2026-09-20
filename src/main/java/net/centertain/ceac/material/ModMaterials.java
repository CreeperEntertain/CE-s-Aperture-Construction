package net.centertain.ceac.material;

import net.centertain.ceac.material.materials.ExampleMaterial;
import net.centertain.ceac.material.materials.ObservationConcreteWallMaterial;
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


    public static final RegistryObject<Material> EXAMPLE =
            MATERIALS.register("example", ExampleMaterial::new);
    public static final RegistryObject<Material> OBSERVATION_CONCRETE_WALL =
            MATERIALS.register("observation_concrete_wall", ObservationConcreteWallMaterial::new);


    private ModMaterials() {}

    public static void register(IEventBus eventBus) {
        MATERIALS.register(eventBus);
    }
}
