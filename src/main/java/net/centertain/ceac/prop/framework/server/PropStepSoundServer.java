package net.centertain.ceac.prop.framework.server;

import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.common.PropStepSoundLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public final class PropStepSoundServer {
    private PropStepSoundServer() {}


    public static boolean isSupportedByProp(Entity entity) {
        if (entity.level().isClientSide)
            return false;
        if (entity.noPhysics || entity.isSpectator())
            return false;
        if (!(entity.level() instanceof ServerLevel level))
            return false;

        List<Prop> nearbyProps = getNearbyProps(entity, level);

        return PropStepSoundLookup.isSupportedByProp(
                entity,
                nearbyProps,
                level.getServer().getResourceManager()
        );
    }

    public static boolean playIfOnProp(Entity entity) {
        if (entity.level().isClientSide || !entity.onGround())
            return false;
        if (entity.noPhysics || entity.isSpectator())
            return false;
        if (!(entity.level() instanceof ServerLevel level))
            return false;

        List<Prop> nearbyProps = getNearbyProps(entity, level);
        SoundType soundType = PropStepSoundLookup.findSoundType(
                entity,
                nearbyProps,
                level.getServer().getResourceManager()
        );
        if (soundType == null)
            return false;

        entity.playSound(
                soundType.getStepSound(),
                soundType.getVolume() * 0.15f,
                soundType.getPitch()
        );
        return true;
    }

    private static List<Prop> getNearbyProps(Entity entity, ServerLevel level) {
        int chunkX = entity.blockPosition().getX() >> 4;
        int chunkZ = entity.blockPosition().getZ() >> 4;

        List<Prop> nearbyProps = new ArrayList<>();

        for (int x = chunkX - 1; x <= chunkX + 1; x++)
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
                if (chunk != null)
                    nearbyProps.addAll(PropCapabilities.get(chunk).getProps().values());
            }

        return nearbyProps;
    }
}
