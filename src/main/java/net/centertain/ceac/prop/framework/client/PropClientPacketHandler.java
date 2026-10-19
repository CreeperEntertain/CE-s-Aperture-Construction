package net.centertain.ceac.prop.framework.client;

import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.network.SyncPropPacket;
import net.centertain.ceac.prop.framework.network.SyncPropTransformPacket;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public final class PropClientPacketHandler {
    private PropClientPacketHandler() {}

    public static void handle(SyncPropTransformPacket packet) {
        for (SyncPropTransformPacket.Transform transform : packet.getTransforms()) {
            ClientProps.updateTransform(
                    transform.propId(),
                    transform.x(),
                    transform.y(),
                    transform.z(),
                    transform.qx(),
                    transform.qy(),
                    transform.qz(),
                    transform.qw()
            );
        }
    }

    public static void handle(SyncPropPacket packet) {
        switch (packet.getAction()) {
            case ADD -> {
                Prop prop = Prop.deserializeNBT(packet.getData());
                if (prop != null)
                    ClientProps.put(prop);
            } case REMOVE -> {
                if (packet.getData().hasUUID("Id"))
                    ClientProps.remove(packet.getData().getUUID("Id"));
            } case CHUNK_SNAPSHOT -> {
                List<Prop> props = new ArrayList<>();
                ListTag list = packet.getData().getList("Props", Tag.TAG_COMPOUND);

                for (int i = 0; i < list.size(); i++) {
                    Prop prop = Prop.deserializeNBT(list.getCompound(i));
                    if (prop != null)
                        props.add(prop);
                }

                ClientProps.replaceChunk(
                        packet.getChunkX(),
                        packet.getChunkZ(),
                        props
                );
            }
        }
    }
}
