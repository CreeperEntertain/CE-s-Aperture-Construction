package net.centertain.ceac.prop.framework.client;

import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.common.PropStepSoundLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class PropClientStepSounds {
    public static void playStepSounds(PlayLevelSoundEvent event) {
        if (!(event instanceof PlayLevelSoundEvent.AtEntity entityEvent))
            return;
        if (!(entityEvent.getEntity() instanceof LocalPlayer player))
            return;
        if (event.getSound() == null)
            return;

        String path = event.getSound().get().getLocation().getPath();
        if (!(path.endsWith(".step") || path.endsWith(".fall")))
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;

        int chunkX = player.blockPosition().getX() >> 4;
        int chunkZ = player.blockPosition().getZ() >> 4;
        List<Prop> nearbyProps = new ArrayList<>();
        for (int x = chunkX - 1; x <= chunkX + 1; x++)
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++)
                nearbyProps.addAll(ClientProps.getInChunk(x, z));

        SoundType soundType = PropStepSoundLookup.findSoundType(
                player,
                nearbyProps,
                minecraft.getResourceManager()
        );
        if (soundType == null)
            return;

        SoundEvent sound = player.fallDistance >= 6.0f
                ? soundType.getFallSound()
                : soundType.getStepSound();

        event.setSound(ForgeRegistries.SOUND_EVENTS.getHolder(sound)
                .orElseThrow(() -> new IllegalStateException("Unregistered step sound: " + sound.getLocation()))
        );
        event.setNewVolume(soundType.getVolume() * 0.15f);
        event.setNewPitch(soundType.getPitch());
    }
}
