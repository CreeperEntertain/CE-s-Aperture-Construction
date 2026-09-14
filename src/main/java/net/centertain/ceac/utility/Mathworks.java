package net.centertain.ceac.utility;

import net.minecraft.world.phys.Vec3;

public final class Mathworks {
    private Mathworks() {}

    public static float randomBetween(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }

    public static float volumeByDistance(Vec3 playerPos, Vec3 soundPos, float distanceMultiplier) {
        float distance = (float) playerPos.distanceTo(soundPos) * distanceMultiplier;
        return Math.max(0.0f, 1.0f - distance / 16.0f);
    }
}
