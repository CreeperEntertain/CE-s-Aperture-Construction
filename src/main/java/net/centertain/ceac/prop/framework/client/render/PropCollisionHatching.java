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

    private static RenderTarget worldDepthTarget;
    private static RenderTarget visibleDepthTarget;
    private static RenderTarget propDepthTarget;
    private static boolean worldDepthReady;

    private static int crossingsTexture;
    private static int crossingsFramebuffer;
    private static int targetWidth;
    private static int targetHeight;

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
        if (capture == null || PropCollisionShaders.getResolve() == null || !worldDepthReady)
            return false;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();

        ensureTargets(main.width, main.height);

        visibleDepthTarget.copyDepthFrom(main);
        main.bindWrite(false);

        clearCrossings();

        GL42.glBindImageTexture(
                0,
                crossingsTexture,
                0,
                false,
                0,
                GL15.GL_READ_WRITE,
                GL30.GL_R32UI
        );

        capture.setSampler("SceneDepth", worldDepthTarget.getDepthTextureId());

        return true;
    }

    public static void finishAndRender() {
        if (
                crossingsTexture == 0 ||
                worldDepthTarget == null ||
                visibleDepthTarget == null ||
                propDepthTarget == null
        )
            return;

        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL42.GL_TEXTURE_FETCH_BARRIER_BIT);
        GL42.glBindImageTexture(0, 0, 0, false, 0, GL15.GL_READ_ONLY, GL30.GL_R32UI);

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        ShaderInstance shader = PropCollisionShaders.getResolve();
        if (shader == null) {
            main.bindWrite(false);
            worldDepthReady = false;
            return;
        }

        main.bindWrite(false);

        int worldDepth = worldDepthTarget.getDepthTextureId();
        int visibleDepth = visibleDepthTarget.getDepthTextureId();
        int propDepth = propDepthTarget.getDepthTextureId();

        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, worldDepth);
        RenderSystem.setShaderTexture(1, visibleDepth);
        RenderSystem.setShaderTexture(2, crossingsTexture);
        RenderSystem.setShaderTexture(3, propDepth);

        shader.setSampler("SceneDepth", worldDepth);
        shader.setSampler("VisibleDepth", visibleDepth);
        shader.setSampler("Crossings", crossingsTexture);
        shader.setSampler("PropDepth", propDepth);

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
        if (crossingsTexture != 0 && targetWidth == width && targetHeight == height)
            return;

        destroyCrossings();

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
            destroyCrossings();
            throw new IllegalStateException("Prop crossing framebuffer incomplete: 0x" + Integer.toHexString(status));
        }
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

    private static void destroyCrossings() {
        if (crossingsFramebuffer != 0) {
            GL30.glDeleteFramebuffers(crossingsFramebuffer);
            crossingsFramebuffer = 0;
        }
        if (crossingsTexture != 0) {
            GL11.glDeleteTextures(crossingsTexture);
            crossingsTexture = 0;
        }
    }

    public static void beginPropDepthCapture() {
        if (propDepthTarget == null)
            return;
        propDepthTarget.clear(Minecraft.ON_OSX);
        propDepthTarget.bindWrite(true);
    }
}
