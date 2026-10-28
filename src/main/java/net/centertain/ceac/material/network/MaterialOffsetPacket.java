package net.centertain.ceac.material.network;

import net.centertain.ceac.item.custom.types.MatItem;
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
    private final int rotation;
    private final boolean textureLocked;
    private final int lockedX;
    private final int lockedY;

    public MaterialOffsetPacket(
            InteractionHand hand,
            Vector2i offset,
            int rotation,
            boolean textureLocked,
            Vector2i lockedCoordinate
    ) {
        this.hand = hand;
        this.x = offset.x;
        this.y = offset.y;
        this.rotation = Math.floorMod(rotation, 4);
        this.textureLocked = textureLocked;
        this.lockedX = lockedCoordinate.x;
        this.lockedY = lockedCoordinate.y;
    }

    public static void encode(
            MaterialOffsetPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeEnum(packet.hand);
        buffer.writeInt(packet.x);
        buffer.writeInt(packet.y);
        buffer.writeInt(packet.rotation);
        buffer.writeBoolean(packet.textureLocked);
        buffer.writeInt(packet.lockedX);
        buffer.writeInt(packet.lockedY);
    }

    public static MaterialOffsetPacket decode(
            FriendlyByteBuf buffer
    ) {
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        int x = buffer.readInt();
        int y = buffer.readInt();
        int rotation = buffer.readInt();
        boolean textureLocked = buffer.readBoolean();
        int lockedX = buffer.readInt();
        int lockedY = buffer.readInt();

        return new MaterialOffsetPacket(
                hand,
                new Vector2i(x, y),
                rotation,
                textureLocked,
                new Vector2i(lockedX, lockedY)
        );
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
            matItem.setMaterialRotation(stack, packet.rotation);
            matItem.setLockedMaterialCoordinate(stack, new Vector2i(packet.lockedX, packet.lockedY));
            matItem.setTextureLocked(stack, packet.textureLocked);
        });

        context.setPacketHandled(true);
    }
}
