package net.centertain.ceac.decal.client;

public final class TranslucentCaptureState {
    private static boolean capturing;

    private TranslucentCaptureState() {}

    public static void begin() {
        capturing = true;
    }

    public static void end() {
        capturing = false;
    }

    public static boolean isCapturing() {
        return capturing;
    }
}
