package net.centertain.ceac.client;

import net.centertain.ceac.decal.client.ClientDecals;
import net.centertain.ceac.decal.client.render.TranslucentRenderTargets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        Minecraft minecraft = Minecraft.getInstance();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        TranslucentRenderTargets.resize(width, height);
    }

    @SubscribeEvent
    public static void onGameShuttingDown(GameShuttingDownEvent event)
    {
        TranslucentRenderTargets.destroy();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientDecals.clear();
    }
}
