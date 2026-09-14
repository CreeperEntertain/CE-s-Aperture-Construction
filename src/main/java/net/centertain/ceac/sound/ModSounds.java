package net.centertain.ceac.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final RegistryObject<SoundEvent> DECAL_PLACEMENT = register("decal_placement");
    public static final RegistryObject<SoundEvent> DECAL_DELETION = register("decal_deletion");
    public static final RegistryObject<SoundEvent> DECAL_CHANGE_GRID_SIZE = register("decal_change_grid_size");
    public static final RegistryObject<SoundEvent> DECAL_DEPTH_DECREASE = register("decal_depth_decrease");
    public static final RegistryObject<SoundEvent> DECAL_DEPTH_INCREASE = register("decal_depth_increase");
    public static final RegistryObject<SoundEvent> DECAL_MOVE_NORMAL = register("decal_move_normal");
    public static final RegistryObject<SoundEvent> DECAL_MOVE_PLANAR = register("decal_move_planar");
    public static final RegistryObject<SoundEvent> DECAL_PLACEMENT_ERROR = register("decal_placement_error");
    public static final RegistryObject<SoundEvent> DECAL_ROTATE = register("decal_rotate");
    public static final RegistryObject<SoundEvent> DECAL_TILT = register("decal_tilt");

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

    private ModSounds() {}
}
