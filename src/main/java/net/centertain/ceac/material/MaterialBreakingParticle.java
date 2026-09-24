package net.centertain.ceac.material;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MaterialBreakingParticle extends TerrainParticle {
    public MaterialBreakingParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            BlockState state,
            BlockPos pos,
            TextureAtlasSprite sprite
    ) {
        super(level, x, y, z, xd, yd, zd, state, pos);
        setSprite(sprite);
    }

    public static class Provider implements ParticleProvider<MaterialParticleOptions> {
        @Override
        public Particle createParticle(
                @NotNull MaterialParticleOptions options,
                @NotNull ClientLevel level,
                double x,
                double y,
                double z,
                double xd,
                double yd,
                double zd
        ) {
            BlockPos pos = options.pos();
            BlockState state = level.getBlockState(pos);

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(options.texture());

            return new MaterialBreakingParticle(
                    level,
                    x,
                    y,
                    z,
                    xd,
                    yd,
                    zd,
                    state,
                    pos,
                    sprite
            ).setPower(0.2f).scale(0.6f);
        }
    }
}
