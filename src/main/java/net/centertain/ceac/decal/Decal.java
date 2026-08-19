package net.centertain.ceac.decal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;

public final class Decal {
    private final UUID id;

    private final Vec3 origin;
    private final Direction normal;
    private final int renderingOrder;

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
}