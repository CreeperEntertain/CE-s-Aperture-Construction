package net.centertain.ceac.decal.network;

import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.server.DecalCapabilities;
import net.centertain.ceac.decal.server.DecalManager;
import net.centertain.ceac.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DecalSyncEvents {
    private DecalSyncEvents() {}

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerPlayer player = event.getPlayer();
        LevelChunk chunk = event.getChunk();
        if (chunk == null)
            return;
        DecalManager manager = DecalCapabilities.get(chunk);
        for (Decal decal : manager.getDecals().values()) {
            ModNetworking.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncDecalPacket(decal)
            );
        }
    }
    @SubscribeEvent
    public static void onChunkUnwatch(ChunkWatchEvent.UnWatch event) {
        ServerPlayer player = event.getPlayer();
        Level level = event.getLevel();
        LevelChunk chunk = level.getChunk(event.getPos().x, event.getPos().z);
        DecalManager manager = DecalCapabilities.get(chunk);
        for (Decal decal : manager.getDecals().values()) {
            ModNetworking.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SyncDecalPacket(decal.getId())
            );
        }
    }
}
