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
import net.centertain.ceac.decal.client.mixin.LevelRendererAccessor;
import net.centertain.ceac.decal.client.mixin.LightTextureAccessor;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class DecalRenderer {
    private static final double VOLUME_SIZE = 1.1;

    private static RenderTarget opaqueDepthTarget;
    private static RenderTarget decalCoverageTarget;
    private static RenderTarget translucentColorSnapshot;
    private static RenderTarget opaqueLightmapTarget;

    private static int decalVolumeVao;
    private static int decalVolumeVbo;
    private static int decalVolumeEbo;

    private static int opaqueLightmapDepthTexture;
    private static int opaqueLightmapDepthWidth;
    private static int opaqueLightmapDepthHeight;
    private static int opaqueLightmapDepthFramebuffer;

    private static int decalInstanceVbo;
    private static int decalInstanceCapacity;

    private static boolean decalVolumeInitialized;

    private DecalRenderer() {}

    private static boolean cameraInsideAnyDecalVolume(
            Collection<Decal> decals,
            Vec3 cameraPosition
    ) {
        for (Decal decal : decals) {
            Vec3 normal = decal.getNormal();

            Vec3 reference;

            if (Math.abs(normal.y) < 0.999)
                reference = new Vec3(0.0, 1.0, 0.0);
            else
                reference = new Vec3(1.0, 0.0, 0.0);

            Vec3 tangent = reference.cross(normal).normalize();
            Vec3 bitangent = normal.cross(tangent).normalize();

            double angle = Math.toRadians(22.5 * (decal.getRotation() & 0xFF));

            double c = Math.cos(angle);
            double s = Math.sin(angle);

            Vec3 rotatedTangent = tangent.scale(c).add(bitangent.scale(s));

            Vec3 rotatedBitangent = tangent.scale(-s).add(bitangent.scale(c));

            Vec3 r = cameraPosition.subtract(decal.getOrigin());

            double localX = r.dot(rotatedTangent);
            double localY = r.dot(rotatedBitangent);
            double localZ = r.dot(normal);

            double width = decal.getPixelWidth() / 16.0;
            double height = decal.getPixelHeight() / 16.0;
            double depth = decal.getBlockDepth();

            if (Math.abs(localX) <= width / 2.0 && Math.abs(localY) <= height / 2.0 && Math.abs(localZ) <= depth / 2.0)
                return true;
        }

        return false;
    }

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

    private static void ensureOpaqueLightmapDepthTexture(
            int width,
            int height
    ) {
        if (
                opaqueLightmapDepthTexture != 0 &&
                        opaqueLightmapDepthWidth == width &&
                        opaqueLightmapDepthHeight == height
        )
            return;

        if (opaqueLightmapDepthTexture != 0)
            GL11.glDeleteTextures(opaqueLightmapDepthTexture);

        if (opaqueLightmapDepthFramebuffer != 0)
            GL30.glDeleteFramebuffers(opaqueLightmapDepthFramebuffer);

        opaqueLightmapDepthTexture = GL11.glGenTextures();
        opaqueLightmapDepthFramebuffer = GL30.glGenFramebuffers();

        opaqueLightmapDepthWidth = width;
        opaqueLightmapDepthHeight = height;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, opaqueLightmapDepthTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL42.glTexStorage2D(GL11.GL_TEXTURE_2D, 1, GL30.GL_R32UI, width, height);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, opaqueLightmapDepthFramebuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, opaqueLightmapDepthTexture, 0);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    public static int getOpaqueLightmapDepthTexture() {
        return opaqueLightmapDepthTexture;
    }

    public static void captureOpaqueLightmap() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        int width = mainTarget.width;
        int height = mainTarget.height;

        ensureOpaqueLightmapDepthTexture(width, height);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, opaqueLightmapDepthFramebuffer);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer clearValue = stack.mallocInt(4);

            clearValue.put(0, -1);
            clearValue.put(1, 0);
            clearValue.put(2, 0);
            clearValue.put(3, 0);

            GL30.glClearBufferuiv(GL30.GL_COLOR, 0, clearValue);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (opaqueLightmapTarget == null) {
            opaqueLightmapTarget = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            opaqueLightmapTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            opaqueLightmapTarget.resize(width, height, true);
        } else if (opaqueLightmapTarget.width != width || opaqueLightmapTarget.height != height) {
            opaqueLightmapTarget.resize(width, height, true);
        }

        opaqueLightmapTarget.bindWrite(false);
        opaqueLightmapTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        opaqueLightmapTarget.clear(false);

        mainTarget.bindWrite(false);

        int texture = opaqueLightmapTarget.getColorTextureId();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public static int getOpaqueLightmapTexture() {
        return opaqueLightmapTarget != null
                ? opaqueLightmapTarget.getColorTextureId()
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
        DynamicTexture lightmap = ((LightTextureAccessor) lightTexture).ceac$getLightTexture();

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

        boolean cameraInsideVolume = cameraInsideAnyDecalVolume(decals, camera.getPosition());
        if (cameraInsideVolume)
            RenderSystem.disableCull();
        else {
            RenderSystem.enableCull();
            GL11.glCullFace(GL11.GL_BACK);
        }

        coverageShader.apply();

        GL30.glBindVertexArray(decalVolumeVao);
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_BYTE, 0L, decalCount);
        GL30.glBindVertexArray(0);

        coverageShader.clear();

        RenderSystem.disableCull();

        if (coveragePass != null)
            coveragePass.set(0);

        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL42.GL_TEXTURE_FETCH_BARRIER_BIT);

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
        RenderSystem.setShaderTexture(3, getOpaqueLightmapTexture());
        RenderSystem.setShaderTexture(4, lightmap.getId());

        resolveShader.setSampler("Sampler0", RenderSystem.getShaderTexture(0));
        resolveShader.setSampler("Sampler1", RenderSystem.getShaderTexture(1));
        resolveShader.setSampler("Coverage", RenderSystem.getShaderTexture(2));
        resolveShader.setSampler("LightmapCoords", RenderSystem.getShaderTexture(3));
        resolveShader.setSampler("Lightmap", RenderSystem.getShaderTexture(4));

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
        RenderSystem.enableCull();
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
                            10L *
                            Float.BYTES,
                    GL15.GL_DYNAMIC_DRAW
            );
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer data = stack.mallocFloat(count * 10);

            for (Decal decal : decals) {
                Vec3 origin = decal.getOrigin();
                Vec3 normal = decal.getNormal();

                Vector4f transformed =
                        new Vector4f(
                                (float)(origin.x - cameraPosition.x),
                                (float)(origin.y - cameraPosition.y),
                                (float)(origin.z - cameraPosition.z),
                                1.0f
                        );

                transformed.mul(decalPoseMat);

                double width = decal.getPixelWidth() / 16.0;
                double height = decal.getPixelHeight() / 16.0;
                double depth = decal.getBlockDepth();

                data.put(transformed.x());
                data.put(transformed.y());
                data.put(transformed.z());

                data.put((float) normal.x);
                data.put((float) normal.y);
                data.put((float) normal.z);

                data.put((float) width);
                data.put((float) height);
                data.put((float) depth);

                data.put((float) (decal.getRotation() & 0xFF));
            }

            data.flip();

            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data);
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void ensureDecalVolumeBuffer() {
        if (decalVolumeInitialized)
            return;

        float half = 0.5f;

        float[] vertices = {
                -half, +half, -half, // 0
                +half, +half, -half, // 1
                +half, -half, -half, // 2
                -half, -half, -half, // 3

                +half, +half, +half, // 4
                -half, +half, +half, // 5
                -half, -half, +half, // 6
                +half, -half, +half  // 7
        };

        byte[] indices = {
                0, 1, 2,
                0, 2, 3,

                4, 5, 6,
                4, 6, 7,

                5, 0, 3,
                5, 3, 6,

                1, 4, 7,
                1, 7, 2,

                5, 4, 1,
                5, 1, 0,

                3, 2, 7,
                3, 7, 6
        };

        decalVolumeVao = GL30.glGenVertexArrays();
        decalVolumeVbo = GL15.glGenBuffers();
        decalVolumeEbo = GL15.glGenBuffers();
        decalInstanceVbo = GL15.glGenBuffers();
        decalInstanceCapacity = 64;

        GL30.glBindVertexArray(decalVolumeVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, decalVolumeVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0L);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, decalInstanceVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) decalInstanceCapacity * 10L * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 10 * Float.BYTES, 0L);
        GL33.glVertexAttribDivisor(1, 1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 10 * Float.BYTES, 3L * Float.BYTES);
        GL33.glVertexAttribDivisor(2, 1);
        GL20.glEnableVertexAttribArray(3);
        GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 10 * Float.BYTES, 6L * Float.BYTES);
        GL33.glVertexAttribDivisor(3, 1);
        GL20.glEnableVertexAttribArray(4);
        GL20.glVertexAttribPointer(4, 1, GL11.GL_FLOAT, false, 10 * Float.BYTES, 9L * Float.BYTES);
        GL33.glVertexAttribDivisor(4, 1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, decalVolumeEbo);

        ByteBuffer indexBuffer = org.lwjgl.BufferUtils.createByteBuffer(indices.length);

        indexBuffer.put(indices);
        indexBuffer.flip();

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        decalVolumeInitialized = true;
    }

    private static void ensureTranslucentColorSnapshot() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        int width = mainTarget.width;
        int height = mainTarget.height;

        if (translucentColorSnapshot == null) {
            translucentColorSnapshot = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            translucentColorSnapshot.resize(width, height, true);
        } else if (translucentColorSnapshot.width != width || translucentColorSnapshot.height != height)
            translucentColorSnapshot.resize(width, height, true);
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
        ShaderInstance shader = DecalShaders.getDecalKBufferResolve();
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

        Uniform fabulousUniform = shader.getUniform("Fabulous");

        if (fabulousUniform != null)
            fabulousUniform.set(fabulous ? 1 : 0);

        LightTexture lightTexture = minecraft.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        DynamicTexture lightmap = ((LightTextureAccessor) lightTexture).ceac$getLightTexture();

        RenderSystem.setShader(() -> shader);

        RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/decal/test.png"));
        RenderSystem.setShaderTexture(1, DecalRenderer.getOpaqueDepthTexture());
        RenderSystem.setShaderTexture(3, lightmap.getId());

        shader.setSampler("Sampler0", RenderSystem.getShaderTexture(0));
        shader.setSampler("Sampler1", RenderSystem.getShaderTexture(1));
        shader.setSampler("Lightmap", RenderSystem.getShaderTexture(3));

        if (fabulous) {
            RenderTarget translucentTarget = ((LevelRendererAccessor) minecraft.levelRenderer).ceac$getTranslucentTarget();
            if (translucentTarget == null)
                return;

            ensureTranslucentColorSnapshot();

            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, translucentTarget.frameBufferId);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, translucentColorSnapshot.frameBufferId);
            GL30.glBlitFramebuffer(
                    0,
                    0,
                    translucentTarget.width,
                    translucentTarget.height,
                    0,
                    0,
                    translucentColorSnapshot.width,
                    translucentColorSnapshot.height,
                    GL11.GL_COLOR_BUFFER_BIT,
                    GL11.GL_NEAREST
            );

            translucentTarget.bindWrite(false);

            RenderSystem.setShaderTexture(2, translucentColorSnapshot.getColorTextureId());

            shader.setSampler("SurfaceColor", RenderSystem.getShaderTexture(2));
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (fabulous) {
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);
            RenderSystem.depthMask(false);
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