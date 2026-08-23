package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public final class TranslucentCaptureState {
    private static boolean active;

    private TranslucentCaptureState() {}

    public static void begin() {
        active = true;
        TranslucentKBuffer.clear();
        TranslucentKBuffer.bind();
    }

    public static void end() {
        try {
            TranslucentKBuffer.barrier();

            DecalShaders.sortKBuffer(
                    TranslucentKBuffer.getWidth(),
                    TranslucentKBuffer.getHeight()
            );

            TranslucentKBuffer.barrier();

            ShaderInstance shader = DecalShaders.getKBufferComposite();
            if (shader == null)
                return;

            Minecraft minecraft = Minecraft.getInstance();
            RenderTarget target = minecraft.getMainRenderTarget();

            TranslucentKBuffer.bind();
            TranslucentKBuffer.setShaderUniforms(shader);

            target.bindWrite(false);

            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderTexture(0, DecalRenderer.getOpaqueDepthTexture());

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            BufferBuilder buffer = Tesselator.getInstance().getBuilder();

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

            buffer.vertex(-1.0, -1.0, 0.0).endVertex();
            buffer.vertex(1.0, -1.0, 0.0).endVertex();
            buffer.vertex(1.0, 1.0, 0.0).endVertex();
            buffer.vertex(-1.0, 1.0, 0.0).endVertex();

            BufferUploader.drawWithShader(buffer.end());

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        } finally {
            active = false;
        }
    }

    public static boolean isActive() {
        return active;
    }
}
