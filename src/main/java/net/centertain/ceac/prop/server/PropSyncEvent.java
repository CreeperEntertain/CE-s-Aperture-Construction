package net.centertain.ceac.prop.server;

import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.network.SyncPropPacket;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PropSyncEvent {
    private PropSyncEvent() {}

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        var chunk = event.getChunk();
        var manager = PropCapabilities.get(chunk);

        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(event::getPlayer),
                new SyncPropPacket(chunk.getPos(), manager.getProps().values())
        );
    }
}
