package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.client.render.TranslucentKBuffer;

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
