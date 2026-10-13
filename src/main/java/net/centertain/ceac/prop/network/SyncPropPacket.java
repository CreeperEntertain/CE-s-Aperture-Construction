package net.centertain.ceac.prop.network;

import net.centertain.ceac.material.network.SyncCurrencyPacket;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.client.PropClientPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public final class SyncPropPacket {
    public enum Action {
        ADD,
        REMOVE,
        CHUNK_SNAPSHOT
    }

    private final Action action;
    private final int chunkX;
    private final int chunkZ;
    private final CompoundTag data;

    public SyncPropPacket(Prop prop) {
        ChunkPos pos = new ChunkPos(prop.getBlockPosition());

        this.action = Action.ADD;
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
        this.data = prop.serializeNBT();
    }

    public SyncPropPacket(UUID propId, BlockPos position) {
        ChunkPos pos = new ChunkPos(position);

        this.action = Action.REMOVE;
        this.chunkX = pos.x;
        this.chunkZ = pos.z;

        this.data = new CompoundTag();
        this.data.putUUID("Id", propId);
    }

    public SyncPropPacket(ChunkPos pos, Collection<Prop> props) {
        this.action = Action.CHUNK_SNAPSHOT;
        this.chunkX = pos.x;
        this.chunkZ = pos.z;

        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();

        for (Prop prop : props)
            list.add(prop.serializeNBT());

        root.put("Props", list);
        this.data = root;
    }

    private SyncPropPacket(
            Action action,
            int chunkX,
            int chunkZ,
            CompoundTag data
    ) {
        this.action = action;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.data = data;
    }

    public Action getAction() {
        return action;
    }
    public int getChunkX() {
        return chunkX;
    }
    public int getChunkZ() {
        return chunkZ;
    }
    public CompoundTag getData() {
        return data;
    }

    public static void encode(
            SyncPropPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeEnum(packet.action);
        buffer.writeVarInt(packet.chunkX);
        buffer.writeVarInt(packet.chunkZ);
        buffer.writeNbt(packet.data);
    }

    public static SyncPropPacket decode(FriendlyByteBuf buffer) {
        Action action = buffer.readEnum(Action.class);
        int chunkX = buffer.readVarInt();
        int chunkZ = buffer.readVarInt();
        CompoundTag data = buffer.readNbt();

        return new SyncPropPacket(
                action,
                chunkX,
                chunkZ,
                data == null ? new CompoundTag() : data
        );
    }

    public static void handle(
            SyncPropPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        var context = contextSupplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> PropClientPacketHandler.handle(packet)
        ));

        context.setPacketHandled(true);
    }
}
