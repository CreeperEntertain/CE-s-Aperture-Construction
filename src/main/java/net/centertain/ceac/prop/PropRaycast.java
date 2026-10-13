package net.centertain.ceac.prop;

import net.centertain.ceac.prop.server.PropCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class PropRaycast {
    private static final double EPSILON = 1.0e-5;

    private PropRaycast() {}

    public record Target(
            Prop prop,
            PropDefinition definition,
            Vec3 location,
            double distance
    ) {}

    public static @Nullable Target findTarget(
            @NotNull ServerPlayer player,
            double reach
    ) {
        if (!(player.level() instanceof ServerLevel level))
            return null;

        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0f).normalize();
        Vec3 end = origin.add(direction.scale(reach));
        BlockHitResult blockHit = level.clip(new ClipContext(
                origin,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
        double blockDistance = blockHit.getType() == HitResult.Type.MISS
                ? reach : origin.distanceTo(blockHit.getLocation());
        Target closest = null;
        double closestPropDistance = reach;
        BlockPos playerPos = player.blockPosition();
        int chunkRadius = (int) Math.ceil(reach / 16.0) + 1;

        int minChunkX = (playerPos.getX() >> 4) - chunkRadius;
        int maxChunkX = (playerPos.getX() >> 4) + chunkRadius;
        int minChunkZ = (playerPos.getZ() >> 4) - chunkRadius;
        int maxChunkZ = (playerPos.getZ() >> 4) + chunkRadius;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++)
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null)
                    continue;

                for (Prop prop : PropCapabilities.get(chunk).getProps().values()) {
                    PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
                    if (definition == null || !definition.isTargetable())
                        continue;

                    Matrix4f transform = prop.getTransform();
                    if (Math.abs(transform.determinant()) < 1.0e-8f)
                        continue;

                    Matrix4f inverse = new Matrix4f(transform).invert();
                    Vec3 localStart = transformPoint(origin, inverse);
                    Vec3 localEnd = transformPoint(end, inverse);
                    AABB hitbox = definition.getLocalHitbox();
                    Vec3 localHit = hitbox.clip(localStart, localEnd).orElse(null);
                    if (localHit == null)
                        continue;

                    Vec3 worldHit = transformPoint(localHit, transform);
                    double distance = origin.distanceTo(worldHit);
                    if (distance > reach)
                        continue;
                    if (distance > blockDistance + EPSILON)
                        continue;
                    if (distance >= closestPropDistance)
                        continue;

                    closestPropDistance = distance;
                    closest = new Target(prop, definition, worldHit, distance);
                }
            }

        return closest;
    }

    @Contract("_, _ -> new")
    private static @NotNull Vec3 transformPoint(
            @NotNull Vec3 point,
            Matrix4f transform
    ) {
        Vector3f result = new Vector3f(
                (float) point.x,
                (float) point.y,
                (float) point.z
        );
        result.mulPosition(transform);
        return new Vec3(result.x, result.y, result.z);
    }
}
