package net.centertain.ceac.decal.server;

import net.centertain.ceac.decal.Decal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    public void removeDecal(UUID id) {
        Decal decal = decals.remove(id);
        if (decal == null)
            return;
        for (BlockPos pos : decal.getAttachedBlocks()) {
            Set<UUID> ids = blockIndex.get(pos);
            if (ids == null)
                continue;
            ids.remove(id);
            if (ids.isEmpty())
                blockIndex.remove(pos);
        }
        chunk.setUnsaved(true);
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
        for (Decal decal : decals.values()) {
            decalList.add(decal.serializeNBT());
        }
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