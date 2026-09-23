package net.centertain.ceac.utility;

import net.minecraft.world.level.Level;
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

    public static Vec3 getItemDropVelocity(Level level) {
        return new Vec3(
                level.random.triangle(0.0D, 0.1148500017118454D),
                level.random.triangle(0.2D, 0.1148500017118454D),
                level.random.triangle(0.0D, 0.1148500017118454D)
        );
    }

    public static boolean isVec3InRange(Vec3 a, Vec3 b, double epsilon) {
        boolean dx = Math.abs(a.x - b.x) <= epsilon;
        boolean dy = Math.abs(a.y - b.y) <= epsilon;
        boolean dz = Math.abs(a.z - b.z) <= epsilon;
        return dx && dy && dz;
    }
}
