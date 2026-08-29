package net.centertain.ceac.decal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Decal {
    private final UUID id;

    private final Vec3 origin;
    private final Direction normal;
    private final int renderingOrder;

    private final boolean glowing;

    private final int pixelWidth;
    private final int pixelHeight;

    private final byte rotation;
    private final ResourceLocation texture;

    private final Set<BlockPos> attachedBlocks;

    public Decal(
            UUID id,
            Vec3 origin,
            Direction normal,
            int renderingOrder,
            boolean glowing,
            int pixelWidth,
            int pixelHeight,
            byte rotation,
            ResourceLocation texture,
            Set<BlockPos> attachedBlocks
    ) {
        this.id = id;
        this.origin = origin;
        this.normal = normal;
        this.renderingOrder = renderingOrder;
        this.glowing = glowing;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.rotation = rotation;
        this.texture = texture;
        this.attachedBlocks = Set.copyOf(attachedBlocks);
    }

    public UUID getId() {
        return id;
    }
    public Vec3 getOrigin() {
        return origin;
    }
    public Direction getNormal() {
        return normal;
    }
    public int getRenderingOrder() {
        return renderingOrder;
    }
    public boolean getGlowing() {
        return glowing;
    }
    public int getPixelWidth() {
        return pixelWidth;
    }
    public int getPixelHeight() {
        return pixelHeight;
    }
    public byte getRotation() {
        return rotation;
    }
    public ResourceLocation getTexture() {
        return texture;
    }
    public Set<BlockPos> getAttachedBlocks() {
        return attachedBlocks;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("Id", id);

        CompoundTag originTag = new CompoundTag();
        originTag.putDouble("X", origin.x);
        originTag.putDouble("Y", origin.y);
        originTag.putDouble("Z", origin.z);
        tag.put("Origin", originTag);

        tag.putString("Normal", normal.getName());
        tag.putInt("RenderingOrder", renderingOrder);
        tag.putInt("PixelWidth", pixelWidth);
        tag.putInt("PixelHeight", pixelHeight);
        tag.putBoolean("Glowing", glowing);
        tag.putByte("Rotation", rotation);
        tag.putString("Texture", texture.toString());

        ListTag blocks = new ListTag();

        for (BlockPos pos : attachedBlocks) {
            blocks.add(NbtUtils.writeBlockPos(pos));
        }

        tag.put("AttachedBlocks", blocks);

        return tag;
    }
    @Nullable
    public static Decal deserializeNBT(CompoundTag tag) {
        if (!tag.hasUUID("Id"))
            return null;
        UUID id = tag.getUUID("Id");

        if (!tag.contains("Origin", Tag.TAG_COMPOUND))
            return null;
        CompoundTag originTag = tag.getCompound("Origin");

        Vec3 origin = new Vec3(
                originTag.getDouble("X"),
                originTag.getDouble("Y"),
                originTag.getDouble("Z")
        );

        Direction normal = Direction.byName(tag.getString("Normal"));
        if (normal == null)
            return null;

        int renderingOrder = tag.getInt("RenderingOrder");

        boolean glowing = tag.getBoolean("Glowing");

        int pixelWidth = tag.getInt("PixelWidth");
        int pixelHeight = tag.getInt("PixelHeight");
        if (pixelWidth <= 0 || pixelHeight <= 0)
            return null;

        byte rotation = tag.getByte("Rotation");
        if (rotation < 0 || rotation > 15)
            return null;

        ResourceLocation texture = ResourceLocation.tryParse(tag.getString("Texture"));
        if (texture == null)
            return null;

        Set<BlockPos> attachedBlocks = new HashSet<>();
        if (tag.contains("AttachedBlocks", Tag.TAG_LIST)) {
            ListTag blocks = tag.getList("AttachedBlocks", Tag.TAG_COMPOUND);
            for (int i = 0; i < blocks.size(); i++) {
                attachedBlocks.add(NbtUtils.readBlockPos(blocks.getCompound(i)));
            }
        }

        return new Decal(
                id,
                origin,
                normal,
                renderingOrder,
                glowing,
                pixelWidth,
                pixelHeight,
                rotation,
                texture,
                attachedBlocks
        );
    }
}