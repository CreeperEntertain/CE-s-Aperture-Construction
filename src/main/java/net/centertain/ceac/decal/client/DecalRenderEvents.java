package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.decal.DecalPreviewer;
import net.centertain.ceac.decal.client.render.DecalRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class DecalRenderEvents {
    private DecalRenderEvents() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {

            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            Vec3 cameraPosition = event.getCamera().getPosition();

            DecalPreviewer preview = ClientDecals.getDecalPreview();
            if (preview != null) {
                preview.render(poseStack, bufferSource, cameraPosition);
                bufferSource.endBatch(RenderType.lines());
            }

            DecalRenderer.captureOpaqueDepth();
            DecalRenderer.render(event);
        }
    }
}