package net.centertain.ceac.prop.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.client.ClientProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public final class PropRenderer {
    private static final Map<ResourceLocation, BakedModel> MODEL_CACHE = new HashMap<>();

    private PropRenderer() {}

    public static void clearModelCache() {
        MODEL_CACHE.clear();
    }

    public static void render(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null)
            return;

        Vec3 camera = event.getCamera().getPosition();
        ChunkPos center = new ChunkPos(BlockPos.containing(camera));

        int chunkRadius = minecraft.options.renderDistance().get() + 1;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        VertexConsumer consumer = buffers.getBuffer(RenderType.solid());
        ModelBlockRenderer modelRenderer = minecraft.getBlockRenderer().getModelRenderer();
        ModelManager modelManager = minecraft.getModelManager();

        for (int chunkX = center.x - chunkRadius; chunkX <= center.x + chunkRadius; chunkX++)
            for (int chunkZ = center.z - chunkRadius; chunkZ <= center.z + chunkRadius; chunkZ++)
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ)) {
                    Vec3 position = prop.getPosition();
                    Vec3 scale = prop.getScale();
                    // Assumes prop geometry is authored in block-sized units
                    // TODO: Make PropDefinition and let it provide this later
                    double radius = Math.max(1.5, Math.max(
                            Math.abs(scale.x),
                            Math.max(Math.abs(scale.y), Math.abs(scale.z))
                    ) * 1.5);
                    AABB bounds = new AABB(
                            position.x - radius,
                            position.y - radius,
                            position.z - radius,
                            position.x + radius,
                            position.y + radius,
                            position.z + radius
                    );
                    if (!minecraft.levelRenderer.getFrustum().isVisible(bounds))
                        continue;

                    BakedModel model = MODEL_CACHE.computeIfAbsent(prop.getDefinition(), modelManager::getModel);

                    poseStack.pushPose();

                    poseStack.translate(
                            position.x - camera.x,
                            position.y - camera.y,
                            position.z - camera.z
                    );

                    Quaternionf rotation = prop.getRotationQuat().normalize();
                    poseStack.mulPose(rotation);

                    poseStack.scale(
                            (float) scale.x,
                            (float) scale.y,
                            (float) scale.z
                    );

                    int light = LevelRenderer.getLightColor(level, prop.getBlockPosition());

                    modelRenderer.renderModel(
                            poseStack.last(),
                            consumer,
                            null,
                            model,
                            1.0f,
                            1.0f,
                            1.0f,
                            light,
                            OverlayTexture.NO_OVERLAY,
                            ModelData.EMPTY,
                            RenderType.solid()
                    );

                    poseStack.popPose();
                }

        buffers.endBatch(RenderType.solid());
    }
}
