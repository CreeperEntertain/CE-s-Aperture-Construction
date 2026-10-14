package net.centertain.ceac.prop.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public final class PropBreakParticle extends TextureSheetParticle {
    private final float uo;
    private final float vo;

    public PropBreakParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            TextureAtlasSprite sprite,
            float shade,
            RandomSource random
    ) {
        super(level, x, y, z);

        setSprite(sprite);
        setColor(shade, shade, shade);

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.gravity = 1.0f;
        this.quadSize = 0.035f + random.nextFloat() * 0.065f;
        this.lifetime = 12 + random.nextInt(13);

        this.uo = random.nextInt(4);
        this.vo = random.nextInt(4);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1) / 4.0);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1) / 4.0);
    }
}
