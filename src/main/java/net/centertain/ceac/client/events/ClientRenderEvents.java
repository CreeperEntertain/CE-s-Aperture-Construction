package net.centertain.ceac.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.decal.DecalPreviewer;
import net.centertain.ceac.decal.client.DecalPlacement;
import net.centertain.ceac.decal.client.render.DecalRenderer;
import net.centertain.ceac.material.MaterialPlacement;
import net.centertain.ceac.material.MaterialPreviewer;
import net.centertain.ceac.phys_screen.utility.PhysRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class ClientRenderEvents {
    private ClientRenderEvents() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            decalRendering(event, poseStack, bufferSource);

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)
            materialPreviewRendering(event, poseStack, bufferSource);

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL)
            billboardPhysScreenRendering(bufferSource);
    }


    private static void decalRendering(
            @NotNull RenderLevelStageEvent event,
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource
    ) {
        Vec3 cameraPosition = event.getCamera().getPosition();

        DecalPreviewer preview = DecalPlacement.getDecalPreview();
        if (preview != null && DecalPlacement.getPrecisePlacement()) {
            preview.render(poseStack, bufferSource, cameraPosition);
            bufferSource.endBatch(RenderType.lines());
        }

        DecalRenderer.captureOpaqueDepth();
        DecalRenderer.render(event);
    }


    private static void materialPreviewRendering(
            @NotNull RenderLevelStageEvent event,
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource
    ) {
        if (MaterialPlacement.getAdjustOffset())
            MaterialPreviewer.setPreviewVisible(MaterialPreviewer.render(
                    poseStack,
                    bufferSource
            ));
    }


    private static void billboardPhysScreenRendering(MultiBufferSource.BufferSource bufferSource) {
        PhysRenderHelper.prepareAfterLevelRender();
        try { // Place all billboard PhysScreen render entries in here.

            decalHelpScreenRendering(bufferSource);
            materialHelpScreenRendering(bufferSource);

        } finally { // Always revert state, regardless of whether a renderer throws.
            PhysRenderHelper.finishAfterLevelRender(bufferSource);
        }
    }

    private static void decalHelpScreenRendering(MultiBufferSource.BufferSource bufferSource) {
        DecalPreviewer preview = DecalPlacement.getDecalPreview();
        if (!(
                preview != null &&
                DecalPlacement.getPrecisePlacement() &&
                DecalPlacement.getHelpShown()
        ))
            return;
        preview.renderHelpScreen(
                PhysRenderHelper.getCleanPoseStack(),
                bufferSource,
                PhysRenderHelper.getProjectionMatrix()
        );
    }
    private static void materialHelpScreenRendering(MultiBufferSource.BufferSource bufferSource) {
        if (!(
                MaterialPlacement.getAdjustOffset() &&
                MaterialPreviewer.getHelpScreenShown() &&
                MaterialPreviewer.getPreviewVisible()
        ))
            return;
        MaterialPreviewer.setHelpScreenShown(MaterialPreviewer.renderHelpScreen(
                PhysRenderHelper.getCleanPoseStack(),
                bufferSource,
                PhysRenderHelper.getProjectionMatrix()
        ));
    }
}