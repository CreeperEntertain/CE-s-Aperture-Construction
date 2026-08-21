package net.centertain.ceac.decal.client.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
    @Accessor("BLOCK_SHEET_MIPPED")
    static RenderStateShard.TextureStateShard ceac$getBlockSheetMipped() {
        throw new AssertionError();
    }

    @Accessor("LEQUAL_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard ceac$getLequalDepthTest() {
        throw new AssertionError();
    }

    @Accessor("NO_CULL")
    static RenderStateShard.CullStateShard ceac$getNoCull() {
        throw new AssertionError();
    }

    @Accessor("LIGHTMAP")
    static RenderStateShard.LightmapStateShard ceac$getLightmap() {
        throw new AssertionError();
    }

    @Accessor("OVERLAY")
    static RenderStateShard.OverlayStateShard ceac$getOverlay() {
        throw new AssertionError();
    }
}
