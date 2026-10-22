package net.centertain.ceac.prop.framework.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.Objects;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class PropCollisionHatching {
    public static final RenderType CAPTURE_TYPE = RenderType.create(
            "ceac_prop_collision_capture",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.TRIANGLES,
            65536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            PropCollisionShaders::getCapture
                    ))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
                            "always", GL11.GL_ALWAYS
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
                            false, false
                    ))
                    .createCompositeState(false)
    );

    public static final RenderType CLIP_CAPTURE_TYPE = RenderType.create(
            "ceac_prop_collision_clip_capture",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.TRIANGLES,
            65536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            PropCollisionShaders::getClipCapture
                    ))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
                            "always", GL11.GL_ALWAYS
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
                            false, false
                    ))
                    .createCompositeState(false)
    );

    public static final RenderType DEPTH_TYPE = RenderType.create(
            "ceac_prop_collision_depth",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.TRIANGLES,
            65536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            PropCollisionShaders::getDepth
                    ))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
                            "lequal", GL11.GL_LEQUAL
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
                            false, true
                    ))
                    .createCompositeState(false)
    );

    public static final RenderType PROP_VOLUME_CAPTURE_TYPE = RenderType.create(
            "ceac_prop_volume_capture",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.TRIANGLES,
            65536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            PropCollisionShaders::getVolumeCapture
                    ))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
                            "always", GL11.GL_ALWAYS
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(false, false))
                    .createCompositeState(false)
    );

    public static final RenderType PROP_VOLUME_ACCUMULATE_TYPE = RenderType.create(
            "ceac_prop_volume_accumulate",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.TRIANGLES,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            PropCollisionShaders::getVolumeAccumulate
                    ))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
                            "always", GL11.GL_ALWAYS
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(false, false))
                    .createCompositeState(false)
    );

    private static RenderTarget worldDepthTarget;
    private static RenderTarget visibleDepthTarget;
    private static RenderTarget propDepthTarget;

    private static MaskTarget propParityMask;
    private static MaskTarget propInsideMask;

    private static boolean worldDepthReady;

    private static int crossingsTexture;
    private static int crossingsFramebuffer;
    private static int targetWidth;
    private static int targetHeight;
    private static int clipMaskTexture;
    private static int clipMaskFramebuffer;

    private PropCollisionHatching() {}


    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            worldDepthReady = false;
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS)
            return;
        if (!PropPreviewer.isPreviewActive())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();

        ensureTargets(main.width, main.height);

        worldDepthTarget.copyDepthFrom(main);
        main.bindWrite(false);
        worldDepthReady = true;
    }

    public static boolean begin() {
        ShaderInstance capture = PropCollisionShaders.getCapture();
        if (
                capture == null ||
                PropCollisionShaders.getClipCapture() == null ||
                PropCollisionShaders.getResolve() == null ||
                PropCollisionShaders.getVolumeCapture() == null ||
                PropCollisionShaders.getVolumeAccumulate() == null ||
                !worldDepthReady
        )
            return false;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();

        ensureTargets(main.width, main.height);

        visibleDepthTarget.copyDepthFrom(main);
        main.bindWrite(false);

        clearCrossings();
        clearClipMask();

        GL42.glBindImageTexture(0, crossingsTexture, 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);
        GL42.glBindImageTexture(1, clipMaskTexture, 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);

        capture.setSampler("SceneDepth", visibleDepthTarget.getDepthTextureId());

        return true;
    }

    public static void finishAndRender() {
        if (
                crossingsTexture == 0 ||
                clipMaskTexture == 0 ||
                worldDepthTarget == null ||
                visibleDepthTarget == null ||
                propDepthTarget == null
        )
            return;

        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL42.GL_TEXTURE_FETCH_BARRIER_BIT);
        GL42.glBindImageTexture(0, 0, 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_R32UI);
        GL42.glBindImageTexture(1, 0, 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_R32UI);
        GL42.glBindImageTexture(2, 0, 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_R32UI);
        GL42.glBindImageTexture(3, 0, 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_R32UI);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        ShaderInstance shader = PropCollisionShaders.getResolve();
        if (shader == null) {
            main.bindWrite(false);
            worldDepthReady = false;
            return;
        }

        main.bindWrite(false);

        int sceneDepth = visibleDepthTarget.getDepthTextureId();
        int visibleDepth = visibleDepthTarget.getDepthTextureId();
        int propDepth = propDepthTarget.getDepthTextureId();
        int clipMask = clipMaskTexture;
        int propInside = propInsideMask.texture();

        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, sceneDepth);
        RenderSystem.setShaderTexture(1, visibleDepth);
        RenderSystem.setShaderTexture(2, crossingsTexture);
        RenderSystem.setShaderTexture(3, propDepth);
        RenderSystem.setShaderTexture(4, clipMask);
        RenderSystem.setShaderTexture(5, propInside);

        shader.setSampler("SceneDepth", sceneDepth);
        shader.setSampler("VisibleDepth", visibleDepth);
        shader.setSampler("Crossings", crossingsTexture);
        shader.setSampler("PropDepth", propDepth);
        shader.setSampler("ClipMask", clipMask);
        shader.setSampler("PropInsideMask", propInside);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        shader.apply();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);
        buffer.vertex(-1.0, -1.0, 0.0).endVertex();
        buffer.vertex( 3.0, -1.0, 0.0).endVertex();
        buffer.vertex(-1.0,  3.0, 0.0).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        shader.clear();

        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        main.bindWrite(false);

        worldDepthReady = false;
    }

    private static void ensureTargets(int width, int height) {
        if (worldDepthTarget == null) {
            worldDepthTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            worldDepthTarget.resize(width, height, Minecraft.ON_OSX);
        } else if (worldDepthTarget.width != width || worldDepthTarget.height != height)
            worldDepthTarget.resize(width, height, Minecraft.ON_OSX);
        if (visibleDepthTarget == null) {
            visibleDepthTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            visibleDepthTarget.resize(width, height, Minecraft.ON_OSX);
        } else if (visibleDepthTarget.width != width || visibleDepthTarget.height != height)
            visibleDepthTarget.resize(width, height, Minecraft.ON_OSX);
        if (propDepthTarget == null) {
            propDepthTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            propDepthTarget.resize(width, height, Minecraft.ON_OSX);
        } else if (propDepthTarget.width != width || propDepthTarget.height != height)
            propDepthTarget.resize(width, height, Minecraft.ON_OSX);
        if (
                crossingsTexture != 0 &&
                clipMaskTexture != 0 &&
                propParityMask != null &&
                propInsideMask != null &&
                targetWidth == width &&
                targetHeight == height
        )
            return;

        destroyMasks();

        targetWidth = width;
        targetHeight = height;

        crossingsTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, crossingsTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL30.GL_R32UI,
                width,
                height,
                0,
                GL30.GL_RED_INTEGER,
                GL11.GL_UNSIGNED_INT,
                (ByteBuffer) null
        );
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        crossingsFramebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, crossingsFramebuffer);
        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D,
                crossingsTexture,
                0
        );
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            destroyMasks();
            throw new IllegalStateException("Prop crossing framebuffer incomplete: 0x" + Integer.toHexString(status));
        }

        clipMaskTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, clipMaskTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL30.GL_R32UI,
                width,
                height,
                0,
                GL30.GL_RED_INTEGER,
                GL11.GL_UNSIGNED_INT,
                (ByteBuffer) null
        );
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        clipMaskFramebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, clipMaskFramebuffer);
        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D,
                clipMaskTexture,
                0
        );
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        int clipStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (clipStatus != GL30.GL_FRAMEBUFFER_COMPLETE) {
            destroyMasks();
            throw new IllegalStateException("Prop clip framebuffer incomplete: 0x" + Integer.toHexString(clipStatus));
        }

        propParityMask = createIntegerMask(width, height, "prop parity");
        propInsideMask = createIntegerMask(width, height, "prop inside");
    }

    private static void clearCrossings() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, crossingsFramebuffer);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glClearBufferuiv(GL30.GL_COLOR, 0, stack.ints(0, 0, 0, 0));
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL42.glMemoryBarrier(GL42.GL_FRAMEBUFFER_BARRIER_BIT | GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    private static void clearClipMask() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, clipMaskFramebuffer);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glClearBufferuiv(GL30.GL_COLOR, 0, stack.ints(0, 0, 0, 0));
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL42.glMemoryBarrier(GL42.GL_FRAMEBUFFER_BARRIER_BIT | GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    private static void clearIntegerMask(MaskTarget mask) {
        if (mask == propParityMask)
            GL42.glBindImageTexture(2, 0, 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);
        if (mask == propInsideMask)
            GL42.glBindImageTexture(3, 0, 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mask.framebuffer());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glClearBufferuiv(GL30.GL_COLOR, 0, stack.ints(0, 0, 0, 0));
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        GL42.glMemoryBarrier(GL42.GL_FRAMEBUFFER_BARRIER_BIT | GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static boolean beginPropVolumeCapture() {
        if (propParityMask == null || propInsideMask == null || propDepthTarget == null)
            return false;

        clearIntegerMask(propInsideMask);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        return true;
    }

    public static void beginPropParityBatch() {
        clearIntegerMask(propParityMask);

        GL42.glBindImageTexture(2, propParityMask.texture(), 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);
        GL42.glBindImageTexture(3, propInsideMask.texture(), 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static void setPropVolumeBit(int bit) {
        ShaderInstance shader = PropCollisionShaders.getVolumeCapture();
        if (shader == null || propDepthTarget == null)
            return;

        shader.setSampler("PropDepth", propDepthTarget.getDepthTextureId());
        Objects.requireNonNull(shader.getUniform("PropBit")).set(bit);
    }

    public static void accumulatePropParityBatch(
            net.minecraft.client.renderer.MultiBufferSource.BufferSource buffers
    ) {
        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL42.GL_TEXTURE_FETCH_BARRIER_BIT);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

        GL42.glBindImageTexture(2, propParityMask.texture(), 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_R32UI);
        GL42.glBindImageTexture(3, propInsideMask.texture(), 0, false, 0, GL15.GL_READ_WRITE, GL30.GL_R32UI);

        VertexConsumer consumer = buffers.getBuffer(PROP_VOLUME_ACCUMULATE_TYPE);
        consumer.vertex(-1.0, -1.0, 0.0).endVertex();
        consumer.vertex( 3.0, -1.0, 0.0).endVertex();
        consumer.vertex(-1.0,  3.0, 0.0).endVertex();
        buffers.endBatch(PROP_VOLUME_ACCUMULATE_TYPE);

        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL42.GL_TEXTURE_FETCH_BARRIER_BIT);
    }

    private static void destroyMasks() {
        if (crossingsFramebuffer != 0) {
            GL30.glDeleteFramebuffers(crossingsFramebuffer);
            crossingsFramebuffer = 0;
        }
        if (crossingsTexture != 0) {
            GL11.glDeleteTextures(crossingsTexture);
            crossingsTexture = 0;
        }

        if (clipMaskFramebuffer != 0) {
            GL30.glDeleteFramebuffers(clipMaskFramebuffer);
            clipMaskFramebuffer = 0;
        }
        if (clipMaskTexture != 0) {
            GL11.glDeleteTextures(clipMaskTexture);
            clipMaskTexture = 0;
        }

        if (propParityMask != null) {
            GL30.glDeleteFramebuffers(propParityMask.framebuffer());
            GL11.glDeleteTextures(propParityMask.texture());
            propParityMask = null;
        }
        if (propInsideMask != null) {
            GL30.glDeleteFramebuffers(propInsideMask.framebuffer());
            GL11.glDeleteTextures(propInsideMask.texture());
            propInsideMask = null;
        }
    }

    public static void beginPropDepthCapture() {
        if (propDepthTarget == null)
            return;
        propDepthTarget.clear(Minecraft.ON_OSX);
        propDepthTarget.bindWrite(true);
    }

    private static MaskTarget createIntegerMask(int width, int height, String name) {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL30.GL_R32UI,
                width, height, 0,
                GL30.GL_RED_INTEGER, GL11.GL_UNSIGNED_INT, (ByteBuffer) null
        );
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        int framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            GL30.glDeleteFramebuffers(framebuffer);
            GL11.glDeleteTextures(texture);
            throw new IllegalStateException("Prop " + name + " framebuffer incomplete: 0x" + Integer.toHexString(status));
        }

        return new MaskTarget(texture, framebuffer);
    }


    private record MaskTarget(int texture, int framebuffer) {}
}
