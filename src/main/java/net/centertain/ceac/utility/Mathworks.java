package net.centertain.ceac.utility;

public final class Mathworks {
    private Mathworks() {}

    public static float randomBetween(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }
}
