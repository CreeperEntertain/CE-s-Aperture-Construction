package net.centertain.ceac.prop.client;

import net.centertain.ceac.prop.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public final class PropTargeting {
    private static final double EPSILON = 1.0e-7;

    private static final Matrix4f RENDER_TRANSFORM = new Matrix4f();

    private PropTargeting() {}

    public static @Nullable PropHitResult pick(
            LocalPlayer player,
            float partialTick,
            HitResult vanillaHit
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.gameMode == null)
            return null;

        Vec3 origin = player.getEyePosition(partialTick);
        Vec3 direction = player.getViewVector(partialTick).normalize();

        double reach = minecraft.gameMode.getPickRange();
        double vanillaDistance = vanillaHit == null || vanillaHit.getType() == HitResult.Type.MISS
                ? reach : origin.distanceTo(vanillaHit.getLocation());
        double closestDistance = Math.min(reach, vanillaDistance);
        if (closestDistance <= 0.0)
            return null;

        Prop closestProp = null;
        Vec3 closestLocation = null;
        Vec3 closestNormal = null;

        int chunkRadius = (int) Math.ceil(reach / 16.0) + 1;
        BlockPos playerPos = player.blockPosition();

        int minChunkX = (playerPos.getX() >> 4) - chunkRadius;
        int maxChunkX = (playerPos.getX() >> 4) + chunkRadius;
        int minChunkZ = (playerPos.getZ() >> 4) - chunkRadius;
        int maxChunkZ = (playerPos.getZ() >> 4) + chunkRadius;

        long renderTimeNanos = System.nanoTime();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++)
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++)
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ)) {
                    PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
                    if (definition == null || !definition.isTargetable())
                        continue;

                    PropGeometry geometry = PropGeometry.get(minecraft.getResourceManager(), definition.getModelId());
                    if (geometry == null)
                        continue;

                    ClientProps.copyRenderTransformTo(prop, RENDER_TRANSFORM, renderTimeNanos);
                    PropGeometry.RayHit hit = geometry.raycast(origin, direction, RENDER_TRANSFORM, closestDistance);
                    if (hit == null)
                        continue;

                    closestDistance = hit.distance();
                    closestProp = prop;
                    closestLocation = hit.location();
                    closestNormal = hit.normal();
                }

        if (closestProp == null)
            return null;

        Direction hitFace = Direction.getNearest(
                closestNormal.x,
                closestNormal.y,
                closestNormal.z
        );
        BlockHitResult underlyingBlockHit = player.level().clip(new ClipContext(
                origin,
                origin.add(direction.scale(reach)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        return new PropHitResult(
                closestLocation,
                hitFace,
                BlockPos.containing(closestLocation),
                false,
                closestProp,
                underlyingBlockHit
        );
    }
}
