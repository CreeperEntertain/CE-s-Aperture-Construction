package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.centertain.ceac.decal.client.mixin.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;

public final class TranslucentCapture {
    private static boolean capturing;

    private TranslucentCapture() {}

    public static boolean isCapturing() {
        return capturing;
    }

    public static void prepareShader() {
        ShaderInstance shader = DecalShaders.getInstance();
        if (shader == null)
            return;

        RenderSystem.setShader(() -> shader);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LESS);
    }

    public static void render(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        RenderTarget target = TranslucentCaptureTarget.get();
        if (target == null) {
            System.out.println("No Targets");
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();

        target.bindWrite(true);
        target.clear(true);
        capturing = true;
        try {
            ((LevelRendererAccessor) minecraft.levelRenderer)
                    .ceac$renderChunkLayer(
                            RenderType.translucent(),
                            poseStack,
                            cameraPos.x,
                            cameraPos.y,
                            cameraPos.z,
                            RenderSystem.getProjectionMatrix()
                    );
        } finally {
            capturing = false;
        }
        target.unbindWrite();
        minecraft.getMainRenderTarget().bindWrite(false);
    }
}