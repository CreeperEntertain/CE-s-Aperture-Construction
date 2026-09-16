package net.centertain.ceac.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

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

    public final RenderType inWorldText(ResourceLocation texture) {
        return RenderType.create(
                "in_world_text",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(
                                GameRenderer::getRendertypeTextShader
                        ))
                        .setTextureState(new RenderStateShard.TextureStateShard(
                                texture,
                                false,
                                false
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
                        .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                        .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
                                true, false
                        ))
                        .createCompositeState(false)
        );
    }

//    public static final RenderType IN_WORLD_UI_TEXTURED = RenderType.create(
//            "in_world_ui_textured",
//            DefaultVertexFormat.POSITION_COLOR_TEX,
//            VertexFormat.Mode.QUADS,
//            256,
//            false,
//            true,
//            RenderType.CompositeState.builder()
//                    .setShaderState(new RenderStateShard.ShaderStateShard(
//                            GameRenderer::getPositionColorTexShader
//                    ))
//                    .setTextureState(new RenderStateShard.TextureStateShard(
//                            null,
//                            false,
//                            false
//                    ))
//                    .setTransparencyState(new RenderStateShard.TransparencyStateShard(
//                            "translucent",
//                            () -> {
//                                RenderSystem.enableBlend();
//                                RenderSystem.defaultBlendFunc();
//                            },
//                            RenderSystem::disableBlend
//                    ))
//                    .setDepthTestState(new RenderStateShard.DepthTestStateShard(
//                            "always",
//                            GL11.GL_ALWAYS
//                    ))
//                    .setCullState(new RenderStateShard.CullStateShard(false))
//                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
//                            true, false
//                    ))
//                    .createCompositeState(false)
//    );
}
