package net.centertain.ceac.prop.client;

import net.centertain.ceac.prop.client.render.PropRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class PropModelRegistration {
    private PropModelRegistration() {}

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        Minecraft.getInstance().getResourceManager()
                .listResources("models/prop", location -> location.getPath().endsWith(".json"))
                .keySet()
                .forEach(location -> {
                    String path = location.getPath().substring("models/".length());
                    path = path.substring(0, path.length() - ".json".length());
                    event.register(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), path));
                });
    }

    @SubscribeEvent
    public static void onModelsBaked(ModelEvent.BakingCompleted event) {
        PropRenderer.clearModelCache();
    }
}
