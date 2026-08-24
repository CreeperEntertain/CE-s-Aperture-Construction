package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class TranslucentRenderTargets {
    private static RenderTarget translucentDepth;

    private TranslucentRenderTargets() {}

    public static void init(int width, int height) {
        if (translucentDepth != null)
            translucentDepth.destroyBuffers();
        translucentDepth = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        translucentDepth.setClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        translucentDepth.clear(true);
        TranslucentKBuffer.init(width, height);
    }

    public static void resize(int width, int height) {
        if (translucentDepth != null) {
            translucentDepth.resize(
                    width,
                    height,
                    Minecraft.ON_OSX
            );
        }
        TranslucentKBuffer.resize(width, height);
    }

    public static RenderTarget getTranslucentDepth() {
        return translucentDepth;
    }

    public static void destroy() {
        if (translucentDepth != null) {
            translucentDepth.destroyBuffers();
            translucentDepth = null;
        }
        TranslucentKBuffer.destroy();
    }
}
