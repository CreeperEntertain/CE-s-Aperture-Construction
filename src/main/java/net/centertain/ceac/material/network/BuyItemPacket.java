package net.centertain.ceac.material.network;

import net.centertain.ceac.block.custom.BasicBlock;
import net.centertain.ceac.item.custom.BasicItem;
import net.centertain.ceac.material.PlayerCurrency;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.sound.ModSounds;
import net.centertain.ceac.utility.Mathworks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BuyItemPacket {
    private final ResourceLocation itemId;
    private final int amount;
    private final BlockPos pos;

    public BuyItemPacket(
            Item item,
            int amount,
            BlockPos pos
    ) {
        this.itemId = ForgeRegistries.ITEMS.getKey(item);
        this.amount = amount;
        this.pos = pos;
    }

    public static void encode(
            BuyItemPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeResourceLocation(packet.itemId);
        buffer.writeInt(packet.amount);
        buffer.writeBlockPos(packet.pos);
    }

    public static BuyItemPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new BuyItemPacket(
                ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()),
                buffer.readInt(),
                buffer.readBlockPos()
        );
    }

    public static void handle(
            BuyItemPacket packet,
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

            Double price = null;
            if (item instanceof BasicItem basicItem)
                price = basicItem.getPrice();
            else if (item instanceof BlockItem blockItem)
                if (blockItem.getBlock() instanceof BasicBlock basicBlock)
                    price = basicBlock.getPrice();
            if (price == null)
                return;
            if (PlayerCurrency.get(player) < packet.amount * price)
                return;

            ItemStack stack = new ItemStack(item, packet.amount);
            if (!player.addItem(stack))
                player.drop(stack, false);

            PlayerCurrency.decrease(player, packet.amount * price);

            ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncCurrencyPacket(PlayerCurrency.get(player)));

            player.level().playSound(
                    null,
                    packet.pos,
                    ModSounds.PURCHASING_TERMINAL_PURCHASE.get(),
                    SoundSource.BLOCKS,
                    Mathworks.randomBetween(1.8f, 2.2f),
                    Mathworks.randomBetween(0.9f, 1.1f)
            );

            player.inventoryMenu.broadcastChanges();
        });

        context.setPacketHandled(true);
    }
}
