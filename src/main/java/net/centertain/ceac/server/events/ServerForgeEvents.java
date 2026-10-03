package net.centertain.ceac.server.events;

import net.centertain.ceac.material.PlayerCurrency;
import net.centertain.ceac.material.network.SyncCurrencyPacket;
import net.centertain.ceac.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ServerForgeEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        PlayerCurrency.initialize(player);

        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncCurrencyPacket(PlayerCurrency.get(player))
        );
    }
}