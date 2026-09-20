package net.centertain.ceac.material;

import net.centertain.ceac.item.custom.MatItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector2i;

import java.util.function.Supplier;

public class MaterialOffsetPacket {
    private final InteractionHand hand;
    private final int x;
    private final int y;

    public MaterialOffsetPacket(
            InteractionHand hand,
            Vector2i offset
    ) {
        this.hand = hand;
        this.x = offset.x;
        this.y = offset.y;
    }

    public static void encode(
            MaterialOffsetPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeEnum(packet.hand);
        buffer.writeInt(packet.x);
        buffer.writeInt(packet.y);
    }

    public static MaterialOffsetPacket decode(
            FriendlyByteBuf buffer
    ) {
        InteractionHand hand = buffer.readEnum(InteractionHand.class);

        int x = buffer.readInt();
        int y = buffer.readInt();

        return new MaterialOffsetPacket(hand, new Vector2i(x, y));
    }

    public static void handle(
            MaterialOffsetPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            ItemStack stack = player.getItemInHand(packet.hand);
            if (!(stack.getItem() instanceof MatItem matItem))
                return;
            matItem.setMaterialCoordinateOffset(stack, new Vector2i(packet.x, packet.y));
        });

        context.setPacketHandled(true);
    }
}
