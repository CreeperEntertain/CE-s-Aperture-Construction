package net.centertain.ceac.prop.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.client.ClientProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.*;

// TODO: Make prop rendering support cutout mipped, potentially translucent
public final class PropRenderer {
    private static final Map<ResourceLocation, BakedModel> MODEL_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, Set<Edge>> OUTLINE_EDGE_CACHE = new HashMap<>();

    private PropRenderer() {}

    public static void clearModelCache() {
        MODEL_CACHE.clear();
        OUTLINE_EDGE_CACHE.clear();
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
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ))
                    handleProp(prop, minecraft, modelManager, poseStack, camera, level, modelRenderer, consumer);

        buffers.endBatch(RenderType.solid());
    }

    private static void handleProp(
            Prop prop,
            Minecraft minecraft,
            ModelManager modelManager,
            PoseStack poseStack,
            Vec3 camera,
            ClientLevel level,
            ModelBlockRenderer modelRenderer,
            VertexConsumer consumer
    ) {
        Vec3 position = prop.getPosition();
        Vec3 scale = prop.getScale();
        // Assumes prop geometry is authored in block-sized units
        // TODO: Make PropDefinition and let it provide this later
        AABB bounds = getBounds(scale, position);
        if (!minecraft.levelRenderer.getFrustum().isVisible(bounds))
            return;

        PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
        if (definition == null)
            return;

        BakedModel model = MODEL_CACHE.computeIfAbsent(
                definition.getModelId(),
                modelManager::getModel
        );

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

        modelRenderer.tesselateBlock(
                level,
                model,
                Blocks.AIR.defaultBlockState(),
                prop.getBlockPosition(),
                poseStack,
                consumer,
                false,
                RandomSource.create(),
                prop.getId().getMostSignificantBits(),
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.solid()
        );

        poseStack.popPose();
    }

    private static @NotNull AABB getBounds(Vec3 scale, Vec3 position) {
        double radius = Math.max(1.5, Math.max(
                Math.abs(scale.x),
                Math.max(Math.abs(scale.y), Math.abs(scale.z))
        ) * 1.5);
        return new AABB(
                position.x - radius,
                position.y - radius,
                position.z - radius,
                position.x + radius,
                position.y + radius,
                position.z + radius
        );
    }

    public static void renderOutline(RenderHighlightEvent.Block event, Prop prop) {
        Minecraft minecraft = Minecraft.getInstance();
        PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
        if (definition == null || minecraft.level == null)
            return;

        ResourceLocation modelId = definition.getModelId();
        BakedModel model = MODEL_CACHE.computeIfAbsent(modelId, minecraft.getModelManager()::getModel);
        Set<Edge> edges = OUTLINE_EDGE_CACHE.computeIfAbsent(modelId, ignored -> getModelEdges(model));

        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        Vec3 position = prop.getPosition();
        Vec3 scale = prop.getScale();

        poseStack.pushPose();

        poseStack.translate(
                position.x - camera.x,
                position.y - camera.y,
                position.z - camera.z
        );
        poseStack.mulPose(prop.getRotationQuat().normalize());
        poseStack.scale(
                (float) scale.x,
                (float) scale.y,
                (float) scale.z
        );

        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());

        for (Edge edge : edges)
            drawLine(
                    consumer,
                    poseMatrix,
                    normalMatrix,
                    edge.a,
                    edge.b
            );

        poseStack.popPose();
    }

    private static Set<Edge> getModelEdges(BakedModel model) {
        Set<Edge> edges = new HashSet<>();
        var state = Blocks.AIR.defaultBlockState();
        RandomSource random = RandomSource.create();

        for (Direction face : Direction.values()) {
            random.setSeed(0L);
            addEdges(edges, model.getQuads(state, face, random, ModelData.EMPTY, null));
        }

        random.setSeed(0L);
        addEdges(edges, model.getQuads(state, null, random, ModelData.EMPTY, null));

        return Set.copyOf(edges);
    }

    private static void addEdges(
            Set<Edge> edges,
            List<BakedQuad> quads
    ) {
        for (BakedQuad quad : quads) {
            int[] data = quad.getVertices();
            int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
            int offset = DefaultVertexFormat.BLOCK.getOffset(0) / Integer.BYTES;

            Vec3[] vertices = new Vec3[4];

            for (int i = 0; i < 4; i++) {
                int index = i * stride + offset;
                vertices[i] = new Vec3(
                        Float.intBitsToFloat(data[index]),
                        Float.intBitsToFloat(data[index + 1]),
                        Float.intBitsToFloat(data[index + 2])
                );
            }

            for (int i = 0; i < 4; i++)
                edges.add(Edge.of(vertices[i], vertices[(i + 1) % 4]));
        }
    }

    private static void drawLine(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3 start,
            Vec3 end
    ) {
        Vec3 direction = end.subtract(start);
        float length = (float) direction.length();
        if (length < 1.0e-6)
            return;

        float x = (float) (direction.x / length);
        float y = (float) (direction.y / length);
        float z = (float) (direction.z / length);

        consumer.vertex(pose, (float) start.x, (float) start.y, (float) start.z)
                .color(0x66000000)
                .normal(normalMatrix, x, y, z)
                .endVertex();
        consumer.vertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .color(0x66000000)
                .normal(normalMatrix, x, y, z)
                .endVertex();
    }

    private record Edge(Vec3 a, Vec3 b) {
        private static Edge of(Vec3 a, Vec3 b) {
            return compare(a, b) <= 0
                    ? new Edge(a, b)
                    : new Edge(b, a);
        }

        private static int compare(Vec3 a, Vec3 b) {
            int result = Double.compare(a.x, b.x);
            if (result != 0)
                return result;
            result = Double.compare(a.y, b.y);
            if (result != 0)
                return result;
            return Double.compare(a.z, b.z);
        }
    }
}
