package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;

public final class TranslucentRenderTargets {
    private static RenderTarget translucentDepth;

    private TranslucentRenderTargets() {}

    public static void init(int width, int height) {
        if (translucentDepth != null)
            translucentDepth.destroyBuffers();
        translucentDepth = new TextureTarget(
                width,
                height,
                true,
                Minecraft.ON_OSX
        );
        translucentDepth.setClearColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static RenderTarget getTranslucentDepth() {
        return translucentDepth;
    }

    public static void destroy() {
        if (translucentDepth != null) {
            translucentDepth.destroyBuffers();
            translucentDepth = null;
        }
    }
}
