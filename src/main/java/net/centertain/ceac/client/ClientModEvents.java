package net.centertain.ceac.client;

import net.centertain.ceac.decal.client.render.TranslucentRenderTargets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientModEvents
{
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();

            int width = minecraft.getWindow().getWidth();
            int height = minecraft.getWindow().getHeight();

            TranslucentRenderTargets.init(width, height);
        });
    }
}
