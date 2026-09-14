package net.centertain.ceac.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayDeque;
import java.util.Queue;

public final class Soundworks {
    public static void enqueueLocalStereoSound(Vec3 position, SoundEvent sound, float distanceMultiplier) {
        QUEUED_LOCAL_STEREO_SOUNDS.add(new LocalStereoSoundRequest(position, sound, distanceMultiplier));
    }

    public static void playLocalStereoSounds(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null)
            return;
        Player player = minecraft.player;
        if (player == null)
            return;
        while (!QUEUED_LOCAL_STEREO_SOUNDS.isEmpty()) {
            LocalStereoSoundRequest request = QUEUED_LOCAL_STEREO_SOUNDS.remove();
            Vec3 playerPosition = player.position();
            float volume = Mathworks.volumeByDistance(
                    playerPosition,
                    request.position(),
                    request.distanceMultiplier()
            );
            level.playLocalSound(
                    request.position().x,
                    request.position().y,
                    request.position().z,
                    request.sound(),
                    SoundSource.BLOCKS,
                    Mathworks.randomBetween(0.9f, 1.1f) * volume,
                    Mathworks.randomBetween(0.9f, 1.1f),
                    false
            );
        }
    }

    private record LocalStereoSoundRequest(
            Vec3 position,
            SoundEvent sound,
            float distanceMultiplier
    ) {}

    private static final Queue<LocalStereoSoundRequest> QUEUED_LOCAL_STEREO_SOUNDS = new ArrayDeque<>();
}
