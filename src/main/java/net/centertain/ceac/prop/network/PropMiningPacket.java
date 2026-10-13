package net.centertain.ceac.prop.network;

import net.centertain.ceac.prop.server.PropMiningManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public final class PropMiningPacket {
    public enum Action {
        START,
        CONTINUE,
        ABORT
    }

    private final Action action;
    private final UUID propId;

    public PropMiningPacket(Action action, UUID propId) {
        this.action = action;
        this.propId = propId;
    }

    public Action getAction() {
        return action;
    }
    public UUID getPropId() {
        return propId;
    }

    public static void encode(
            PropMiningPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeEnum(packet.action);
        buffer.writeUUID(packet.propId);
    }

    public static PropMiningPacket decode(FriendlyByteBuf buffer) {
        return new PropMiningPacket(
                buffer.readEnum(Action.class),
                buffer.readUUID()
        );
    }

    public static void handle(
            PropMiningPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null)
                PropMiningManager.handle(player, packet);
        });

        context.setPacketHandled(true);
    }
}
