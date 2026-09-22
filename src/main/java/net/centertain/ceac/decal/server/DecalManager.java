package net.centertain.ceac.decal.server;

import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.sound.ModSounds;
import net.centertain.ceac.utility.Mathworks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class DecalManager implements INBTSerializable<CompoundTag> {
    private final LevelChunk chunk;

    private final Map<UUID, Decal> decals = new HashMap<>();
    private final Map<BlockPos, Set<UUID>> blockIndex = new HashMap<>();

    public DecalManager(LevelChunk chunk) {
        this.chunk = chunk;
    }

    public void addDecal(Decal decal) {
        if (decals.containsKey(decal.getId()))
            throw new IllegalArgumentException("Decal with ID " + decal.getId() + " already exists");
        decals.put(decal.getId(), decal);
        ServerLevel level = (ServerLevel) chunk.getLevel();
        level.playSound(
                null,
                BlockPos.containing(decal.getOrigin()),
                ModSounds.DECAL_PLACEMENT.get(),
                SoundSource.BLOCKS,
                Mathworks.randomBetween(0.9f, 1.1f),
                Mathworks.randomBetween(0.9f, 1.1f)
        );
        for (BlockPos pos : decal.getAttachedBlocks()) {
            BlockPos immutablePos = pos.immutable();
            blockIndex
                    .computeIfAbsent(immutablePos, p -> new HashSet<>())
                    .add(decal.getId());
        }
        chunk.setUnsaved(true);
    }
    public Decal getDecal(UUID id) {
        return decals.get(id);
    }
    public void removeDecals(UUID[] ids, BlockPos pos) {
        Decal[] decals = new Decal[ids.length];
        for (int i = 0; i < ids.length; i++)
            decals[i] = this.decals.remove(ids[i]);
        ServerLevel level = (ServerLevel) chunk.getLevel();
        int successCount = 0;
        for (int i = 0; i < ids.length; i++)
            if (decals[i] != null) {
                removeSingleDecal(decals[i], ids[i]);
                successCount++;
            }
        if (successCount > 0) {
            chunk.setUnsaved(true);
            level.playSound(
                    null,
                    pos,
                    ModSounds.DECAL_DELETION.get(),
                    SoundSource.BLOCKS,
                    Mathworks.randomBetween(1.8f, 2.2f),
                    Mathworks.randomBetween(0.9f, 1.1f)
            );
        }
    }
    public void removeDecal(UUID id) {
        Decal decal = decals.remove(id);
        if (decal == null)
            return;
        ServerLevel level = (ServerLevel) chunk.getLevel();
        Vec3 origin = decal.getOrigin();
        removeSingleDecal(decal, id);
        chunk.setUnsaved(true);
        level.playSound(
                null,
                origin.x,
                origin.y,
                origin.z,
                ModSounds.DECAL_DELETION.get(),
                SoundSource.BLOCKS,
                Mathworks.randomBetween(0.9f, 1.1f),
                Mathworks.randomBetween(0.9f, 1.1f)
        );
    }
    private void removeSingleDecal(Decal decal, UUID id) {
        for (BlockPos pos : decal.getAttachedBlocks()) {
            Set<UUID> ids = blockIndex.get(pos);
            if (ids == null)
                continue;
            ids.remove(id);
            if (ids.isEmpty())
                blockIndex.remove(pos);
        }
    }
    public Set<UUID> getDecalsAt(BlockPos pos) {
        return blockIndex.getOrDefault(pos, Set.of());
    }
    public Map<UUID, Decal> getDecals() {
        return decals;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag root = new CompoundTag();
        ListTag decalList = new ListTag();
        for (Decal decal : decals.values())
            decalList.add(decal.serializeNBT());
        root.put("Decals", decalList);
        return root;
    }
    @Override
    public void deserializeNBT(CompoundTag root) {
        decals.clear();
        blockIndex.clear();
        ListTag decalList = root.getList("Decals", Tag.TAG_COMPOUND);
        for (int i = 0; i < decalList.size(); i++) {
            Decal decal = Decal.deserializeNBT(decalList.getCompound(i));
            if (decal == null)
                continue;
            addDecal(decal);
        }
    }
}