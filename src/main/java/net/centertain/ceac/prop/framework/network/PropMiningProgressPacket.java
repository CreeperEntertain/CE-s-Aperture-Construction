package net.centertain.ceac.prop.framework.network;

import net.centertain.ceac.prop.framework.client.ClientPropMiningProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public final class PropMiningProgressPacket {
    private final UUID propId;
    private final int stage;

    public PropMiningProgressPacket(UUID propId, int stage) {
        this.propId = propId;
        this.stage = stage;
    }

    public UUID getPropId() {
        return propId;
    }
    public int getStage() {
        return stage;
    }

    public static void encode(
            PropMiningProgressPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeUUID(packet.propId);
        buffer.writeInt(packet.stage);
    }

    public static PropMiningProgressPacket decode(FriendlyByteBuf buffer) {
        return new PropMiningProgressPacket(
                buffer.readUUID(),
                buffer.readInt()
        );
    }

    public static void handle(
            PropMiningProgressPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> ClientPropMiningProgress.setStage(
                        packet.propId,
                        packet.stage
                )
        ));

        context.setPacketHandled(true);
    }
}
