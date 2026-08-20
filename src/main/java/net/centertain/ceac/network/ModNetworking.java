package net.centertain.ceac.network;

import net.centertain.ceac.decal.network.SyncDecalPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );
    private static int packetId = 0;

    private ModNetworking() {}

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                SyncDecalPacket.class,
                SyncDecalPacket::encode,
                SyncDecalPacket::decode,
                SyncDecalPacket::handle
        );
    }
}
