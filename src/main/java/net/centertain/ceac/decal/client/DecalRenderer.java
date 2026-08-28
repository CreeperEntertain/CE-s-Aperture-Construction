package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.List;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class DecalRenderer {
    private static final double VOLUME_SIZE = 1.1;

    private static RenderTarget opaqueDepthTarget;
    private static RenderTarget decalCoverageTarget;

    private static int decalVolumeVao;
    private static int decalVolumeVbo;

    private static int decalInstanceVbo;
    private static int decalInstanceCapacity;

    private static boolean decalVolumeInitialized;

    private DecalRenderer() {}

    public static void captureOpaqueDepth() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        int width = mainTarget.width;
        int height = mainTarget.height;

        if (opaqueDepthTarget == null) {
            opaqueDepthTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            opaqueDepthTarget.resize(width, height, true);
        } else if (opaqueDepthTarget.width != width || opaqueDepthTarget.height != height) {
            opaqueDepthTarget.resize(width, height, true);
        }
        opaqueDepthTarget.copyDepthFrom(mainTarget);
    }

    public static int getOpaqueDepthTexture() {
        return opaqueDepthTarget != null
                ? opaqueDepthTarget.getDepthTextureId()
                : 0;
    }

    private static void ensureDecalCoverageTarget() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        int width = mainTarget.width;
        int height = mainTarget.height;

        if (decalCoverageTarget == null) {
            decalCoverageTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            decalCoverageTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            decalCoverageTarget.resize(width, height, true);
        } else if (decalCoverageTarget.width != width || decalCoverageTarget.height != height) {
            decalCoverageTarget.resize(width, height, true);
        }
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
        ShaderInstance coverageShader = DecalShaders.getDecalCoverage();
        ShaderInstance resolveShader = DecalShaders.getDecalOpaqueResolve();

        if (coverageShader == null || resolveShader == null)
            return;

        List<Decal> decals = ClientDecals.getByRenderOrder();

        int decalCount = decals.size();

        ensureDecalVolumeBuffer();
        ensureDecalCoverageTarget();
        uploadDecalInstances(decals, camera.getPosition(), event.getPoseStack().last().pose());

        ResourceLocation decalTexture = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/decal/test.png");
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        Uniform coverageDecalPoseMat = coverageShader.getUniform("DecalPoseMat");

        if (coverageDecalPoseMat != null)
            coverageDecalPoseMat.set(event.getPoseStack().last().pose());

        Uniform coverageProjection = coverageShader.getUniform("ProjMat");

        if (coverageProjection != null)
            coverageProjection.set(RenderSystem.getProjectionMatrix());

        Uniform coveragePass = coverageShader.getUniform("CoveragePass");

        if (coveragePass != null)
            coveragePass.set(1);

        Uniform coverageScreenSize = coverageShader.getUniform("ScreenSize");

        if (coverageScreenSize != null)
            coverageScreenSize.set(
                    (float) minecraft.getWindow().getWidth(),
                    (float) minecraft.getWindow().getHeight(),
                    0.0f,
                    0.0f
            );

        LightTexture lightTexture = minecraft.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        RenderTarget translucentDepthTarget = TranslucentRenderTargets.getTranslucentDepth();
        decalCoverageTarget.copyDepthFrom(translucentDepthTarget);
        decalCoverageTarget.bindWrite(false);
        decalCoverageTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        decalCoverageTarget.clear(false);

        RenderSystem.setShader(() -> coverageShader);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LESS);
        RenderSystem.depthMask(false);

        coverageShader.apply();

        GL30.glBindVertexArray(decalVolumeVao);
        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 36, decalCount);
        GL30.glBindVertexArray(0);

        coverageShader.clear();

        if (coveragePass != null)
            coveragePass.set(0);

        TranslucentKBuffer.uploadDecals(decals);
        TranslucentKBuffer.bind();
        TranslucentKBuffer.setShaderUniforms(resolveShader);

        Uniform cameraPositionUniform = resolveShader.getUniform("CameraPosition");

        if (cameraPositionUniform != null)
            cameraPositionUniform.set(
                    (float) camera.getPosition().x,
                    (float) camera.getPosition().y,
                    (float) camera.getPosition().z
            );

        Matrix4f invProj = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

        Uniform invProjMatUniform = resolveShader.getUniform("InvProjMat");

        if (invProjMatUniform != null)
            invProjMatUniform.set(invProj);

        Matrix3f viewRotation = new Matrix3f().rotate(camera.rotation());
        Matrix3f inverseViewRotation = new Matrix3f(viewRotation).invert();

        Uniform iViewRotMatUniform = resolveShader.getUniform("IViewRotMat");

        if (iViewRotMatUniform != null)
            iViewRotMatUniform.set(inverseViewRotation);

        Uniform decalCountUniform = resolveShader.getUniform("DecalCount");

        if (decalCountUniform != null)
            decalCountUniform.set((float) decals.size());

        Uniform screenSizeUniform = resolveShader.getUniform("ScreenSize");

        if (screenSizeUniform != null)
            screenSizeUniform.set(
                    (float) minecraft.getWindow().getWidth(),
                    (float) minecraft.getWindow().getHeight(),
                    0.0f,
                    0.0f
            );

        mainTarget.bindWrite(false);

        RenderSystem.setShader(() -> resolveShader);
        RenderSystem.setShaderTexture(0, decalTexture);
        RenderSystem.setShaderTexture(1, opaqueDepthTarget.getDepthTextureId());
        RenderSystem.setShaderTexture(2, decalCoverageTarget.getColorTextureId());

        resolveShader.setSampler("Sampler0", RenderSystem.getShaderTexture(0));
        resolveShader.setSampler("Sampler1", RenderSystem.getShaderTexture(1));
        resolveShader.setSampler("Coverage", RenderSystem.getShaderTexture(2));

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        resolveShader.apply();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);
        buffer.vertex(-1.0, -1.0, 0.0).endVertex();
        buffer.vertex(3.0, -1.0, 0.0).endVertex();
        buffer.vertex(-1.0, 3.0, 0.0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        resolveShader.clear();

        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        lightTexture.turnOffLightLayer();
    }

    private static void uploadDecalInstances(
            Collection<Decal> decals,
            Vec3 cameraPosition,
            Matrix4f decalPoseMat
    ) {
        int count = decals.size();

        if (decalInstanceVbo == 0)
            decalInstanceVbo = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, decalInstanceVbo);

        if (decalInstanceCapacity < count) {
            decalInstanceCapacity = Math.max(count, Math.max(decalInstanceCapacity * 2, 64));

            GL15.glBufferData(
                    GL15.GL_ARRAY_BUFFER,
                    (long) decalInstanceCapacity *
                            6L *
                            Float.BYTES,
                    GL15.GL_DYNAMIC_DRAW
            );
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer data = stack.mallocFloat(count * 6);

            for (Decal decal : decals) {
                Vec3 origin = decal.getOrigin();
                Vec3 normal = Vec3.atLowerCornerOf(decal.getNormal().getNormal());

                Vector4f transformed =
                        new Vector4f(
                                (float)(origin.x - cameraPosition.x),
                                (float)(origin.y - cameraPosition.y),
                                (float)(origin.z - cameraPosition.z),
                                1.0f
                        );

                transformed.mul(decalPoseMat);

                data.put(transformed.x());
                data.put(transformed.y());
                data.put(transformed.z());

                data.put((float) normal.x);
                data.put((float) normal.y);
                data.put((float) normal.z);
            }

            data.flip();

            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data);
        }
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void ensureDecalVolumeBuffer() {
        if (decalVolumeInitialized)
            return;

        float[] vertices = getVertices();

        decalVolumeVao = GL30.glGenVertexArrays();
        decalVolumeVbo = GL15.glGenBuffers();
        decalInstanceVbo = GL15.glGenBuffers();
        decalInstanceCapacity = 64;

        GL30.glBindVertexArray(decalVolumeVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, decalVolumeVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0L);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, decalInstanceVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) decalInstanceCapacity * 6L * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0L);
        GL33.glVertexAttribDivisor(1, 1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3L * Float.BYTES);
        GL33.glVertexAttribDivisor(2, 1);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        decalVolumeInitialized = true;
    }

    private static float @NotNull [] getVertices() {
        float half = (float) (VOLUME_SIZE / 2.0);
        return new float[]{
                -half, +half, -half,
                +half, +half, -half,
                +half, -half, -half,
                -half, +half, -half,
                +half, -half, -half,
                -half, -half, -half,

                +half, +half, +half,
                -half, +half, +half,
                -half, -half, +half,
                +half, +half, +half,
                -half, -half, +half,
                +half, -half, +half,

                -half, +half, +half,
                -half, +half, -half,
                -half, -half, -half,
                -half, +half, +half,
                -half, -half, -half,
                -half, -half, +half,

                +half, +half, -half,
                +half, +half, +half,
                +half, -half, +half,
                +half, +half, -half,
                +half, -half, +half,
                +half, -half, -half,

                -half, +half, +half,
                +half, +half, +half,
                +half, +half, -half,
                -half, +half, +half,
                +half, +half, -half,
                -half, +half, -half,

                -half, -half, -half,
                +half, -half, -half,
                +half, -half, +half,
                -half, -half, -half,
                +half, -half, +half,
                -half, -half, +half
        };
    }

    public static void renderKBuffer(
            Camera camera,
            boolean fabulous
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        if (ClientDecals.getAll().isEmpty())
            return;
        ShaderInstance shader = DecalShaders.getDecalKBufferCapture();
        if (shader == null)
            return;

        Collection<Decal> decals = ClientDecals.getAll().values();

        TranslucentKBuffer.uploadDecals(decals);
        TranslucentKBuffer.bind();
        TranslucentKBuffer.setShaderUniforms(shader);

        Uniform cameraPositionUniform = shader.getUniform("CameraPosition");

        if (cameraPositionUniform != null)
            cameraPositionUniform.set(
                    (float) camera.getPosition().x,
                    (float) camera.getPosition().y,
                    (float) camera.getPosition().z
            );

        Matrix4f invProj = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

        Uniform invProjMatUniform = shader.getUniform("InvProjMat");

        if (invProjMatUniform != null)
            invProjMatUniform.set(invProj);

        Matrix3f viewRotation = new Matrix3f().rotate(camera.rotation());
        Matrix3f inverseViewRotation = new Matrix3f(viewRotation).invert();

        Uniform iViewRotMatUniform = shader.getUniform("IViewRotMat");

        if (iViewRotMatUniform != null)
            iViewRotMatUniform.set(inverseViewRotation);

        Uniform decalCountUniform = shader.getUniform("DecalCount");

        if (decalCountUniform != null)
            decalCountUniform.set((float) decals.size());

        RenderSystem.setShader(() -> shader);

        RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/decal/test.png"));
        RenderSystem.setShaderTexture(1, DecalRenderer.getOpaqueDepthTexture());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (fabulous) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);
            RenderSystem.depthMask(true);
        } else {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
        }
        RenderSystem.disableCull();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);

        buffer.vertex(-1.0, -1.0, 0.0).endVertex();
        buffer.vertex(3.0, -1.0, 0.0).endVertex();
        buffer.vertex(-1.0, 3.0, 0.0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}