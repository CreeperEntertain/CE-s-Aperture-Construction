package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.decal.client.mixin.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class TranslucentRenderTypes {
    private static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard("ceac_no_transparency", () -> {}, () -> {});
    private static final RenderStateShard.WriteMaskStateShard DEPTH_ONLY =
            new RenderStateShard.WriteMaskStateShard(true, false);

    private static final RenderType TRANSLUCENT_DEPTH = RenderType.create(
            "ceac_translucent_depth",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShardAccessor.ceac$getTranslucentShader())
                    .setTextureState(RenderStateShardAccessor.ceac$getBlockSheetMipped())
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDepthTestState(RenderStateShardAccessor.ceac$getLequalDepthTest())
                    .setCullState(RenderStateShardAccessor.ceac$getNoCull())
                    .setLightmapState(RenderStateShardAccessor.ceac$getLightmap())
                    .setOverlayState(RenderStateShardAccessor.ceac$getOverlay())
                    .setWriteMaskState(DEPTH_ONLY)
                    .createCompositeState(false)
    );

    private TranslucentRenderTypes() {}

    public static RenderType translucentDepth() {
        return TRANSLUCENT_DEPTH;
    }
}
