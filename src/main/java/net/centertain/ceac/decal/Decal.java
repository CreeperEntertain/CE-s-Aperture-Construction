package net.centertain.ceac.decal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
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



    public static Set<BlockPos> getAttachedBlockSet(
            Level level,
            Vec3 origin,
            Vec3 normal,
            int pixelWidth,
            int pixelHeight,
            double blockDepth,
            byte rotation
    ) {
        double halfWidth = pixelWidth / 32.0;
        double halfHeight = pixelHeight / 32.0;
        double halfDepth = blockDepth / 2.0;

        Vec3 reference = Math.abs(normal.y) < 0.999
                ? new Vec3(0, 1, 0)
                : new Vec3(1, 0, 0);
        Vec3 right = reference.cross(normal).normalize();
        Vec3 up = normal.cross(right).normalize();

        double angle = (Math.PI * 2.0 / 16.0) * rotation;

        Vec3 rotatedRight = right.scale(Math.cos(angle))
                .add(up.scale(Math.sin(angle)))
                .normalize();
        Vec3 rotatedUp = normal.cross(rotatedRight).normalize();

        Vec3 widthVector = rotatedRight.scale(halfWidth);
        Vec3 heightVector = rotatedUp.scale(halfHeight);
        Vec3 depthVector = normal.scale(halfDepth);

        Vec3[] corners = { // sigh...
                origin.add(widthVector).add(heightVector).add(depthVector),
                origin.add(widthVector).add(heightVector).subtract(depthVector),
                origin.add(widthVector).subtract(heightVector).add(depthVector),
                origin.add(widthVector).subtract(heightVector).subtract(depthVector),
                origin.subtract(widthVector).add(heightVector).add(depthVector),
                origin.subtract(widthVector).add(heightVector).subtract(depthVector),
                origin.subtract(widthVector).subtract(heightVector).add(depthVector),
                origin.subtract(widthVector).subtract(heightVector).subtract(depthVector)
        };
        AABB bounds = getBounds(corners);
        Set<BlockPos> attachedBlocks = new HashSet<>();
        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            AABB blockBounds = new AABB(pos);
             if (obbIntersectsAabb(
                     origin,
                     rotatedRight,
                     rotatedUp,
                     normal,
                     halfWidth,
                     halfHeight,
                     halfDepth,
                     blockBounds
             ))
                 attachedBlocks.add(pos.immutable());
        });
        return attachedBlocks;
    }
    private static @NotNull AABB getBounds(Vec3[] corners) {
        double minX = corners[0].x;
        double minY = corners[0].y;
        double minZ = corners[0].z;
        double maxX = corners[0].x;
        double maxY = corners[0].y;
        double maxZ = corners[0].z;
        for (Vec3 corner : corners) {
            minX = Math.min(minX, corner.x);
            minY = Math.min(minY, corner.y);
            minZ = Math.min(minZ, corner.z);
            maxX = Math.max(maxX, corner.x);
            maxY = Math.max(maxY, corner.y);
            maxZ = Math.max(maxZ, corner.z);
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
    private static boolean obbIntersectsAabb(
            Vec3 center,
            Vec3 axisX,
            Vec3 axisY,
            Vec3 axisZ,
            double halfX,
            double halfY,
            double halfZ,
            AABB box
    ) {
        Vec3 boxCenter = box.getCenter();
        Vec3 boxHalf = new Vec3(
                box.getXsize() / 2.0,
                box.getYsize() / 2.0,
                box.getZsize() / 2.0
        );
        Vec3 translation = boxCenter.subtract(center);
        Vec3[] decalAxes = {axisX, axisY, axisZ};
        Vec3[] worldAxes = {
                new Vec3(1, 0, 0),
                new Vec3(0, 1, 0),
                new Vec3(0, 0, 1)
        };
        for (Vec3 axis : decalAxes)
            if (!overlapsOnAxis(
                    axis,
                    translation,
                    boxHalf,
                    decalAxes,
                    halfX,
                    halfY,
                    halfZ
            ))
                return false;
        for (Vec3 axis : worldAxes)
            if (!overlapsOnAxis(
                    axis,
                    translation,
                    boxHalf,
                    decalAxes,
                    halfX,
                    halfY,
                    halfZ
            ))
                return false;
        for (Vec3 decalAxis : decalAxes)
            for (Vec3 worldAxis : worldAxes) {
                Vec3 axis = decalAxis.cross(worldAxis);
                if (axis.lengthSqr() < 1.0e-12)
                    continue;
                if (!overlapsOnAxis(
                        axis.normalize(),
                        translation,
                        boxHalf,
                        decalAxes,
                        halfX,
                        halfY,
                        halfZ
                ))
                    return false;
            }
        return true;
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean overlapsOnAxis(
            Vec3 axis,
            Vec3 translation,
            Vec3 boxHalf,
            Vec3[] decalAxes,
            double halfX,
            double halfY,
            double halfZ
    ) {
        double distance = Math.abs(translation.dot(axis));
        double decalRadius =
                halfX * Math.abs(decalAxes[0].dot(axis)) +
                halfY * Math.abs(decalAxes[1].dot(axis)) +
                halfZ * Math.abs(decalAxes[2].dot(axis));
        double boxRadius =
                boxHalf.x * Math.abs(axis.x) +
                boxHalf.y * Math.abs(axis.y) +
                boxHalf.z * Math.abs(axis.z);
        return distance <= decalRadius + boxRadius;
    }
}