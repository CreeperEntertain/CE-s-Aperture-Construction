package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.client.render.CeacRenderTypes;
import net.centertain.ceac.decal.DecalPreviewer;
import net.centertain.ceac.decal.client.render.DecalRenderer;
import net.centertain.ceac.phys_screen.framework.GameRendererViewOffsetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

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
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Vec3 cameraPosition = event.getCamera().getPosition();

            DecalPreviewer preview = DecalPlacement.getDecalPreview();
            if (preview != null && DecalPlacement.getPrecisePlacement()) {
                preview.render(poseStack, bufferSource, cameraPosition);
                bufferSource.endBatch(RenderType.lines());
            }

            DecalRenderer.captureOpaqueDepth();
            DecalRenderer.render(event);
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            DecalPreviewer preview = DecalPlacement.getDecalPreview();

            if (
                    preview != null &&
                    DecalPlacement.getPrecisePlacement() &&
                    DecalPlacement.getHelpShown()
            ) {
                GameRendererViewOffsetAccessor renderer = (GameRendererViewOffsetAccessor) Minecraft.getInstance().gameRenderer;

                Matrix4f projectionMatrix = renderer.ceac$getProjectionBeforeViewOffset();

                if (projectionMatrix != null) {
                    RenderSystem.backupProjectionMatrix();
                    RenderSystem.setProjectionMatrix(projectionMatrix, RenderSystem.getVertexSorting());

                    preview.renderHelpScreen(
                            poseStack,
                            bufferSource,
                            projectionMatrix
                    );

                    bufferSource.endBatch(CeacRenderTypes.IN_WORLD_UI);

                    RenderSystem.restoreProjectionMatrix();
                }
            }
        }
    }
}