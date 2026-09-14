package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

public final class CeacRenderTypes {
    public static final RenderType IN_WORLD_UI = RenderType.create(
            "in_world_ui",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            GameRenderer::getPositionColorShader
                    ))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard(
                            "translucent",
                            () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                            },
                            RenderSystem::disableBlend
                    ))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
                            "always",
                            GL11.GL_ALWAYS
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
                            true, false
                    ))
                    .createCompositeState(false)
    );
}
