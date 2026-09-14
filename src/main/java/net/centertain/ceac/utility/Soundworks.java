package net.centertain.ceac.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class Soundworks {
    /// For local stereo sound only!
    public static void playLocalSound(Vec3 position, SoundEvent sound, float distanceMultiplier) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null)
            return;
        Player player = minecraft.player;
        assert player != null;
        Vec3 playerPosition = player.position();
        float volume = Mathworks.volumeByDistance(playerPosition, position, distanceMultiplier);
        level.playLocalSound(
                position.x,
                position.y,
                position.z,
                sound,
                SoundSource.BLOCKS,
                Mathworks.randomBetween(0.9f, 1.1f) * volume,
                Mathworks.randomBetween(0.9f, 1.1f),
                false
        );
    }
}
