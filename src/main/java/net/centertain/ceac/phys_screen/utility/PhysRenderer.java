package net.centertain.ceac.phys_screen.utility;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.centertain.ceac.phys_screen.framework.PhysScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public final class PhysRenderer {
    /// It's important to only ever execute this within the <code>AFTER_LEVEL</code> render stage as the transforms will
    /// otherwise be utterly fucked.
    /// @return <code>true</code> if your crosshair is over the <code>PhysScreen</code> provided.
    public static boolean billboardAfterLevel(
            @NotNull PhysScreen physScreen,
            @NotNull Vec3 inWorldAnchor,
            @NotNull Vec3 viewSpaceOffset,
            @NotNull PoseStack poseStack,
            MultiBufferSource bufferSource,
            @NotNull Matrix4f projectionMatrix
    ) {
        return billboardAfterLevel(
                physScreen,
                inWorldAnchor,
                viewSpaceOffset,
                poseStack,
                bufferSource,
                projectionMatrix,
                false,
                3.0
        );
    }

    /// It's important to only ever execute this within the <code>AFTER_LEVEL</code> render stage as the transforms will
    /// otherwise be utterly fucked.
    /// @return <code>true</code> if your crosshair is over the <code>PhysScreen</code> provided.
    public static boolean billboardAfterLevel(
            @NotNull PhysScreen physScreen,
            @NotNull Vec3 inWorldAnchor,
            @NotNull Vec3 viewSpaceOffset,
            @NotNull PoseStack poseStack,
            MultiBufferSource bufferSource,
            @NotNull Matrix4f projectionMatrix,
            boolean stayConstantSize
    ) {
        return billboardAfterLevel(
                physScreen,
                inWorldAnchor,
                viewSpaceOffset,
                poseStack,
                bufferSource,
                projectionMatrix,
                stayConstantSize,
                3.0
        );
    }

    /// It's important to only ever execute this within the <code>AFTER_LEVEL</code> render stage as the transforms will
    /// otherwise be utterly fucked.
    /// @return <code>true</code> if your crosshair is over the <code>PhysScreen</code> provided.
    public static boolean billboardAfterLevel(
            @NotNull PhysScreen physScreen,
            @NotNull Vec3 inWorldAnchor,
            @NotNull Vec3 viewSpaceOffset,
            @NotNull PoseStack poseStack,
            MultiBufferSource bufferSource,
            @NotNull Matrix4f projectionMatrix,
            boolean stayConstantSize,
            double constantSizeScale
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();

        poseStack.pushPose();

        Vector3f projectionScale = projectionMatrix.getScale(new Vector3f());
        poseStack.scale(
                1.0f / projectionScale.x(),
                1.0f / projectionScale.y(),
                1.0f
        );

        Vec3 cameraPosition = camera.getPosition();
        Vec3 relative = inWorldAnchor.subtract(cameraPosition);

        Vector3f cameraLeft = camera.getLeftVector();
        Vector3f cameraUp = camera.getUpVector();
        Vector3f cameraLook = camera.getLookVector();
        Vector3f cameraRight = new Vector3f(
                -cameraLeft.x(),
                -cameraLeft.y(),
                -cameraLeft.z()
        );

        double cameraX =
                relative.x * cameraRight.x()
                + relative.y * cameraRight.y()
                + relative.z * cameraRight.z();
        double cameraY =
                relative.x * cameraUp.x()
                + relative.y * cameraUp.y()
                + relative.z * cameraUp.z();
        double cameraZ =
                relative.x * cameraLook.x()
                + relative.y * cameraLook.y()
                + relative.z * cameraLook.z();

        cameraZ += viewSpaceOffset.z;

        double sizeScale = 1.0;

        if (stayConstantSize)
            sizeScale = cameraZ / constantSizeScale;

        cameraX += viewSpaceOffset.x * sizeScale;
        cameraY += viewSpaceOffset.y * sizeScale;

        double physicalWidth = physScreen.getPhysicalWidth();
        double physicalHeight = physScreen.getPhysicalHeight();

        double worldPerPixel = physicalWidth / physScreen.getScreenWidth();

        physicalWidth *= sizeScale;
        physicalHeight *= sizeScale;
        worldPerPixel *= sizeScale;

        double halfWidth = physicalWidth / 2.0;
        double halfHeight = physicalHeight / 2.0;

        double left = cameraX - halfWidth;
        double top = cameraY + halfHeight;

        double mouseX = -left / worldPerPixel;
        double mouseY = top / worldPerPixel;
        boolean mouseOver = physScreen.updateMouse(mouseX, mouseY);

        poseStack.translate(
                left,
                top,
                cameraZ
        );
        poseStack.scale(
                (float) worldPerPixel,
                (float) -worldPerPixel,
                1.0f
        );

        int previousDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);

        PhysGuiGraphics guiGraphics = new PhysGuiGraphics(
                poseStack,
                bufferSource
        );
        physScreen.renderPhysical(guiGraphics);

        RenderSystem.depthFunc(previousDepthFunc);

        poseStack.popPose();

        return mouseOver;
    }
}
