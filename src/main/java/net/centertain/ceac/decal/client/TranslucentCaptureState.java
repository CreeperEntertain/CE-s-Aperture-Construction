package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public final class TranslucentCaptureState {
    private static boolean active;

    private TranslucentCaptureState() {}

    public static void begin() {
        active = true;
        TranslucentKBuffer.clear();
        TranslucentKBuffer.bind();
    }

    public static void end() {
        TranslucentKBuffer.barrier();
        DecalShaders.sortKBuffer(
                TranslucentKBuffer.getWidth(),
                TranslucentKBuffer.getHeight()
        );
        TranslucentKBuffer.barrier();
        active = false;
    }

    public static boolean isActive() {
        return active;
    }
}
