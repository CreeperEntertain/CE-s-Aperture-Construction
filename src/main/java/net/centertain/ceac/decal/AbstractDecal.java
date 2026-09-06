package net.centertain.ceac.decal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class AbstractDecal {
    private UUID id;
    private Vec3 origin;
    private Vec3 normal;
    private int renderingOrder;
    private boolean glowing;
    private int pixelWidth;
    private int pixelHeight;
    private double blockDepth;
    private byte rotation;
    private ResourceLocation texture;
    private Set<BlockPos> attachedBlocks;

    public AbstractDecal(
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

    public void setId(UUID id) {
        this.id = id;
    }
    public void setOrigin(Vec3 origin) {
        this.origin = origin;
    }
    public void setNormal(Vec3 normal) {
        this.normal = normal;
    }
    public void setRenderingOrder(int renderingOrder) {
        this.renderingOrder = renderingOrder;
    }
    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }
    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
    }
    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }
    public void setBlockDepth(double blockDepth) {
        this.blockDepth = blockDepth;
    }
    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }
    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }
    public void setAttachedBlocks(Set<BlockPos> attachedBlocks) {
        this.attachedBlocks = attachedBlocks;
    }

    public static AbstractDecal getFromDecal(Decal decal) {
        return new AbstractDecal(
                decal.getId(),
                decal.getOrigin(),
                decal.getNormal(),
                decal.getRenderingOrder(),
                decal.getGlowing(),
                decal.getPixelWidth(),
                decal.getPixelHeight(),
                decal.getBlockDepth(),
                decal.getRotation(),
                decal.getTexture(),
                decal.getAttachedBlocks()
        );
    }
    public static AbstractDecal getAbstractFromDeltaChanges(Decal decal, @Nullable AbstractDecal abstraction) {
        if (abstraction == null)
            return getFromDecal(decal);
        return new AbstractDecal(
                decal.getId().equals(abstraction.id) ? abstraction.id : decal.getId(),
                decal.getOrigin().equals(abstraction.origin) ? abstraction.origin : decal.getOrigin(),
                decal.getNormal().equals(abstraction.normal) ? abstraction.normal : decal.getNormal(),
                decal.getRenderingOrder() == abstraction.renderingOrder ? abstraction.renderingOrder : decal.getRenderingOrder(),
                decal.getGlowing() == abstraction.glowing ? abstraction.glowing : decal.getGlowing(),
                decal.getPixelWidth() == abstraction.pixelWidth ? abstraction.pixelWidth : decal.getPixelWidth(),
                decal.getPixelHeight() == abstraction.pixelHeight ? abstraction.pixelHeight : decal.getPixelHeight(),
                decal.getBlockDepth() == abstraction.blockDepth ? abstraction.blockDepth : decal.getBlockDepth(),
                decal.getRotation() == abstraction.rotation ? abstraction.rotation : decal.getRotation(),
                decal.getTexture().equals(abstraction.texture) ? abstraction.texture : decal.getTexture(),
                decal.getAttachedBlocks().equals(abstraction.attachedBlocks) ? Set.copyOf(abstraction.attachedBlocks) : Set.copyOf(decal.getAttachedBlocks())
        );
    }
    public static Decal getFromAbstract(AbstractDecal abstraction) {
        return new Decal(
                abstraction.id,
                abstraction.origin,
                abstraction.normal,
                abstraction.renderingOrder,
                abstraction.glowing,
                abstraction.pixelWidth,
                abstraction.pixelHeight,
                abstraction.blockDepth,
                abstraction.rotation,
                abstraction.texture,
                abstraction.attachedBlocks
        );
    }
}
