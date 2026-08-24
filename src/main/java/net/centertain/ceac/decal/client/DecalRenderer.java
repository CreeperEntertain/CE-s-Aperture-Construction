package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Collection;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class DecalRenderer {
    private static final double VOLUME_SIZE = 1.1;

    private static RenderTarget opaqueDepthTarget;

    private DecalRenderer() {}

    public static void captureOpaqueDepth() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        int width = mainTarget.width;
        int height = mainTarget.height;

        if (opaqueDepthTarget == null) {
            opaqueDepthTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            opaqueDepthTarget.resize(width, height, true);
        } else if (opaqueDepthTarget.width != width ||
                opaqueDepthTarget.height != height) {
            opaqueDepthTarget.resize(width, height, true);
        }
        opaqueDepthTarget.copyDepthFrom(mainTarget);
    }

    public static int getOpaqueDepthTexture() {
        return opaqueDepthTarget != null
                ? opaqueDepthTarget.getDepthTextureId()
                : 0;
    }

    public static void render(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        if (ClientDecals.getAll().isEmpty())
            return;
        if (opaqueDepthTarget == null)
            return;

        Camera camera = event.getCamera();
        Vec3 cameraPosition = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        ShaderInstance shader = DecalShaders.getInstance();
        if (shader == null)
            return;

        ResourceLocation decalTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/decal/test.png");
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        int depthTexture = opaqueDepthTarget.getDepthTextureId();

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
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        for (Decal decal : ClientDecals.getAll().values()) {
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
            renderVolume(
                    decal,
                    cameraPosition,
                    poseStack,
                    buffer,
                    shader
            );
            BufferUploader.drawWithShader(buffer.end());
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        lightTexture.turnOffLightLayer();
    }

    public static void renderKBuffer(
            Camera camera
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        if (ClientDecals.getAll().isEmpty())
            return;
        ShaderInstance shader = DecalShaders.getDecalKBufferCapture();
        if (shader == null)
            return;

        TranslucentKBuffer.bind();
        TranslucentKBuffer.setShaderUniforms(shader);

        Matrix4f invProj = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

        var invProjMatUniform = shader.getUniform("InvProjMat");

        if (invProjMatUniform != null)
            invProjMatUniform.set(invProj);

        Matrix3f viewRotation = new Matrix3f().rotate(camera.rotation());
        Matrix3f inverseViewRotation = new Matrix3f(viewRotation).invert();

        var iViewRotMatUniform = shader.getUniform("IViewRotMat");

        if (iViewRotMatUniform != null)
            iViewRotMatUniform.set(inverseViewRotation);

        Collection<Decal> decals = ClientDecals.getAll().values();

        TranslucentKBuffer.uploadDecals(decals, camera.getPosition());

        var decalCountUniform = shader.getUniform("DecalCount");

        if (decalCountUniform != null)
            decalCountUniform.set((float) decals.size());

        RenderSystem.setShader(() -> shader);

        RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/decal/test.png"));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        buffer.vertex(-1.0, -1.0, 0.0).endVertex();
        buffer.vertex( 3.0, -1.0, 0.0).endVertex();
        buffer.vertex(-1.0,  3.0, 0.0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void renderVolume(
            Decal decal,
            Vec3 cameraPosition,
            PoseStack poseStack,
            BufferBuilder buffer,
            ShaderInstance shader
    ) {
        Vec3 origin = decal.getOrigin();

        float x = (float) (origin.x - cameraPosition.x);
        float y = (float) (origin.y - cameraPosition.y);
        float z = (float) (origin.z - cameraPosition.z);

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

        vertex(buffer, matrix, -half, +half, -half, vertexShade);
        vertex(buffer, matrix, +half, +half, -half, vertexShade);
        vertex(buffer, matrix, +half, -half, -half, vertexShade);
        vertex(buffer, matrix, -half, -half, -half, vertexShade);

        vertex(buffer, matrix, +half, +half, +half, vertexShade);
        vertex(buffer, matrix, -half, +half, +half, vertexShade);
        vertex(buffer, matrix, -half, -half, +half, vertexShade);
        vertex(buffer, matrix, +half, -half, +half, vertexShade);

        vertex(buffer, matrix, -half, +half, +half, vertexShade);
        vertex(buffer, matrix, -half, +half, -half, vertexShade);
        vertex(buffer, matrix, -half, -half, -half, vertexShade);
        vertex(buffer, matrix, -half, -half, +half, vertexShade);

        vertex(buffer, matrix, +half, +half, -half, vertexShade);
        vertex(buffer, matrix, +half, +half, +half, vertexShade);
        vertex(buffer, matrix, +half, -half, +half, vertexShade);
        vertex(buffer, matrix, +half, -half, -half, vertexShade);

        vertex(buffer, matrix, -half, +half, +half, vertexShade);
        vertex(buffer, matrix, +half, +half, +half, vertexShade);
        vertex(buffer, matrix, +half, +half, -half, vertexShade);
        vertex(buffer, matrix, -half, +half, -half, vertexShade);

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