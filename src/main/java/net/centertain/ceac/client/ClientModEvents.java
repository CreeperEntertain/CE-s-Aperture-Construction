package net.centertain.ceac.client;

import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.render.TranslucentRenderTargets;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new PreparableReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(
                    @NotNull PreparationBarrier barrier,
                    @NotNull ResourceManager resourceManager,
                    @NotNull ProfilerFiller preparationProfiler,
                    @NotNull ProfilerFiller applyProfiler,
                    @NotNull Executor backgroundExecutor,
                    @NotNull Executor gameExecutor
            ) {
                return CompletableFuture.completedFuture(null)
                        .thenCompose(barrier::wait)
                        .thenRunAsync(DecalLoader::gatherResourceLocations, gameExecutor);
            }
        });
    }
}
