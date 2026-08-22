package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.client.mixin.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Objects;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class DecalRenderer {
    private static final double VOLUME_SIZE = 1.1;

    private DecalRenderer() {}

    public static void render(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        if (ClientDecals.getAll().isEmpty())
            return;

        Camera camera = event.getCamera();
        Vec3 cameraPosition = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        ShaderInstance shader = DecalShaders.getInstance();
        if (shader == null)
            return;

        ResourceLocation decalTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/decal/test.png");
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        RenderTarget translucentTarget = TranslucentRenderTargets.getTranslucentDepth();
        if (translucentTarget == null) {
            System.out.println("TranslucentTarget is null.");
            return;
        }

        boolean fabulous = minecraft.options.graphicsMode().get() == GraphicsStatus.FABULOUS;
        int depthTexture;

        if (fabulous) {
            LevelRendererAccessor levelRenderer = ((LevelRendererAccessor) minecraft.levelRenderer);
            depthTexture = levelRenderer.ceac$getTranslucentTarget().getDepthTextureId();
        } else {
            translucentTarget.copyDepthFrom(mainTarget);
            depthTexture = translucentTarget.getDepthTextureId();
        }

        mainTarget.bindWrite(false);

        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, decalTexture);
        RenderSystem.setShaderTexture(1, depthTexture);
        var screenSizeUniform = shader.getUniform("ScreenSize");
        if (screenSizeUniform != null) {
            screenSizeUniform.set(
                    (float) minecraft.getWindow().getWidth(),
                    (float) minecraft.getWindow().getHeight(),
                    0.0f,
                    0.0f
            );
        }

        var invProjMatUniform = shader.getUniform("InvProjMat");
        if (invProjMatUniform != null) {
            Matrix4f invProj = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();
            invProjMatUniform.set(invProj);
        }

        LightTexture lightTexture = minecraft.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (!fabulous) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
        }
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        for (Decal decal : ClientDecals.getAll().values()) {
            buffer.begin(
                    VertexFormat.Mode.QUADS,
                    DefaultVertexFormat.POSITION_COLOR_LIGHTMAP
            );
            renderVolume(
                    decal,
                    cameraPosition,
                    poseStack,
                    buffer
            );
            BufferUploader.drawWithShader(buffer.end());
        }
        if (!fabulous) {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();
    }

    private static void renderVolume(
            Decal decal,
            Vec3 cameraPosition,
            PoseStack poseStack,
            BufferBuilder buffer
    ) {
        Vec3 origin = decal.getOrigin();

        float x = (float) (origin.x - cameraPosition.x);
        float y = (float) (origin.y - cameraPosition.y);
        float z = (float) (origin.z - cameraPosition.z);

        ShaderInstance shader = DecalShaders.getInstance();
        if (shader == null)
            return;

        var decalOriginUniform = shader.getUniform("DecalOriginRelative");
        if (decalOriginUniform != null)
            decalOriginUniform.set(x, y, z);

        Vec3 normal = Vec3.atLowerCornerOf(decal.getNormal().getNormal());

        var decalNormalUniform = shader.getUniform("DecalNormal");
        if (decalNormalUniform != null)
            decalNormalUniform.set(
                    (float) normal.x,
                    (float) normal.y,
                    (float) normal.z
            );

        float half = (float) (VOLUME_SIZE / 2.0);

        Matrix4f matrix = poseStack.last().pose();
        float vertexShade = 1.0f;

        // Front (-Z)
        vertex(buffer, matrix, -half, +half, -half, vertexShade);
        vertex(buffer, matrix, +half, +half, -half, vertexShade);
        vertex(buffer, matrix, +half, -half, -half, vertexShade);
        vertex(buffer, matrix, -half, -half, -half, vertexShade);

        // Back (+Z)
        vertex(buffer, matrix, +half, +half, +half, vertexShade);
        vertex(buffer, matrix, -half, +half, +half, vertexShade);
        vertex(buffer, matrix, -half, -half, +half, vertexShade);
        vertex(buffer, matrix, +half, -half, +half, vertexShade);

        // Left (-X)
        vertex(buffer, matrix, -half, +half, +half, vertexShade);
        vertex(buffer, matrix, -half, +half, -half, vertexShade);
        vertex(buffer, matrix, -half, -half, -half, vertexShade);
        vertex(buffer, matrix, -half, -half, +half, vertexShade);

        // Right (+X)
        vertex(buffer, matrix, +half, +half, -half, vertexShade);
        vertex(buffer, matrix, +half, +half, +half, vertexShade);
        vertex(buffer, matrix, +half, -half, +half, vertexShade);
        vertex(buffer, matrix, +half, -half, -half, vertexShade);

        // Top (+Y)
        vertex(buffer, matrix, -half, +half, +half, vertexShade);
        vertex(buffer, matrix, +half, +half, +half, vertexShade);
        vertex(buffer, matrix, +half, +half, -half, vertexShade);
        vertex(buffer, matrix, -half, +half, -half, vertexShade);

        // Bottom (-Y)
        vertex(buffer, matrix, -half, -half, -half, vertexShade);
        vertex(buffer, matrix, +half, -half, -half, vertexShade);
        vertex(buffer, matrix, +half, -half, +half, vertexShade);
        vertex(buffer, matrix, -half, -half, +half, vertexShade);
    }

    private static void vertex(
            BufferBuilder buffer,
            Matrix4f matrix,
            float x,
            float y,
            float z,
            float shade
    ) {
        shade = Math.max(0.0f, Math.min(1.0f, shade));

        int r = (int) (255.0f * shade);
        int g = (int) (255.0f * shade);
        int b = (int) (255.0f * shade);

        buffer.vertex(matrix, x, y, z)
                .color(r, g, b, 255)
                .uv2(240, 240)
                .endVertex();
    }
}