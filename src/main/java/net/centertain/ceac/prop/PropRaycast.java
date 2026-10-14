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

    @SuppressWarnings("resource")
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
        double closestPropDistance = reach;
        Target closest = null;

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

                    PropGeometry geometry = PropGeometry.get(
                            level.getServer().getResourceManager(),
                            definition.getModelId()
                    );
                    if (geometry == null)
                        continue;

                    double maxDistance = Math.min(reach, Math.max(blockDistance + EPSILON, closestPropDistance));
                    PropGeometry.RayHit hit = geometry.raycast(origin, direction, prop.getTransform(), maxDistance);
                    if (hit == null)
                        continue;

                    double distance = hit.distance();
                    if (
                            distance > reach ||
                            distance > blockDistance + EPSILON ||
                            distance >= closestPropDistance
                    )
                        continue;

                    closestPropDistance = distance;
                    closest = new Target(prop, definition, hit.location(), distance);
                }
            }

        return closest;
    }
}
