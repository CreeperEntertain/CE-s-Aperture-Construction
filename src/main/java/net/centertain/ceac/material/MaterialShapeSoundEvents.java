package net.centertain.ceac.material;

import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;

@SuppressWarnings("deprecation")
public final class MaterialShapeSoundEvents {
    private MaterialShapeSoundEvents() {}

    public static void handleLevelSoundEvents(PlayLevelSoundEvent event) {
        if (event instanceof PlayLevelSoundEvent.AtEntity entityEvent)
            if (topSoundHandling(entityEvent)) return;
    }

    public static void handleGeneralSoundEvents(PlaySoundEvent event) {
        if (digSoundHandling(event)) return;
    }

    private static boolean topSoundHandling(PlayLevelSoundEvent.AtEntity event) {
        if (!(event.getEntity() instanceof LivingEntity entity))
            return false;
        Level level = event.getLevel();

        BlockPos onPos = entity.getOnPos();
        if (!(level.getBlockState(onPos).getBlock() instanceof MaterialShape shape))
            return false;

        Vec3 stepPosition = entity.position().subtract(
                onPos.getX(),
                onPos.getY(),
                onPos.getZ()
        );

        SoundType soundType = shape.getSoundType(stepPosition, level, entity.getOnPos());
        SoundEvent sound = entity.fallDistance >= 6
                ? soundType.getFallSound()
                : soundType.getStepSound();

        assert event.getSound() != null;
        String path = event.getSound().get().getLocation().getPath();
        if (!(
                path.endsWith(".step") ||
                path.endsWith(".fall")
        ))
            return false;

        event.setSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound));
        event.setNewPitch(soundType.pitch);

        return true;
    }

    private static boolean digSoundHandling(PlaySoundEvent event) {
        SoundInstance original = event.getOriginalSound();
        if (!original.getLocation().equals(SoundType.NETHERITE_BLOCK.getHitSound().getLocation()))
            return false;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null)
            return false;

        HitResult hit = minecraft.player.pick(
                minecraft.player.getBlockReach(),
                1.0F,
                false
        );
        if (!(hit instanceof BlockHitResult blockHit))
            return false;

        BlockPos pos = blockHit.getBlockPos();
        if (!(minecraft.level.getBlockState(pos).getBlock() instanceof MaterialShape shape))
            return false;

        Vec3 localHit = blockHit.getLocation().subtract(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        SoundType soundType = shape.getSoundType(localHit, minecraft.level, pos);
        SoundInstance replacement = new SimpleSoundInstance(
                soundType.getHitSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 8.0F,
                soundType.getPitch() * 0.5F,
                SoundInstance.createUnseededRandom(),
                original.getX(),
                original.getY(),
                original.getZ()
        );

        event.setSound(replacement);

        return true;
    }
}
