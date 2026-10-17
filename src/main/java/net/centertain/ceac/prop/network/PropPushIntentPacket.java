package net.centertain.ceac.prop.network;

import net.centertain.ceac.prop.physics.PropPhysManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public final class PropPushIntentPacket {
    private final UUID propId;
    private final float velocityX;
    private final float velocityZ;

    public PropPushIntentPacket(
            UUID propId,
            float velocityX,
            float velocityZ
    ) {
        this.propId = propId;
        this.velocityX = velocityX;
        this.velocityZ = velocityZ;
    }

    public static void encode(
            PropPushIntentPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeUUID(packet.propId);
        buffer.writeFloat(packet.velocityX);
        buffer.writeFloat(packet.velocityZ);
    }

    public static PropPushIntentPacket decode(FriendlyByteBuf buffer) {
        return new PropPushIntentPacket(
                buffer.readUUID(),
                buffer.readFloat(),
                buffer.readFloat()
        );
    }

    public static void handle(
            PropPushIntentPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;

            PropPhysManager.applyPlayerPush(
                    player,
                    packet.propId,
                    new Vec3(
                            packet.velocityX,
                            0.0,
                            packet.velocityZ
                    )
            );
        });

        context.setPacketHandled(true);
    }
}
