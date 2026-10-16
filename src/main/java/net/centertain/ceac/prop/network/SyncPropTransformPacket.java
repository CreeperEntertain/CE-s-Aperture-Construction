package net.centertain.ceac.prop.network;

import net.centertain.ceac.prop.client.PropClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public final class SyncPropTransformPacket {
    public static final int MAX_TRANSFORMS_PER_PACKET = 65_536;

    public record Transform(
            UUID propId,
            float x,
            float y,
            float z,
            float qx,
            float qy,
            float qz,
            float qw
    ) {}

    private final List<Transform> transforms;

    public SyncPropTransformPacket(List<Transform> transforms) {
        if (transforms.isEmpty())
            throw new IllegalArgumentException("Transform batch cannot be empty");
        if (transforms.size() > MAX_TRANSFORMS_PER_PACKET)
            throw new IllegalArgumentException("Transform batch is too large");
        this.transforms = List.copyOf(transforms);
    }

    public List<Transform> getTransforms() {
        return transforms;
    }


    public static void encode(
            SyncPropTransformPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeVarInt(packet.transforms.size());

        for (Transform transform : packet.transforms) {
            buffer.writeUUID(transform.propId());
            buffer.writeFloat(transform.x());
            buffer.writeFloat(transform.y());
            buffer.writeFloat(transform.z());
            buffer.writeFloat(transform.qx());
            buffer.writeFloat(transform.qy());
            buffer.writeFloat(transform.qz());
            buffer.writeFloat(transform.qw());
        }
    }

    public static SyncPropTransformPacket decode(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        if (count < 1 || count > MAX_TRANSFORMS_PER_PACKET)
            throw new IllegalArgumentException("Invalid transform batch size: " + count);

        List<Transform> transforms = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            transforms.add(new Transform(
                    buffer.readUUID(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat()
            ));
        }

        return new SyncPropTransformPacket(transforms);
    }

    public static void handle(
            SyncPropTransformPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> PropClientPacketHandler.handle(packet)
        ));

        context.setPacketHandled(true);
    }
}
