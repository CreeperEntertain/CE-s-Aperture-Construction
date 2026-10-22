package net.centertain.ceac.prop.framework.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.item.custom.PropPlacerItem;
import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.PropDefinition;
import net.centertain.ceac.prop.framework.PropGeometry;
import net.centertain.ceac.prop.framework.client.ClientProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class PropPreviewer {
    private static final int OUTLINE_COLOR = 0xFFFFFFFF;
    private static final double FACING_EPSILON = 1.0e-6;
    private static final double OCCLUSION_EPSILON = 0.003;
    private static final int MIN_EDGE_SAMPLES = 8;
    private static final int MAX_EDGE_SAMPLES = 48;

    private static final Map<ResourceLocation, List<MeshEdge>> EDGE_CACHE = new HashMap<>();

    private static ResourceLocation activeDefinitionId;
    private static boolean seenThisTick;

    private static final Matrix4f PROP_VOLUME_TRANSFORM = new Matrix4f();

    private static final RenderType SILHOUETTE_LINES = RenderType.create(
            "ceac_prop_preview_silhouette",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            GameRenderer::getRendertypeLinesShader
                    ))
                    .setLineState(new RenderStateShard.LineStateShard(
                            OptionalDouble.of(3.0)
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
                            "lequal", GL11.GL_LEQUAL
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(
                            true, false
                    ))
                    .createCompositeState(false)
    );

    private PropPreviewer() {}


    static boolean isPreviewActive() {
        return activeDefinitionId != null;
    }

    public static void render(ResourceLocation definitionId) {
        activeDefinitionId = definitionId;
        seenThisTick = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if (!seenThisTick || minecraft.player == null || minecraft.level == null)
            activeDefinitionId = null;

        seenThisTick = false;
    }

    @SubscribeEvent
    public static void onModelIsBaked(ModelEvent.BakingCompleted event) {
        // Whatever. Go, my Minecraft baking path!
        EDGE_CACHE.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            return;

        ResourceLocation definitionId = activeDefinitionId;
        if (definitionId == null)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null)
            return;

        if (
                !(minecraft.player.getMainHandItem().getItem() instanceof PropPlacerItem) &&
                !(minecraft.player.getOffhandItem().getItem() instanceof PropPlacerItem)
        )
            return;

        PropDefinition definition = ModProps.get(definitionId).orElse(null);
        if (definition == null)
            return;

        PropGeometry geometry = PropGeometry.get(minecraft.getResourceManager(), definition.getModelId());
        if (geometry == null || geometry.getTriangles().isEmpty())
            return;

        List<MeshEdge> edges = EDGE_CACHE.computeIfAbsent(
                definition.getModelId(), ignored -> buildEdges(geometry)
        );

        HitResult hit = minecraft.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || blockHit.getType() != HitResult.Type.BLOCK)
            return;

        Vec3 normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
        Vec3 position = blockHit.getLocation().add(normal.scale(0.001));

        Matrix4f transform = new Matrix4f().translation(
                (float) position.x,
                (float) position.y,
                (float) position.z
        );

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        Vector3f translation = transform.getTranslation(new Vector3f());
        Quaternionf rotation = transform.getUnnormalizedRotation(new Quaternionf()).normalize();
        Vector3f scale = transform.getScale(new Vector3f());

        poseStack.translate(
                translation.x - camera.x,
                translation.y - camera.y,
                translation.z - camera.z
        );
        poseStack.mulPose(rotation);
        poseStack.scale(scale.x, scale.y, scale.z);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = new Matrix4f(pose.pose());

        var buffers = minecraft.renderBuffers().bufferSource();

        boolean captureCollision = PropCollisionHatching.begin();

        List<PropGeometry.Triangle> clippedTriangles = captureCollision
                ? PropCollisionClipGeometry.build(
                        geometry, transform, minecraft.level
                ) : List.of();

        if (captureCollision) {
            VertexConsumer collisionConsumer = buffers.getBuffer(PropCollisionHatching.CAPTURE_TYPE);

            emitTriangles(collisionConsumer, geometry, poseMatrix);
            buffers.endBatch(PropCollisionHatching.CAPTURE_TYPE);
            PropCollisionHatching.beginPropDepthCapture();

            VertexConsumer depthConsumer = buffers.getBuffer(PropCollisionHatching.DEPTH_TYPE);

            emitTriangles(depthConsumer, geometry, poseMatrix);
            buffers.endBatch(PropCollisionHatching.DEPTH_TYPE);
        }

        poseStack.popPose();

        if (captureCollision && !clippedTriangles.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(-camera.x, -camera.y, -camera.z);

            VertexConsumer clipConsumer = buffers.getBuffer(PropCollisionHatching.CLIP_CAPTURE_TYPE);
            emitTriangles(clipConsumer, clippedTriangles, poseStack.last().pose());
            buffers.endBatch(PropCollisionHatching.CLIP_CAPTURE_TYPE);

            poseStack.popPose();
        }
        if (captureCollision)
            capturePlacedPropVolumes(minecraft, poseStack, camera, buffers);
        if (captureCollision)
            PropCollisionHatching.finishAndRender();

        PoseStack outlinePoseStack = event.getPoseStack();
        outlinePoseStack.pushPose();

        outlinePoseStack.translate(
                translation.x - camera.x,
                translation.y - camera.y,
                translation.z - camera.z
        );
        outlinePoseStack.mulPose(rotation);
        outlinePoseStack.scale(
                scale.x,
                scale.y,
                scale.z
        );

        PoseStack.Pose outlinePose = outlinePoseStack.last();
        Matrix4f outlinePoseMatrix = outlinePose.pose();
        Matrix3f outlineNormalMatrix = outlinePose.normal();

        VertexConsumer outlineConsumer = buffers.getBuffer(SILHOUETTE_LINES);

        for (MeshEdge edge : edges) {
            if (isSilhouetteEdge(edge, transform, camera))
                continue;

            drawVisibleParts(
                    edge,
                    geometry,
                    transform,
                    camera,
                    outlineConsumer,
                    outlinePoseMatrix,
                    outlineNormalMatrix
            );
        }

        outlinePoseStack.popPose();

        buffers.endBatch(SILHOUETTE_LINES);
    }

    private static void capturePlacedPropVolumes(
            Minecraft minecraft,
            PoseStack poseStack,
            Vec3 camera,
            MultiBufferSource.BufferSource buffers
    ) {
        if (!PropCollisionHatching.beginPropVolumeCapture())
            return;

        long renderTimeNanos = PropRenderer.getFrameRenderTimeNanos();
        if (renderTimeNanos == 0L)
            renderTimeNanos = System.nanoTime();

        ChunkPos center = new ChunkPos(BlockPos.containing(camera));
        int chunkRadius = minecraft.options.renderDistance().get() + 1;

        int batchCount = 0;
        PropCollisionHatching.beginPropParityBatch();

        for (int chunkX = center.x - chunkRadius; chunkX <= center.x + chunkRadius; chunkX++)
            for (int chunkZ = center.z - chunkRadius; chunkZ <= center.z + chunkRadius; chunkZ++)
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ)) {
                    PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);
                    if (definition == null)
                        continue;

                    PropGeometry geometry = PropGeometry.get(minecraft.getResourceManager(), definition.getModelId());
                    if (geometry == null || geometry.getTriangles().isEmpty())
                        continue;

                    ClientProps.copyRenderTransformTo(prop, PROP_VOLUME_TRANSFORM, renderTimeNanos);

                    Vector3f translation = PROP_VOLUME_TRANSFORM.getTranslation(new Vector3f());
                    Quaternionf rotation = PROP_VOLUME_TRANSFORM.getUnnormalizedRotation(new Quaternionf()).normalize();
                    Vector3f scale = PROP_VOLUME_TRANSFORM.getScale(new Vector3f());

                    poseStack.pushPose();
                    poseStack.translate(
                            translation.x - camera.x,
                            translation.y - camera.y,
                            translation.z - camera.z
                    );
                    poseStack.mulPose(rotation);
                    poseStack.scale(scale.x, scale.y, scale.z);

                    PropCollisionHatching.setPropVolumeBit(1 << batchCount);

                    VertexConsumer consumer = buffers.getBuffer(PropCollisionHatching.PROP_VOLUME_CAPTURE_TYPE);
                    emitTriangles(consumer, geometry, poseStack.last().pose());

                    poseStack.popPose();
                    buffers.endBatch(PropCollisionHatching.PROP_VOLUME_CAPTURE_TYPE);

                    batchCount++;

                    if (batchCount == 31) {
                        PropCollisionHatching.accumulatePropParityBatch(buffers);
                        PropCollisionHatching.beginPropParityBatch();
                        batchCount = 0;
                    }
                }
        if (batchCount > 0)
            PropCollisionHatching.accumulatePropParityBatch(buffers);
    }

    private static List<MeshEdge> buildEdges(PropGeometry geometry) {
        Map<EdgeKey, List<PropGeometry.Triangle>> uses = new HashMap<>();

        for (PropGeometry.Triangle triangle : geometry.getTriangles()) {
            addEdge(uses, triangle.a(), triangle.b(), triangle);
            addEdge(uses, triangle.b(), triangle.c(), triangle);
            addEdge(uses, triangle.c(), triangle.a(), triangle);
        }

        List<MeshEdge> result = new ArrayList<>(uses.size());
        for (Map.Entry<EdgeKey, List<PropGeometry.Triangle>> entry : uses.entrySet()) {
            EdgeKey key = entry.getKey();
            result.add(new MeshEdge(
                    key.first,
                    key.second,
                    List.copyOf(entry.getValue())
            ));
        }

        return List.copyOf(result);
    }

    private static void addEdge(
            Map<EdgeKey, List<PropGeometry.Triangle>> uses,
            Vec3 a,
            Vec3 b,
            PropGeometry.Triangle triangle
    ) {
        uses.computeIfAbsent(EdgeKey.of(a, b), ignored -> new ArrayList<>()).add(triangle);
    }

    private static boolean isSilhouetteEdge(
            MeshEdge edge,
            Matrix4f transform,
            Vec3 camera
    ) {
        if (edge.adjacent.size() == 1)
            return false;

        boolean facingCamera = false;
        boolean facingAway = false;

        for (PropGeometry.Triangle triangle : edge.adjacent) {
            Vec3 a = transformPoint(triangle.a(), transform);
            Vec3 b = transformPoint(triangle.b(), transform);
            Vec3 c = transformPoint(triangle.c(), transform);

            Vec3 normal = b.subtract(a).cross(c.subtract(a));
            if (normal.lengthSqr() < 1.0e-12)
                continue;

            Vec3 center = a.add(b).add(c).scale(1.0 / 3.0);
            Vec3 toCamera = camera.subtract(center);
            if (toCamera.lengthSqr() < 1.0e-12)
                continue;

            double facing = normal.normalize().dot(toCamera.normalize());
            if (facing > FACING_EPSILON)
                facingCamera = true;
            else if (facing < -FACING_EPSILON)
                facingAway = true;
            if (facingCamera && facingAway)
                return false;
        }

        return true;
    }

    private static void emitTriangles(
            VertexConsumer consumer,
            List<PropGeometry.Triangle> triangles,
            Matrix4f poseMatrix
    ) {
        for (PropGeometry.Triangle triangle : triangles) {
            Vec3 a = triangle.a();
            Vec3 b = triangle.b();
            Vec3 c = triangle.c();

            consumer.vertex(poseMatrix, (float) a.x, (float) a.y, (float) a.z).endVertex();
            consumer.vertex(poseMatrix, (float) b.x, (float) b.y, (float) b.z).endVertex();
            consumer.vertex(poseMatrix, (float) c.x, (float) c.y, (float) c.z).endVertex();
        }
    }

    private static void emitTriangles(
            VertexConsumer consumer,
            PropGeometry geometry,
            Matrix4f poseMatrix
    ) {
        for (PropGeometry.Triangle triangle : geometry.getTriangles()) {
            Vec3 a = triangle.a();
            Vec3 b = triangle.b();
            Vec3 c = triangle.c();

            consumer.vertex(poseMatrix, (float) a.x, (float) a.y, (float) a.z).endVertex();
            consumer.vertex(poseMatrix, (float) b.x, (float) b.y, (float) b.z).endVertex();
            consumer.vertex(poseMatrix, (float) c.x, (float) c.y, (float) c.z).endVertex();
        }
    }

    private static void drawVisibleParts(
            MeshEdge edge,
            PropGeometry geometry,
            Matrix4f transform,
            Vec3 camera,
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normalMatrix
    ) {
        Vec3 worldA = transformPoint(edge.a, transform);
        Vec3 worldB = transformPoint(edge.b, transform);
        double length = worldA.distanceTo(worldB);
        if (length < 1.0e-6)
            return;

        double cameraDistance = camera.distanceTo(worldA.lerp(worldB, 0.5));
        int samples = Math.max(MIN_EDGE_SAMPLES, Math.min(
                MAX_EDGE_SAMPLES,
                (int) Math.ceil(length * 24.0 / Math.max(1.0, cameraDistance * 0.5))
        ));

        double previousT = 0.0;
        boolean previousVisible = isVisible(geometry, transform, camera, worldA);
        double visibleStart = previousVisible ? 0.0 : Double.NaN;

        for (int i = 0; i <= samples; i++) {
            double currentT = (double) i / samples;
            Vec3 point = worldA.lerp(worldB, currentT);
            boolean currentVisible = isVisible(geometry, transform, camera, point);

            if (previousVisible && !currentVisible) {
                double boundary = findBoundary(
                        geometry, transform, camera,
                        worldA, worldB,
                        previousT, currentT,
                        true
                );
                drawSegment(
                        consumer, pose, normalMatrix,
                        edge.a().lerp(edge.b(), visibleStart),
                        edge.a().lerp(edge.b(), boundary)
                );
                visibleStart = Double.NaN;
            } else if (!previousVisible && currentVisible)
                visibleStart = findBoundary(
                        geometry, transform, camera,
                        worldA, worldB,
                        previousT, currentT,
                        false
                );

            previousT = currentT;
            previousVisible = currentVisible;
        }
        if (previousVisible)
            drawSegment(
                    consumer, pose, normalMatrix,
                    edge.a.lerp(edge.b, visibleStart),
                    edge.b
            );
    }

    private static double findBoundary(
            PropGeometry geometry,
            Matrix4f transform,
            Vec3 camera,
            Vec3 worldA,
            Vec3 worldB,
            double leftT,
            double rightT,
            boolean leftVisible
    ) {
        for (int i = 0; i < 9; i++) {
            double middleT = (leftT + rightT) * 0.5;
            boolean middleVisible = isVisible(geometry, transform, camera, worldA.lerp(worldB, middleT));
            if (middleVisible == leftVisible)
                leftT = middleT;
            else
                rightT = middleT;
        }

        return (leftT + rightT) * 0.5;
    }

    private static boolean isVisible(
            PropGeometry geometry,
            Matrix4f transform,
            Vec3 camera,
            Vec3 worldPoint
    ) {
        Vec3 toPoint = worldPoint.subtract(camera);
        double distance = toPoint.length();
        if (distance < 1.0e-6)
            return true;

        PropGeometry.RayHit hit = geometry.raycast(
                camera,
                toPoint.scale(1.0 / distance),
                transform,
                distance + OCCLUSION_EPSILON
        );

        return hit == null || hit.distance() >= distance - OCCLUSION_EPSILON;
    }

    private static void drawSegment(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3 start,
            Vec3 end
    ) {
        drawSegment(consumer, pose, normalMatrix, start, end, OUTLINE_COLOR);
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawSegment(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3 start,
            Vec3 end,
            int color
    ) {
        Vec3 direction = end.subtract(start);
        double length = direction.length();
        if (length < 1.0e-6)
            return;

        float nx = (float) (direction.x / length);
        float ny = (float) (direction.y / length);
        float nz = (float) (direction.z / length);

        consumer
                .vertex(pose, (float) start.x, (float) start.y, (float) start.z)
                .color(color)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
        consumer
                .vertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .color(color)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
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


    private record MeshEdge(Vec3 a, Vec3 b, List<PropGeometry.Triangle> adjacent) {}
    private record EdgeKey(Vec3 first, Vec3 second) {
        private static EdgeKey of (Vec3 a, Vec3 b) {
            return compare(a, b) <= 0 ? new EdgeKey(a, b) : new EdgeKey(b, a);
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
