package net.centertain.ceac.prop.client;

import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.network.SyncPropPacket;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public final class PropClientPacketHandler {
    private PropClientPacketHandler() {}

    public static void handle(SyncPropPacket packet) {
        switch (packet.getAction()) {
            case ADD -> {
                Prop prop = Prop.deserializeNBT(packet.getData());
                if (prop != null)
                    ClientProps.put(prop);
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
