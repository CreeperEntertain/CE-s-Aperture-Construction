package net.centertain.ceac.material.network;

import net.centertain.ceac.constants.PriceConstants;
import net.centertain.ceac.material.PlayerCurrency;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.sound.ModSounds;
import net.centertain.ceac.utility.Mathworks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SellItemPacket {
    private final ResourceLocation itemId;
    private final int amount;
    private final BlockPos pos;

    public SellItemPacket(
            Item item,
            int amount,
            BlockPos pos
    ) {
        this.itemId = ForgeRegistries.ITEMS.getKey(item);
        this.amount = amount;
        this.pos = pos;
    }

    public static void encode(
            SellItemPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeResourceLocation(packet.itemId);
        buffer.writeInt(packet.amount);
        buffer.writeBlockPos(packet.pos);
    }

    public static SellItemPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new SellItemPacket(
                ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()),
                buffer.readInt(),
                buffer.readBlockPos()
        );
    }

    public static void handle(
            SellItemPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            if (packet.amount <= 0)
                return;

            Item item = ForgeRegistries.ITEMS.getValue(packet.itemId);
            if (item == null)
                return;
            if (item == Items.AIR)
                return;
            if (player.getInventory().countItem(item) < packet.amount)
                return;

            Double worth = PriceConstants.get(item);
            if (worth == null)
                return;

            int removed = player.getInventory().clearOrCountMatchingItems(
                    stack -> stack.is(item),
                    packet.amount,
                    player.getInventory()
            );
            if (removed != packet.amount)
                return;

            PlayerCurrency.increase(player, worth * packet.amount);

            ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncCurrencyPacket(PlayerCurrency.get(player)));

            player.level().playSound(
                    null,
                    packet.pos,
                    ModSounds.PURCHASING_TERMINAL_SELL.get(),
                    SoundSource.BLOCKS,
                    Mathworks.randomBetween(1.8f, 2.2f),
                    Mathworks.randomBetween(0.9f, 1.1f)
            );

            player.inventoryMenu.broadcastChanges();
        });

        context.setPacketHandled(true);
    }
}
