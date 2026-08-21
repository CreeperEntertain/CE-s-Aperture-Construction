package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.decal.client.mixin.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class TranslucentRenderTypes {
    private static final RenderStateShard.ShaderStateShard CEAC_TRANSLUCENT_DEPTH_SHADER =
            new RenderStateShard.ShaderStateShard(DecalShaders::getTranslucentCaptureShader);
    private static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard("ceac_no_transparency", () -> {}, () -> {});
    private static final RenderStateShard.WriteMaskStateShard COLOR_AND_DEPTH =
            new RenderStateShard.WriteMaskStateShard(true, true);

    private static final RenderType TRANSLUCENT_DEPTH = RenderType.create(
            "ceac_translucent_depth",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            262144,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(CEAC_TRANSLUCENT_DEPTH_SHADER)
                    .setTextureState(RenderStateShardAccessor.ceac$getBlockSheetMipped())
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDepthTestState(RenderStateShardAccessor.ceac$getLequalDepthTest())
                    .setCullState(RenderStateShardAccessor.ceac$getNoCull())
                    .setLightmapState(RenderStateShardAccessor.ceac$getLightmap())
                    .setOverlayState(RenderStateShardAccessor.ceac$getOverlay())
                    .setWriteMaskState(COLOR_AND_DEPTH)
                    .createCompositeState(false)
    );

    private TranslucentRenderTypes() {}

    public static RenderType translucentDepth() {
        return TRANSLUCENT_DEPTH;
    }
}
