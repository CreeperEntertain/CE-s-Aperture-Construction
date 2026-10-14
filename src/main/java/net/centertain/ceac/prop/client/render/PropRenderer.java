package net.centertain.ceac.prop.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.client.ClientPropMiningProgress;
import net.centertain.ceac.prop.client.ClientProps;
import net.centertain.ceac.prop.client.PropMiningClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
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
import org.joml.Vector3f;

import java.util.*;

// TODO: Make prop rendering support cutout mipped, potentially translucent
public final class PropRenderer {
    private static final Map<ResourceLocation, BakedModel> MODEL_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, Set<Edge>> OUTLINE_EDGE_CACHE = new HashMap<>();

    private static final double CRACK_OVERLAY_OFFSET = 0.001;

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
        MultiBufferSource.BufferSource crumblingBuffers = minecraft.renderBuffers().crumblingBufferSource();

        VertexConsumer consumer = buffers.getBuffer(RenderType.solid());
        ModelBlockRenderer modelRenderer = minecraft.getBlockRenderer().getModelRenderer();
        ModelManager modelManager = minecraft.getModelManager();

        for (int chunkX = center.x - chunkRadius; chunkX <= center.x + chunkRadius; chunkX++)
            for (int chunkZ = center.z - chunkRadius; chunkZ <= center.z + chunkRadius; chunkZ++)
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ))
                    handleProp(
                            prop,
                            minecraft,
                            modelManager,
                            poseStack,
                            camera,
                            level,
                            modelRenderer,
                            consumer,
                            crumblingBuffers
                    );

        buffers.endBatch(RenderType.solid());
        crumblingBuffers.endBatch();
    }

    private static void handleProp(
            Prop prop,
            Minecraft minecraft,
            ModelManager modelManager,
            PoseStack poseStack,
            Vec3 camera,
            ClientLevel level,
            ModelBlockRenderer modelRenderer,
            VertexConsumer consumer,
            MultiBufferSource.BufferSource crumblingBuffers
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

        poseStack.mulPose(prop.getRotationQuat().normalize());

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

        int stage = PropMiningClient.getPredictedStage(prop.getId());
        if (stage < 0)
            stage = ClientPropMiningProgress.getStage(prop.getId());
        if (stage >= 0) {
            VertexConsumer crackConsumer = crumblingBuffers.getBuffer(RenderType.crumbling(InventoryMenu.BLOCK_ATLAS));
            renderDamageOverlay(model, poseStack, crackConsumer, stage, prop);
        }

        poseStack.popPose();
    }

    private static void renderDamageOverlay(
            BakedModel model,
            PoseStack poseStack,
            VertexConsumer consumer,
            int stage,
            Prop prop
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation spriteId = ResourceLocation.fromNamespaceAndPath(
                "minecraft",
                "block/destroy_stage_" + stage
        );
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteId);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Matrix4f worldTransform = prop.getTransform();

        for (BakedQuad quad : getModelQuads(model, prop.getId().getMostSignificantBits())) {
            int[] data = quad.getVertices();
            int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
            int offset = DefaultVertexFormat.BLOCK.getOffset(0) / Integer.BYTES;

            Vec3[] local = new Vec3[4];
            Vec3[] world = new Vec3[4];

            for (int i = 0; i < 4; i++) {
                int index = i * stride + offset;
                local[i] = new Vec3(
                        Float.intBitsToFloat(data[index]),
                        Float.intBitsToFloat(data[index + 1]),
                        Float.intBitsToFloat(data[index + 2])
                );
                world[i] = transformPoint(local[i], worldTransform);
            }

            Vec3 localNormal = local[1].subtract(local[0]).cross(local[2].subtract(local[0]));
            Vec3 worldNormal = world[1].subtract(world[0]).cross(world[2].subtract(world[0]));
            if (localNormal.lengthSqr() < 1.0e-12 || worldNormal.lengthSqr() < 1.0e-12)
                continue;
            localNormal = localNormal.normalize();
            worldNormal = worldNormal.normalize();

            double ax = Math.abs(worldNormal.x);
            double ay = Math.abs(worldNormal.y);
            double az = Math.abs(worldNormal.z);

            Projection projection;
            if (ay >= ax && ay >= az)
                projection = new Projection(0, 2);
            else if (ax >= az)
                projection = new Projection(2, 1);
            else
                projection = new Projection(0, 1);

            List<OverlayVertex> polygon = new ArrayList<>(4);
            for (int i = 0; i < 4; i++)
                polygon.add(new OverlayVertex(local[i], world[i]));
            emitRepeatedProjectedPolygon(
                    polygon,
                    projection,
                    sprite,
                    consumer,
                    poseMatrix,
                    normalMatrix,
                    localNormal
            );
        }
    }

    private static List<BakedQuad> getModelQuads(BakedModel model, long seed) {
        List<BakedQuad> quads = new ArrayList<>();
        var state = Blocks.AIR.defaultBlockState();
        RandomSource random = RandomSource.create();

        for (Direction face : Direction.values()) {
            random.setSeed(seed);
            quads.addAll(model.getQuads(state, face, random, ModelData.EMPTY, null));
        }

        random.setSeed(seed);
        quads.addAll(model.getQuads(state, null, random, ModelData.EMPTY, null));

        return quads;
    }

    private static Vec3 transformPoint(Vec3 point, Matrix4f transform) {
        Vector3f result = new Vector3f(
                (float) point.x,
                (float) point.y,
                (float) point.z
        );
        result.mulPosition(transform);
        return new Vec3(result.x, result.y, result.z);
    }

    private static void emitRepeatedProjectedPolygon(
            List<OverlayVertex> polygon,
            Projection projection,
            TextureAtlasSprite sprite,
            VertexConsumer consumer,
            Matrix4f poseMatrix,
            Matrix3f normalMatrix,
            Vec3 normal
    ) {
        if (polygon.size() < 3)
            return;

        double minU = Double.POSITIVE_INFINITY;
        double minV = Double.POSITIVE_INFINITY;
        double maxU = Double.NEGATIVE_INFINITY;
        double maxV = Double.NEGATIVE_INFINITY;

        for (OverlayVertex vertex : polygon) {
            double u = projection.u(vertex.world);
            double v = projection.v(vertex.world);

            minU = Math.min(minU, u);
            minV = Math.min(minV, v);
            maxU = Math.max(maxU, u);
            maxV = Math.max(maxV, v);
        }

        int firstU = (int) Math.floor(minU);
        int firstV = (int) Math.floor(minV);
        int lastU = (int) Math.ceil(maxU) - 1;
        int lastV = (int) Math.ceil(maxV) - 1;

        for (int tileU = firstU; tileU <= lastU; tileU++)
            for (int tileV = firstV; tileV <= lastV; tileV++) {
                List<OverlayVertex> clipped = clipPolygon(polygon, projection, 0, tileU, true);
                clipped = clipPolygon(clipped, projection, 0, tileU + 1.0, false);
                clipped = clipPolygon(clipped, projection, 1, tileV, true);
                clipped = clipPolygon(clipped, projection, 1, tileV + 1.0, false);
                if (clipped.size() < 3)
                    continue;

                OverlayVertex first = clipped.get(0);

                for (int i = 1; i < clipped.size() - 1; i++) {
                    emitOverlayVertex(
                            first, tileU, tileV, projection, sprite,
                            consumer, poseMatrix, normalMatrix, normal
                    );
                    emitOverlayVertex(
                            clipped.get(i), tileU, tileV, projection, sprite,
                            consumer, poseMatrix, normalMatrix, normal
                    );
                    emitOverlayVertex(
                            clipped.get(i + 1), tileU, tileV, projection, sprite,
                            consumer, poseMatrix, normalMatrix, normal
                    );
                }
            }
    }

    private static List<OverlayVertex> clipPolygon(
            List<OverlayVertex> input,
            Projection projection,
            int axis,
            double boundary,
            boolean keepGreater
    ) {
        List<OverlayVertex> output = new ArrayList<>();
        if (input.isEmpty())
            return output;

        OverlayVertex previous = input.get(input.size() - 1);
        double previousValue = projectedAxis(previous, projection, axis);
        boolean previousInside = keepGreater
                ? previousValue >= boundary - 1.0e-9
                : previousValue <= boundary + 1.0e-9;

        for (OverlayVertex current : input) {
            double currentValue = projectedAxis(current, projection, axis);
            boolean currentInside = keepGreater
                    ? previousValue >= boundary - 1.0e-9
                    : previousValue <= boundary + 1.0e-9;

            if (currentInside != previousInside) {
                double denominator = currentValue - previousValue;
                if (Math.abs(denominator) > 1.0e-12) {
                    double t = (boundary - previousValue) / denominator;
                    output.add(new OverlayVertex(
                            previous.local.lerp(current.local, t),
                            previous.world.lerp(current.world, t)
                    ));
                }
            }
            if (currentInside)
                output.add(current);

            previous = current;
            previousValue = currentValue;
            previousInside = currentInside;
        }

        return output;
    }

    private static double projectedAxis(OverlayVertex vertex, Projection projection, int axis) {
        return axis == 0 ? projection.u(vertex.world) : projection.v(vertex.world);
    }

    private static void emitOverlayVertex(
            OverlayVertex vertex,
            int tileU, int tileV,
            Projection projection,
            TextureAtlasSprite sprite,
            VertexConsumer consumer,
            Matrix4f poseMatrix,
            Matrix3f normalMatrix,
            Vec3 normal
    ) {
        double localU = projection.u(vertex.world) - tileU;
        double localV = projection.v(vertex.world) - tileV;

        float x = (float) (vertex.local.x + normal.x * CRACK_OVERLAY_OFFSET);
        float y = (float) (vertex.local.y + normal.y * CRACK_OVERLAY_OFFSET);
        float z = (float) (vertex.local.z + normal.z * CRACK_OVERLAY_OFFSET);

        float u = sprite.getU(Math.max(0.0, Math.min(16.0, localU * 16.0)));
        float v = sprite.getV(Math.max(0.0, Math.min(16.0, (1.0 - localV) * 16.0)));

        consumer.vertex(poseMatrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(
                        normalMatrix,
                        (float) normal.x,
                        (float) normal.y,
                        (float) normal.z
                )
                .endVertex();
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

    private record OverlayVertex(Vec3 local, Vec3 world) {}

    private record Projection(int uAxis, int vAxis) {
        double u(Vec3 point) {
            return component(point, uAxis);
        }
        double v(Vec3 point) {
            return component(point, vAxis);
        }

        private static double component(Vec3 point, int axis) {
            return switch (axis) {
                case 0 -> point.x;
                case 1 -> point.y;
                default -> point.z;
            };
        }
    }
}
