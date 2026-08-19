package net.centertain.ceac.decal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;

public final class Decal {
    private final UUID id;
    private Vec3 origin;
    private Direction normal;

    private int pixelWidth;
    private int pixelHeight;

    private byte rotation;
    private ResourceLocation texture;

    private Set<BlockPos> attachedBlocks;

    public Decal(
            UUID id,
            Vec3 origin,
            Direction normal,
            int pixelWidth,
            int pixelHeight,
            byte rotation,
            ResourceLocation texture,
            Set<BlockPos> attachedBlocks
    ) {
        this.id = id;
        this.origin = origin;
        this.normal = normal;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.rotation = rotation;
        this.texture = texture;
        this.attachedBlocks = attachedBlocks;
    }

    public UUID getId() {
        return id;
    }

    public Vec3 getOrigin() {
        return origin;
    }
    public void setOrigin(Vec3 origin) {
        this.origin = origin;
    }

    public Direction getNormal() {
        return normal;
    }
    public void setNormal(Direction normal) {
        this.normal = normal;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }
    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }
    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    public byte getRotation() {
        return rotation;
    }
    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public Set<BlockPos> getAttachedBlocks() {
        return attachedBlocks;
    }
    public void setAttachedBlocks(Set<BlockPos> attachedBlocks) {
        this.attachedBlocks = attachedBlocks;
    }
}