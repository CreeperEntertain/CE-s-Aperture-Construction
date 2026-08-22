package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.client.render.TranslucentKBuffer;

public final class TranslucentCaptureState {
    private static boolean active;

    private TranslucentCaptureState() {}

    public static void begin() {
        active = true;
    }

    public static void end() {
        active = false;
        TranslucentKBuffer.barrier();
    }

    public static boolean isActive() {
        return active;
    }
}
