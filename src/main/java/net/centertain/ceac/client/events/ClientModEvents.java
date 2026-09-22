package net.centertain.ceac.client.events;

import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.render.TranslucentRenderTargets;
import net.centertain.ceac.material.MaterialShapeBakedModel;
import net.centertain.ceac.material.loaders.MaterialShapeSlopeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            if (!(block instanceof MaterialShape materialShape))
                continue;

            BlockState defaultState = block.defaultBlockState();
            ModelResourceLocation defaultLocation = BlockModelShaper.stateToModelLocation(defaultState);

            BakedModel canonicalModel = event.getModels().get(defaultLocation);

            if (canonicalModel == null)
                continue;

            materialShape.createFaces(canonicalModel);

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);

                if (!event.getModels().containsKey(location))
                    continue;

                event.getModels().put(location, new MaterialShapeBakedModel(canonicalModel, materialShape));
            }
        }
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_slope", MaterialShapeSlopeLoader.INSTANCE);
    }
}
