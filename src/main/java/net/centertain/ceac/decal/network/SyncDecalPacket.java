package net.centertain.ceac.decal.network;

import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.client.ClientDecals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncDecalPacket {
    public enum Action {
        ADD,
        REMOVE
    }

    private final Action action;
    private final CompoundTag data;

    public SyncDecalPacket(Decal decal) {
        this.action = Action.ADD;
        this.data = decal.serializeNBT();
    }
    public SyncDecalPacket(UUID id) {
        this.action = Action.REMOVE;
        this.data = new CompoundTag();
        this.data.putUUID("Id", id);
    }
    private SyncDecalPacket(Action action, CompoundTag data) {
        this.action = action;
        this.data = data;
    }

    public static void encode(SyncDecalPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.action);
        buffer.writeNbt(packet.data);
    }
    public static SyncDecalPacket decode(FriendlyByteBuf buffer) {
        Action action = buffer.readEnum(Action.class);
        CompoundTag data = buffer.readNbt();
        return new SyncDecalPacket(action, data);
    }

    public static void handle(SyncDecalPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            switch (packet.action) {
                case ADD -> handleAddition(packet.data);
                case REMOVE -> handleRemoval(packet.data);
            }
        });
        context.setPacketHandled(true);
    }
    private static void handleAddition(CompoundTag data) {
        Decal decal = Decal.deserializeNBT(data);
        if (decal != null) {
            ClientDecals.put(decal);
            System.out.println("Client received decal " + decal.getId());
        }
    }
    private static void handleRemoval(CompoundTag data) {
        UUID id = data.getUUID("Id");
        ClientDecals.remove(id);
        System.out.println("Client removed decal " + id);
    }
}