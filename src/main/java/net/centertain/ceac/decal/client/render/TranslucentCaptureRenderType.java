package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.decal.client.DecalShaders;
import net.centertain.ceac.decal.client.mixin.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class TranslucentCaptureRenderType {
    private static final RenderStateShard.ShaderStateShard SHADER =
            new RenderStateShard.ShaderStateShard(DecalShaders::getTranslucentCapture);

    private static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard(
                    "ceac_no_transparency",
                    () -> {},
                    () -> {}
            );

    private static final RenderStateShard.DepthTestStateShard DEPTH_ALWAYS =
            new RenderStateShard.DepthTestStateShard("ceac_always_depth", 0x0207);

    private static final RenderType TYPE = RenderType.create(
            "ceac_translucent_capture",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDepthTestState(DEPTH_ALWAYS)
                    .setWriteMaskState(RenderStateShardAccessor.ceac$getColorWrite())
                    .createCompositeState(false)
    );

    private TranslucentCaptureRenderType() {}
}
