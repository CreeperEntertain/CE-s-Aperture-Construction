package net.centertain.ceac.material;

import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.PlayLevelSoundEvent;

@SuppressWarnings("deprecation")
public final class MaterialShapeSoundEvents {
    private MaterialShapeSoundEvents() {}

    public static void handleSoundEvents(PlayLevelSoundEvent.AtEntity event) {
        if (topSoundHandling(event))
            return;
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

        SoundType soundType = shape.getSoundType(stepPosition, level, entity.getOnPos(), true);
        SoundEvent sound = entity.fallDistance >= 6
                ? soundType.getFallSound()
                : soundType.getStepSound();

        assert event.getSound() != null;
        String path = event.getSound().get().getLocation().getPath();
        if (
                path.endsWith(".hurt") ||
                path.endsWith(".small_fall") ||
                path.endsWith(".big_fall")
        )
            return false;

        event.setSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound));
        event.setNewPitch(soundType.pitch);

        return true;
    }
}
