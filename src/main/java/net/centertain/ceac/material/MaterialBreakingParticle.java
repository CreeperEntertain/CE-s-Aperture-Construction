package net.centertain.ceac.material;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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
}
