package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;

public final class TranslucentCaptureTarget {
    private static RenderTarget target;

    private TranslucentCaptureTarget() {}

    public static void init() {
        resize();
    }

    public static void resize() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (target != null)
            target.destroyBuffers();

        target = new TextureTarget(
                width,
                height,
                true,
                Minecraft.ON_OSX
        );
        target.resize(width, height, Minecraft.ON_OSX);
        target.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static RenderTarget get() {
        return target;
    }

    public static void clear() {
        target.bindWrite(true);
        target.clear(true);
    }
}
