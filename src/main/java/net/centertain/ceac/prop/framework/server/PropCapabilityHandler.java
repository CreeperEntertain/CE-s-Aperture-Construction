package net.centertain.ceac.prop.framework.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class PropCapabilityHandler {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "props");

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
        PropCapabilityProvider provider = new PropCapabilityProvider(event.getObject());
        event.addCapability(ID, provider);
        event.addListener(provider::invalidate);
    }

    private PropCapabilityHandler() {}
}
