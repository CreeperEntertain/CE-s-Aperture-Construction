package net.centertain.ceac.prop.network;

import net.centertain.ceac.prop.client.PropClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public final class SyncPropTransformPacket {
    private final UUID propId;

    private final float x;
    private final float y;
    private final float z;

    private final float qx;
    private final float qy;
    private final float qz;
    private final float qw;

    public SyncPropTransformPacket(
            UUID propId,
            float x,
            float y,
            float z,
            float qx,
            float qy,
            float qz,
            float qw
    ) {
        this.propId = propId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
        this.qw = qw;
    }

    public UUID getPropId() {
        return propId;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getZ() {
        return z;
    }
    public float getQx() {
        return qx;
    }
    public float getQy() {
        return qy;
    }
    public float getQz() {
        return qz;
    }
    public float getQw() {
        return qw;
    }

    public static void encode(
            SyncPropTransformPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeUUID(packet.propId);

        buffer.writeFloat(packet.x);
        buffer.writeFloat(packet.y);
        buffer.writeFloat(packet.z);

        buffer.writeFloat(packet.qx);
        buffer.writeFloat(packet.qy);
        buffer.writeFloat(packet.qz);
        buffer.writeFloat(packet.qw);
    }

    public static SyncPropTransformPacket decode(
            FriendlyByteBuf buffer
    ) {
        UUID propId = buffer.readUUID();

        float x = buffer.readFloat();
        float y = buffer.readFloat();
        float z = buffer.readFloat();

        float qx = buffer.readFloat();
        float qy = buffer.readFloat();
        float qz = buffer.readFloat();
        float qw = buffer.readFloat();

        return new SyncPropTransformPacket(propId, x, y, z, qx, qy, qz, qw);
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
