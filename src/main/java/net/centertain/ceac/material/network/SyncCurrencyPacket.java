package net.centertain.ceac.material.network;

import net.centertain.ceac.material.PlayerCurrency;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCurrencyPacket {
    private final double amount;

    public SyncCurrencyPacket(double amount) {
        this.amount = amount;
    }

    public static void encode(
            SyncCurrencyPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeDouble(packet.amount);
    }

    public static SyncCurrencyPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new SyncCurrencyPacket(buffer.readDouble());
    }

    public static void handle(
            SyncCurrencyPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null)
                return;
            PlayerCurrency.set(Minecraft.getInstance().player, packet.amount);
        });

        context.setPacketHandled(true);
    }
}
