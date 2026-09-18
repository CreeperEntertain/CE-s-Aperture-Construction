package net.centertain.ceac.phys_screen.utility;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.client.render.CeacRenderTypes;
import net.centertain.ceac.phys_screen.framework.GameRendererViewOffsetAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public final class PhysRenderHelper {
    private static Matrix4f projectionMatrix = new Matrix4f();
    private static PoseStack cleanPoseStack = new PoseStack();

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
    public static PoseStack getCleanPoseStack() {
        return cleanPoseStack;
    }

    private PhysRenderHelper() {}


    public static void prepareAfterLevelRender() {
        Matrix4f newProjectionMatrix = getCleanAfterLevelMatrix();

        PoseStack newCleanPoseStack = new PoseStack();
        newCleanPoseStack.last().pose().set(newProjectionMatrix);

        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(
                newProjectionMatrix,
                RenderSystem.getVertexSorting()
        );

        projectionMatrix = newProjectionMatrix;
        cleanPoseStack = newCleanPoseStack;
    }
    private static Matrix4f getCleanAfterLevelMatrix() {
        GameRendererViewOffsetAccessor renderer = (GameRendererViewOffsetAccessor) Minecraft.getInstance().gameRenderer;
        return renderer.ceac$getProjectionBeforeViewOffset();
    }

    public static void finishAfterLevelRender(MultiBufferSource.BufferSource bufferSource) {
        bufferSource.endBatch(CeacRenderTypes.IN_WORLD_UI);
        RenderSystem.restoreProjectionMatrix();
    }
}
