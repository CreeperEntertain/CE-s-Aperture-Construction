package net.centertain.ceac.prop.network;

import net.centertain.ceac.prop.client.PropBreakParticlesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public final class PropBreakParticlesPacket {
    private static final int MAX_PARTICLES = 1000;

    private final ResourceLocation modelId;
    private final float[] transform;
    private final int count;
    private final long seed;

    public PropBreakParticlesPacket(
            ResourceLocation modelId,
            Matrix4f transform,
            int count,
            long seed
    ) {
        this.modelId = modelId;
        this.transform = new float[16];
        transform.get(this.transform);
        this.count = Math.max(0, Math.min(MAX_PARTICLES, count));
        this.seed = seed;
    }

    private PropBreakParticlesPacket(
            ResourceLocation modelId,
            float[] transform,
            int count,
            long seed
    ) {
        this.modelId = modelId;
        this.transform = transform;
        this.count = Math.max(0, Math.min(MAX_PARTICLES, count));
        this.seed = seed;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }
    public Matrix4f getTransform() {
        return new Matrix4f().set(transform);
    }
    public int getCount() {
        return count;
    }
    public long getSeed() {
        return seed;
    }

    public static void encode(
            PropBreakParticlesPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeResourceLocation(packet.modelId);
        for (float value : packet.transform)
            buffer.writeFloat(value);
        buffer.writeVarInt(packet.count);
        buffer.writeLong(packet.seed);
    }

    public static PropBreakParticlesPacket decode(FriendlyByteBuf buffer) {
        ResourceLocation modelId = buffer.readResourceLocation();
        float[] transform = new float[16];
        for (int i = 0; i < transform.length; i++)
            transform[i] = buffer.readFloat();
        int count = buffer.readVarInt();
        long seed = buffer.readLong();

        return new PropBreakParticlesPacket(modelId, transform, count, seed);
    }

    public static void handle(
            PropBreakParticlesPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> PropBreakParticlesClient.spawn(packet)
        ));

        context.setPacketHandled(true);
    }
}
