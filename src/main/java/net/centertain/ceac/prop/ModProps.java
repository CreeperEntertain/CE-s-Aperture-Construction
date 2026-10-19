package net.centertain.ceac.prop;

import net.centertain.ceac.prop.props.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModProps {
    public static final DeferredRegister<PropDefinition> PROPS =
            DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_definitions"), MOD_ID);

    public static final Supplier<IForgeRegistry<PropDefinition>> REGISTRY =
            PROPS.makeRegistry(RegistryBuilder::new);



    public static final RegistryObject<PropDefinition> TEST_PROP = PROPS.register("test_prop", TestProp::new);



    private ModProps() {}

    public static Optional<PropDefinition> get(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.get().getValue(id));
    }

    public static void register(IEventBus eventBus) {
        PROPS.register(eventBus);
    }
}