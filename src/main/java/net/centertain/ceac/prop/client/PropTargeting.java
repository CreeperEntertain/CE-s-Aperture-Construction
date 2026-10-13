package net.centertain.ceac.prop.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.PropHitResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class PropTargeting {
    private static final double EPSILON = 1.0e-7;

    private PropTargeting() {}

    @SuppressWarnings("resource")
    public static @Nullable PropHitResult pick(
            LocalPlayer player,
            float partialTick,
            HitResult vanillaHit
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.gameMode == null)
            return null;

        Vec3 origin = player.getEyePosition(partialTick);
        Vec3 direction = player.getViewVector(partialTick).normalize();

        double reach = minecraft.gameMode.getPickRange();
        double vanillaDistance = vanillaHit == null || vanillaHit.getType() == HitResult.Type.MISS
                ? reach : origin.distanceTo(vanillaHit.getLocation());
        double closestDistance = Math.min(reach, vanillaDistance);
        if (closestDistance <= 0.0)
            return null;

        Prop closestProp = null;
        Vec3 closestLocation = null;
        Vec3 closestNormal = null;

        int chunkRadius = (int) Math.ceil(reach / 16.0) + 1;
        BlockPos playerPos = player.blockPosition();

        for (int chunkX = (playerPos.getX() >> 4) - chunkRadius; chunkX <= (playerPos.getX() >> 4) + chunkRadius; chunkX++)
            for (int chunkZ = (playerPos.getZ() >> 4) - chunkRadius; chunkZ <= (playerPos.getZ() >> 4) + chunkRadius; chunkZ++)
                for (Prop prop : ClientProps.getInChunk(chunkX, chunkZ)) {
                    PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
                    if (definition == null || !definition.isTargetable())
                        continue;
                    BakedModel model = minecraft.getModelManager().getModel(definition.getModelId());
                    Matrix4f transform = prop.getTransform();
                    for (BakedQuad quad : getQuads(model, prop.getId().getMostSignificantBits())) {
                        List<Vec3> vertices = getWorldVertices(quad, transform);
                        if (vertices.size() != 4)
                            continue;

                        Intersection first = intersectTriangle(
                                origin,
                                direction,
                                vertices.get(0),
                                vertices.get(1),
                                vertices.get(2),
                                closestDistance
                        );
                        if (first != null && first.distance < closestDistance) {
                            closestDistance = first.distance;
                            closestProp = prop;
                            closestLocation = origin.add(direction.scale(closestDistance));
                            closestNormal = first.normal;
                        }

                        Intersection second = intersectTriangle(
                                origin,
                                direction,
                                vertices.get(0),
                                vertices.get(2),
                                vertices.get(3),
                                closestDistance
                        );
                        if (second != null && second.distance < closestDistance) {
                            closestDistance = second.distance;
                            closestProp = prop;
                            closestLocation = origin.add(direction.scale(closestDistance));
                            closestNormal = second.normal;
                        }


                    }
                }
        if (closestProp == null)
            return null;

        Direction hitFace = Direction.getNearest(
                closestNormal.x,
                closestNormal.y,
                closestNormal.z
        );

        BlockHitResult underlyingBlockHit = player.level().clip(new ClipContext(
                origin,
                origin.add(direction.scale(reach)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        return new PropHitResult(
                closestLocation,
                hitFace,
                BlockPos.containing(closestLocation),
                false,
                closestProp,
                underlyingBlockHit
        );
    }

    private static List<BakedQuad> getQuads(
            BakedModel model,
            long seed
    ) {
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

    private static List<Vec3> getWorldVertices(
            BakedQuad quad,
            Matrix4f transform
    ) {
        int[] data = quad.getVertices();
        int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
        int offset = DefaultVertexFormat.BLOCK.getOffset(0) / Integer.BYTES;

        List<Vec3> vertices = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            int index = i * stride + offset;
            Vector3f point = new Vector3f(
                    Float.intBitsToFloat(data[index]),
                    Float.intBitsToFloat(data[index + 1]),
                    Float.intBitsToFloat(data[index + 2])
            );
            point.mulPosition(transform);
            vertices.add(new Vec3(point.x, point.y, point.z));
        }

        return vertices;
    }

    private static @Nullable Intersection intersectTriangle(
            Vec3 origin,
            Vec3 direction,
            Vec3 a,
            Vec3 b,
            Vec3 c,
            double maxDistance
    ) {
        Vec3 edge1 = b.subtract(a);
        Vec3 edge2 = c.subtract(a);
        Vec3 p = direction.cross(edge2);
        double determinant = edge1.dot(p);
        if (Math.abs(determinant) < EPSILON)
            return null;

        double inverse = 1.0 / determinant;
        Vec3 t = origin.subtract(a);
        double u = t.dot(p) * inverse;
        if (u < 0.0 || u > 1.0)
            return null;

        Vec3 q = t.cross(edge1);
        double v = direction.dot(q) * inverse;
        if (v < 0.0 || u + v > 1.0)
            return null;

        double distance = edge2.dot(q) * inverse;
        if (distance < 0.0 || distance >= maxDistance)
            return null;

        Vec3 normal = edge1.cross(edge2);
        if (normal.lengthSqr() < EPSILON)
            return null;

        normal = normal.normalize();
        if (normal.dot(direction) > 0.0)
            normal = normal.scale(-1.0);

        return new Intersection(distance, normal);
    }

    private record Intersection(double distance, Vec3 normal) {}
}
