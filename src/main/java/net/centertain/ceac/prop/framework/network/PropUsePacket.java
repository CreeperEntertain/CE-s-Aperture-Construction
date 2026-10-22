package net.centertain.ceac.prop.framework.network;

import net.centertain.ceac.prop.framework.server.PropInteractionManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class PropUsePacket {
    private final UUID propId;
    private final InteractionHand hand;

    public PropUsePacket(UUID propId, InteractionHand hand) {
        this.propId = propId;
        this.hand = hand;
    }

    public UUID getPropId() {
        return propId;
    }
    public InteractionHand getHand() {
        return hand;
    }


    public static void encode(PropUsePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.propId);
        buffer.writeEnum(packet.hand);
    }

    public static PropUsePacket decode(FriendlyByteBuf buffer) {
        return new PropUsePacket(
                buffer.readUUID(),
                buffer.readEnum(InteractionHand.class)
        );
    }

    public static void handle(
            PropUsePacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null)
                PropInteractionManager.handle(player, packet);
        });

        context.setPacketHandled(true);
    }
}
