package net.centertain.ceac.material.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.centertain.ceac.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"deprecation", "ClassCanBeRecord"})
public final class MaterialParticleOptions implements ParticleOptions {
    public static final Codec<MaterialParticleOptions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC
                            .fieldOf("texture")
                            .forGetter(MaterialParticleOptions::texture),
                    BlockPos.CODEC
                            .fieldOf("pos")
                            .forGetter(MaterialParticleOptions::pos)
            ).apply(instance, MaterialParticleOptions::new));

    public static final Deserializer<MaterialParticleOptions> DESERIALIZER =
            new Deserializer<>() {
                @Override
                public @NotNull MaterialParticleOptions fromCommand(
                        @NotNull ParticleType<MaterialParticleOptions> type,
                        @NotNull StringReader reader
                ) throws CommandSyntaxException {
                    ResourceLocation texture = ResourceLocation.read(reader);

                    reader.expect(' ');
                    int x = reader.readInt();

                    reader.expect(' ');
                    int y = reader.readInt();

                    reader.expect(' ');
                    int z = reader.readInt();

                    return new MaterialParticleOptions(
                            texture,
                            new BlockPos(x, y, z)
                    );
                }

                @Override
                public @NotNull MaterialParticleOptions fromNetwork(
                        @NotNull ParticleType<MaterialParticleOptions> type,
                        FriendlyByteBuf buffer
                ) {
                    return new MaterialParticleOptions(
                            buffer.readResourceLocation(),
                            buffer.readBlockPos()
                    );
                }
            };

    private final ResourceLocation texture;
    private final BlockPos pos;

    public MaterialParticleOptions(
            ResourceLocation texture,
            BlockPos pos
    ) {
        this.texture = texture;
        this.pos = pos;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public BlockPos pos() {
        return pos;
    }

    @Override
    public @NotNull ParticleType<MaterialParticleOptions> getType() {
        return ModParticles.MATERIAL.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(texture);
        buffer.writeBlockPos(pos);
    }

    @Override
    public @NotNull String writeToString() {
        return texture + " "
                + pos.getX() + " "
                + pos.getY() + " "
                + pos.getZ();
    }
}