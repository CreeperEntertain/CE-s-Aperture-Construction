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
    private final Vec3 normal;
    private final int renderingOrder;

    private final boolean glowing;

    private final int pixelWidth;
    private final int pixelHeight;
    private final double blockDepth;

    private final byte rotation;
    private final ResourceLocation texture;

    private final Set<BlockPos> attachedBlocks;

    public Decal(
            UUID id,
            Vec3 origin,
            Vec3 normal,
            int renderingOrder,
            boolean glowing,
            int pixelWidth,
            int pixelHeight,
            double blockDepth,
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
        this.blockDepth = blockDepth;
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
    public Vec3 getNormal() {
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
    public double getBlockDepth() {
        return blockDepth;
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

        CompoundTag normalTag = new CompoundTag();
        normalTag.putDouble("X", normal.x);
        normalTag.putDouble("Y", normal.y);
        normalTag.putDouble("Z", normal.z);
        tag.put("Normal", normalTag);

        tag.putInt("RenderingOrder", renderingOrder);
        tag.putInt("PixelWidth", pixelWidth);
        tag.putInt("PixelHeight", pixelHeight);
        tag.putDouble("BlockDepth", blockDepth);
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

        Vec3 normal;
        if (tag.contains("Normal", Tag.TAG_COMPOUND)) {
            // New format
            CompoundTag normalTag = tag.getCompound("Normal");
            normal = new Vec3(
                    normalTag.getDouble("X"),
                    normalTag.getDouble("Y"),
                    normalTag.getDouble("Z")
            );
        } else if (tag.contains("Normal", Tag.TAG_STRING)) {
            // Old format preservation
            Direction oldNormal = Direction.byName(tag.getString("Normal"));
            if (oldNormal == null)
                return null;
            normal = Vec3.atLowerCornerOf(oldNormal.getNormal());
        } else
            return null;
        if (normal.lengthSqr() < 1.0e-12)
            return null;
        normal = normal.normalize();

        int renderingOrder = tag.getInt("RenderingOrder");

        boolean glowing = tag.getBoolean("Glowing");

        int pixelWidth = tag.getInt("PixelWidth");
        int pixelHeight = tag.getInt("PixelHeight");
        if (pixelWidth <= 0 || pixelHeight <= 0)
            return null;

        double blockDepth = tag.getDouble("BlockDepth");
        if (blockDepth < 0 || blockDepth > 16)
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
                blockDepth,
                rotation,
                texture,
                attachedBlocks
        );
    }
}