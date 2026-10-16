package net.centertain.ceac.prop;

import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.centertain.ceac.prop.enums.PropMotionMode;
import net.centertain.ceac.prop.network.SyncPropPacket;
import net.centertain.ceac.prop.server.PropCapabilities;
import net.centertain.ceac.prop.server.PropManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

public final class Prop {
    private static final int TRANSFORM_SIZE = 16;

    private final UUID id;
    private final ResourceLocation definitionId;
    private Matrix4f transform;
    private long transformRevision;
    private PropCollisionMode collisionMode;
    private PropMotionMode motionMode;
    private CompoundTag data;

    public Prop(
            UUID id,
            ResourceLocation definition,
            Matrix4f transform,
            PropCollisionMode collisionMode,
            PropMotionMode motionMode
    ) {
        this(id, definition, transform, collisionMode, motionMode, new CompoundTag());
    }

    public Prop(
            UUID id,
            ResourceLocation definitionId,
            Matrix4f transform,
            PropCollisionMode collisionMode,
            PropMotionMode motionMode,
            @NotNull CompoundTag data
    ) {
        this.id = id;
        this.definitionId = definitionId;
        this.transform = new Matrix4f(transform);
        this.collisionMode = collisionMode;
        this.motionMode = motionMode;
        this.data = data.copy();
    }

    public UUID getId() {
        return id;
    }
    public ResourceLocation getDefinitionId() {
        return definitionId;
    }
    @Contract(" -> new")
    public @NotNull Matrix4f getTransform() {
        return new Matrix4f(transform);
    }
    public long getTransformRevision() {
        return transformRevision;
    }
    public PropCollisionMode getCollisionMode() {
        return collisionMode;
    }
    public PropMotionMode getMotionMode() {
        return motionMode;
    }
    public CompoundTag getData() {
        return data;
    }
    @Contract(" -> new")
    public @NotNull BlockPos getBlockPosition() {
        return BlockPos.containing(
                transform.m30(),
                transform.m31(),
                transform.m32()
        );
    }
    @Contract(" -> new")
    public @NotNull Vec3 getPosition() {
        return new Vec3(
                transform.m30(),
                transform.m31(),
                transform.m32()
        );
    }
    public @NotNull Vec3 getRotation() {
        Vector3f euler = getRotationQuat().getEulerAnglesXYZ(new Vector3f());
        return new Vec3(euler.x, euler.y, euler.z);
    }
    public Quaternionf getRotationQuat() {
        return transform.getUnnormalizedRotation(new Quaternionf());
    }
    public @NotNull Vec3 getScale() {
        Vector3f scale = transform.getScale(new Vector3f());
        return new Vec3(scale.x, scale.y, scale.z);
    }

    public void setTransform(Matrix4f transform) {
        if (this.transform.equals(transform))
            return;
        this.transform = new Matrix4f(transform);
        transformRevision++;
    }
    public void setCollisionMode(PropCollisionMode collisionMode) {
        this.collisionMode = collisionMode;
    }
    public void setMotionMode(PropMotionMode motionMode) {
        this.motionMode = motionMode;
    }
    public void setData(CompoundTag data) {
        this.data = data;
    }
    public void setPosition(@NotNull Vec3 position) {
        float x = (float) position.x;
        float y = (float) position.y;
        float z = (float) position.z;
        if (
                Float.compare(transform.m30(), x) == 0 &&
                Float.compare(transform.m31(), y) == 0 &&
                Float.compare(transform.m32(), z) == 0
        )
            return;
        transform.setTranslation(x, y, z);
        transformRevision++;
    }
    public void setRotation(@NotNull Vec3 rotation) {
        setRotation(new Quaternionf().rotationXYZ(
                (float) rotation.x,
                (float) rotation.y,
                (float) rotation.z
        ));
    }
    public void setRotation(Quaternionf rotation) {
        Quaternionf normalized = new Quaternionf(rotation).normalize();
        Quaternionf current = transform.getUnnormalizedRotation(new Quaternionf()).normalize();
        if (Math.abs(current.dot(normalized)) >= 1.0f - 1.0e-7f)
            return;

        Vector3f position = transform.getTranslation(new Vector3f());
        Vector3f scale = transform.getScale(new Vector3f());

        transform.translationRotateScale(position, normalized, scale);
        transformRevision++;
    }
    public void setScale(@NotNull Vec3 scale) {
        float x = (float) scale.x;
        float y = (float) scale.y;
        float z = (float) scale.z;
        Vector3f currentScale = transform.getScale(new Vector3f());
        if (
                Float.compare(currentScale.x, x) == 0 &&
                Float.compare(currentScale.y, y) == 0 &&
                Float.compare(currentScale.z, z) == 0
        )
            return;

        Vector3f position = transform.getTranslation(new Vector3f());
        Quaternionf rotation = transform.getUnnormalizedRotation(new Quaternionf());

        transform.translationRotateScale(position, rotation, new Vector3f(x, y, z));
        transformRevision++;
    }

    public @NotNull CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", id);
        tag.putString("Definition", definitionId.toString());
        tag.putString("CollisionMode", collisionMode.name());
        tag.putString("MotionMode", motionMode.name());
        float[] values = new float[TRANSFORM_SIZE];
        transform.get(values);
        ListTag matrix = new ListTag();
        for (float value : values)
            matrix.add(FloatTag.valueOf(value));
        tag.put("Transform", matrix);
        tag.put("Data", data.copy());
        return tag;
    }

    public static @Nullable Prop deserializeNBT(@NotNull CompoundTag tag) {
        if (!tag.hasUUID("Id"))
            return null;
        ResourceLocation definitionId = ResourceLocation.tryParse(tag.getString("Definition"));
        if (definitionId == null)
            return null;
        ListTag matrixTag = tag.getList("Transform", FloatTag.TAG_FLOAT);
        if (matrixTag.size() != TRANSFORM_SIZE)
            return null;
        float[] values = new float[TRANSFORM_SIZE];
        for (int i = 0; i < TRANSFORM_SIZE; i++) {
            values[i] = matrixTag.getFloat(i);
            if (!Float.isFinite(values[i]))
                return null;
        }
        try {
            PropCollisionMode collisionMode = PropCollisionMode.valueOf(tag.getString("CollisionMode"));
            PropMotionMode motionMode = PropMotionMode.valueOf(tag.getString("MotionMode"));
            CompoundTag data = tag.contains("Data", FloatTag.TAG_COMPOUND)
                    ? tag.getCompound("Data") : new CompoundTag();
            return new Prop(
                    tag.getUUID("Id"),
                    definitionId,
                    new Matrix4f().set(values),
                    collisionMode,
                    motionMode,
                    data
            );
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static void placeInWorld(Prop prop, @NotNull Level level) {
        if (level.isClientSide)
            throw new IllegalStateException("Props must be placed on the logical server");
        LevelChunk chunk = level.getChunkAt(prop.getBlockPosition());
        PropManager manager = PropCapabilities.get(chunk);
        manager.addProp(prop);

        ModNetworking.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                new SyncPropPacket(prop)
        );
    }

    public static boolean removeFromWorld(Prop prop, @NotNull Level level) {
        if (level.isClientSide)
            throw new IllegalStateException("Props must be removed on the logical server");

        LevelChunk chunk = level.getChunkAt(prop.getBlockPosition());
        PropManager manager = PropCapabilities.get(chunk);
        if (!manager.removeProp(prop.getId()))
            return false;

        ModNetworking.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                new SyncPropPacket(prop.getId(), prop.getBlockPosition())
        );

        return true;
    }
}
