package net.centertain.ceac.decal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
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
            CompoundTag tag = new CompoundTag();

            tag.putUUID("Id", decal.getId());

            CompoundTag origin = new CompoundTag();
            origin.putDouble("X", decal.getOrigin().x);
            origin.putDouble("Y", decal.getOrigin().y);
            origin.putDouble("Z", decal.getOrigin().z);
            tag.put("Origin", origin);

            tag.putString("Normal", decal.getNormal().getName());
            tag.putInt("RenderingOrder", decal.getRenderingOrder());
            tag.putInt("PixelWidth", decal.getPixelWidth());
            tag.putInt("PixelHeight", decal.getPixelHeight());
            tag.putByte("Rotation", decal.getRotation());
            tag.putString("Texture", decal.getTexture().toString());

            ListTag blocks = new ListTag();

            for (BlockPos pos : decal.getAttachedBlocks()) {
                blocks.add(NbtUtils.writeBlockPos(pos));
            }

            tag.put("AttachedBlocks", blocks);
            decalList.add(tag);
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
            CompoundTag tag = decalList.getCompound(i);

            UUID id = tag.getUUID("Id");

            CompoundTag originTag = tag.getCompound("Origin");
            Vec3 origin = new Vec3(
                    originTag.getDouble("X"),
                    originTag.getDouble("Y"),
                    originTag.getDouble("Z")
            );

            Direction normal = Direction.byName(tag.getString("Normal"));
            if (normal == null)
                continue;

            int renderingOrder = tag.getInt("RenderingOrder");
            int pixelWidth = tag.getInt("PixelWidth");
            int pixelHeight = tag.getInt("PixelHeight");
            if (pixelWidth < 0 || pixelHeight < 0)
                continue;
            byte rotation = tag.getByte("Rotation");
            if (rotation < 0 || rotation > 15)
                continue;

            ResourceLocation texture = ResourceLocation.tryParse(tag.getString("Texture"));
            if (texture == null)
                continue;

            Set<BlockPos> attachedBlocks = new HashSet<>();
            ListTag blocks = tag.getList("AttachedBlocks", Tag.TAG_COMPOUND);
            for (int j = 0; j < blocks.size(); j++) {
                attachedBlocks.add(NbtUtils.readBlockPos(blocks.getCompound(j)));
            }

            addDecal(new Decal(
                    id,
                    origin,
                    normal,
                    renderingOrder,
                    pixelWidth,
                    pixelHeight,
                    rotation,
                    texture,
                    attachedBlocks
            ));
        }

//        if (!decals.isEmpty())
//            System.out.println("Loaded " + decals.size() + " decals.");
    }
}