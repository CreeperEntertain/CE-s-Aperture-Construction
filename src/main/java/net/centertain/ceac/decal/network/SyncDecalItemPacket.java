package net.centertain.ceac.decal.network;

import net.centertain.ceac.item.custom.DecalItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDecalItemPacket {
    public final InteractionHand hand;
    public final ResourceLocation texture;

    public SyncDecalItemPacket(
            InteractionHand hand,
            ResourceLocation texture
    ) {
        this.hand = hand;
        this.texture = texture;
    }

    public static void encode(
            SyncDecalItemPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeEnum(packet.hand);
        buffer.writeResourceLocation(packet.texture);
    }

    public static SyncDecalItemPacket decode(FriendlyByteBuf buffer) {
        return new SyncDecalItemPacket(
                buffer.readEnum(InteractionHand.class),
                buffer.readResourceLocation()
        );
    }

    public static void handle(
            SyncDecalItemPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            ItemStack stack = player.getItemInHand(packet.hand);
            if (!(stack.getItem() instanceof DecalItem))
                return;
            stack.getOrCreateTag().putString("SelectedTexture", packet.texture.toString());
        });
        context.setPacketHandled(true);
    }
}
