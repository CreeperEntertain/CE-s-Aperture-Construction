package net.centertain.ceac.decal.server;

import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.utility.Mathworks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;

import java.util.*;

public final class DecalBreakage {
    public static void breakDecalsInRange(BlockEvent.BreakEvent event) {
        LevelAccessor accessor = event.getLevel();
        if (!(accessor instanceof Level level) || level.isClientSide)
            return;
        BlockPos pos = event.getPos();
        if (pos == null)
            return;
        LevelChunk[] chunks = {
                level.getChunkAt(new BlockPos(pos.getX() - 16, pos.getY(), pos.getZ() - 16)),
                level.getChunkAt(new BlockPos(pos.getX() - 16, pos.getY(), pos.getZ())),
                level.getChunkAt(new BlockPos(pos.getX() - 16, pos.getY(), pos.getZ() + 16)),

                level.getChunkAt(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 16)),
                level.getChunkAt(new BlockPos(pos.getX(), pos.getY(), pos.getZ())),
                level.getChunkAt(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 16)),

                level.getChunkAt(new BlockPos(pos.getX() + 16, pos.getY(), pos.getZ() - 16)),
                level.getChunkAt(new BlockPos(pos.getX() + 16, pos.getY(), pos.getZ())),
                level.getChunkAt(new BlockPos(pos.getX() + 16, pos.getY(), pos.getZ() + 16))
        };
        DecalManager[] managers = new DecalManager[chunks.length];
        for (int i = 0; i < chunks.length; i++)
            managers[i] = DecalCapabilities.get(chunks[i]);

        List<Decal> potentialDecals = new ArrayList<>();
        for (DecalManager manager : managers)
            potentialDecals.addAll(manager.getDecals().values());
        if (potentialDecals.isEmpty())
            return;

        List<Decal> decals = new ArrayList<>();
        for (Decal decal : potentialDecals)
            if (decal.isAttachedToPos(pos))
                decals.add(decal);
        if (decals.isEmpty())
            return;

        for (Decal decal : decals) // Fiiiiiinally do dropping. Geez.
            if (!decal.isAttachedToGeometry())
                Decal.removeFromWorld(decal, level);
    }

    public static void dropItem(
            Level level,
            Vec3 position
    ) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
            return;
        ItemStack stack = new ItemStack(ModItems.DECAL.get());
        ItemEntity item = new ItemEntity(
                level,
                position.x,
                position.y,
                position.z,
                stack
        );
        item.setDefaultPickUpDelay();
        item.setDeltaMovement(Mathworks.getItemDropVelocity(level));
        level.addFreshEntity(item);
    }
}
