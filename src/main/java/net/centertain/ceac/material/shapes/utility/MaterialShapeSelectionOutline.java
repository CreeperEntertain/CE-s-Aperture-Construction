package net.centertain.ceac.material.shapes.utility;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MaterialShapeSelectionOutline {
    private MaterialShapeSelectionOutline() {}

    public static void replaceSelectionOutline(RenderHighlightEvent.Block event) {
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof MaterialShape))
            return;

        BakedModel model = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(state);

        event.setCanceled(true);

        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(
                pos.getX() - camera.x,
                pos.getY() - camera.y,
                pos.getZ() - camera.z
        );
        PoseStack.Pose pose = poseStack.last();

        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());

        RandomSource random = RandomSource.create();
        Set<Edge> edges = new HashSet<>();

        for (Direction side : Direction.values())
            addEdges(edges, model.getQuads(
                    state,
                    side,
                    random,
                    ModelData.EMPTY,
                    null
            ));
        addEdges(edges, model.getQuads(
                state,
                null,
                random,
                ModelData.EMPTY,
                null
        ));

        for (Edge edge : edges)
            drawLine(
                    consumer,
                    poseMatrix,
                    normalMatrix,
                    edge.a(),
                    edge.b(),
                    0x66000000
            );

        poseStack.popPose();
    }

    private static void addEdges(
            Set<Edge> edges,
            List<BakedQuad> quads
    ) {
        for (BakedQuad quad : quads) {
            List<Vec3> vertices = getQuadVertices(quad);

            for (int i = 0; i < vertices.size(); i++) {
                Vec3 a = vertices.get(i);
                Vec3 b = vertices.get((i + 1) % vertices.size());

                edges.add(Edge.of(a, b));
            }
        }
    }

    private static List<Vec3> getQuadVertices(BakedQuad quad) {
        int[] vertices = quad.getVertices();
        VertexFormat format = DefaultVertexFormat.BLOCK;

        int stride = format.getIntegerSize();
        int positionOffset = format.getOffset(0) / Integer.BYTES;

        List<Vec3> points = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            int offset = i * stride + positionOffset;

            Vec3 point = new Vec3(
                    Float.intBitsToFloat(vertices[offset]),
                    Float.intBitsToFloat(vertices[offset + 1]),
                    Float.intBitsToFloat(vertices[offset + 2])
            );

            if (!points.contains(point))
                points.add(point);
        }

        return List.copyOf(points);
    }

    private static void drawLine(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            Matrix3f normalMatrix,
            Vec3 start,
            Vec3 end,
            int colorARGB
    ) {
        Vec3 direction = end.subtract(start);
        float length = (float) direction.length();

        if (length < 1.0e-6f)
            return;

        float x = (float) (direction.x / length);
        float y = (float) (direction.y / length);
        float z = (float) (direction.z / length);

        vertexConsumer
                .vertex(pose, (float) start.x, (float) start.y, (float) start.z)
                .color(colorARGB)
                .normal(normalMatrix, x, y, z)
                .endVertex();

        vertexConsumer
                .vertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .color(colorARGB)
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
