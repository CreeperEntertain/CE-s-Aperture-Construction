package net.centertain.ceac.decal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.centertain.ceac.decal.client.ClientDecals;
import net.centertain.ceac.item.custom.ScraperItem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;

public final class ScraperXray {
    private static boolean xrayViewActive = false;

    private ScraperXray() {}

    public static boolean getXrayViewActive() {
        return xrayViewActive;
    }

    public static void setXrayViewActive(boolean state) {
        xrayViewActive = state;
    }


    public static void renderXrayView(PoseStack poseStack) {
        if (!xrayViewActive)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null)
            return;
        boolean mainHand = player.getMainHandItem().getItem() instanceof ScraperItem;
        boolean offHand = player.getOffhandItem().getItem() instanceof ScraperItem;
        if (!mainHand && !offHand)
            return;
        Collection<Decal> decals = ClientDecals.getAll().values();
        if (decals.isEmpty())
            return;

        Camera camera = minecraft.gameRenderer.getMainCamera();

        OutlineBufferSource outlineBuffer = minecraft.renderBuffers().outlineBufferSource();
        outlineBuffer.setColor(255, 255, 255, 255);
        VertexConsumer buffer = outlineBuffer.getBuffer(RenderType.outline(InventoryMenu.BLOCK_ATLAS));

        Vec3 cameraPosition = camera.getPosition();

        int[][] faces = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {5, 0, 3, 6},
                {1, 4, 7, 2},
                {5, 4, 1, 0},
                {3, 2, 7, 6}
        };

        for (Decal decal : decals) {
            Vec3 origin = decal.getOrigin();
            Vec3 normal = decal.getNormal().normalize();

            Vec3 reference;
            if (Math.abs(normal.y) < 0.999)
                reference = new Vec3(0.0, 1.0, 0.0);
            else
                reference = new Vec3(1.0, 0.0, 0.0);

            Vec3 tangent = reference.cross(normal).normalize();
            Vec3 bitangent = normal.cross(tangent).normalize();

            double angle = Math.toRadians(22.5 * (decal.getRotation() & 0xff));

            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            Vec3 rotatedTangent = tangent.scale(cos).add(bitangent.scale(sin));
            Vec3 rotatedBitangent = tangent.scale(-sin).add(bitangent.scale(cos));

            double halfWidth = decal.getPixelWidth() / 32.0;
            double halfHeight = decal.getPixelHeight() / 32.0;
            double halfDepth = decal.getBlockDepth() / 2.0;

            Vec3[] corners = {
                    origin.add(rotatedTangent.scale(-halfWidth)).add(rotatedBitangent.scale(halfHeight)).add(normal.scale(-halfDepth)),
                    origin.add(rotatedTangent.scale(halfWidth)).add(rotatedBitangent.scale(halfHeight)).add(normal.scale(-halfDepth)),
                    origin.add(rotatedTangent.scale(halfWidth)).add(rotatedBitangent.scale(-halfHeight)).add(normal.scale(-halfDepth)),
                    origin.add(rotatedTangent.scale(-halfWidth)).add(rotatedBitangent.scale(-halfHeight)).add(normal.scale(-halfDepth)),

                    origin.add(rotatedTangent.scale(halfWidth)).add(rotatedBitangent.scale(halfHeight)).add(normal.scale(halfDepth)),
                    origin.add(rotatedTangent.scale(-halfWidth)).add(rotatedBitangent.scale(halfHeight)).add(normal.scale(halfDepth)),
                    origin.add(rotatedTangent.scale(-halfWidth)).add(rotatedBitangent.scale(-halfHeight)).add(normal.scale(halfDepth)),
                    origin.add(rotatedTangent.scale(halfWidth)).add(rotatedBitangent.scale(-halfHeight)).add(normal.scale(halfDepth))
            };

            for (int[] face : faces)
                for (int index : face) {
                    Vec3 corner = corners[index].subtract(cameraPosition);
                    buffer.vertex(
                            poseStack.last().pose(),
                            (float) corner.x,
                            (float) corner.y,
                            (float) corner.z
                    ).color(255, 255, 255, 255).endVertex();
                }
        }
    }
}
