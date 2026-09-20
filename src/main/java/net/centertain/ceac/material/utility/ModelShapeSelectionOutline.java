package net.centertain.ceac.material.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.material.MaterialShapeFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModelShapeSelectionOutline {
    private ModelShapeSelectionOutline() {}

    public static void replaceSelectionOutline(RenderHighlightEvent.Block event) {
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof MaterialShape shape))
            return;

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

        Set<Edge> edges = new HashSet<>();

        for (MaterialShapeFace face : shape.faces()) {
            List<Vec3> vertices = face.getVertices();

            for (int i = 0; i < vertices.size(); i++) {
                Vec3 a = vertices.get(i);
                Vec3 b = vertices.get((i + 1) % vertices.size());

                Edge edge = Edge.of(a, b);

                if (edges.add(edge))
                    drawLine(
                            consumer,
                            poseMatrix,
                            normalMatrix,
                            a,
                            b,
                            0x66000000
                    );
            }
        }

        poseStack.popPose();
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
