package net.centertain.ceac.decal.client;

import net.minecraft.client.renderer.RenderType;

public final class OpaqueLightmapCaptureState {
    private static boolean active;
    private static RenderType renderType;

    private OpaqueLightmapCaptureState() {}

    public static void begin(RenderType type) {
        renderType = type;
        active = true;
    }

    public static void end() {
        active = false;
        renderType = null;
    }

    public static boolean isActive() {
        return active;
    }

    public static RenderType getRenderType() {
        return renderType;
    }
}