package net.centertain.ceac.material.network;

import net.centertain.ceac.constants.PriceConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SellItemPacket {
    private final ResourceLocation itemId;
    private final int amount;

    public SellItemPacket(
            Item item,
            int amount
    ) {
        this.itemId = ForgeRegistries.ITEMS.getKey(item);
        this.amount = amount;
    }

    public static void encode(
            SellItemPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeResourceLocation(packet.itemId);
        buffer.writeInt(packet.amount);
    }

    public static SellItemPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new SellItemPacket(
                ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation()),
                buffer.readInt()
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

            player.inventoryMenu.broadcastChanges();
        });

        context.setPacketHandled(true);
    }
}
