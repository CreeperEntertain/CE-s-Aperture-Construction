package net.centertain.ceac.prop.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public final class PropLightingSolver {
    private static final float NEAR_LIGHT_OFFSET = 0.1f;
    private static final float FAR_LIGHT_OFFSET = 0.75f;
    private static final float MIN_AMBIENT_FACTOR = 0.95f;
    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    private PropLightingSolver() {}


    public static void render(
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos tintPosition,
            PoseStack poseStack,
            VertexConsumer consumer,
            Matrix4f worldTransform,
            BlockColors blockColors,
            long seed,
            int overlay,
            RenderType renderType
    ) {
        PoseStack.Pose pose = poseStack.last();
        Matrix3f worldNormalMatrix = new Matrix3f(worldTransform).invert().transpose();
        boolean ambientOcclusion = Minecraft.useAmbientOcclusion() && model.useAmbientOcclusion(state, renderType);

        Scratch scratch = SCRATCH.get();
        scratch.resetCache(level);
        RandomSource random = scratch.random;

        for (Direction side : Direction.values()) {
            random.setSeed(seed);
            List<BakedQuad> quads = model.getQuads(
                    state,
                    side,
                    random,
                    ModelData.EMPTY,
                    renderType
            );
            renderQuads(
                    level, quads, state, tintPosition, pose, consumer,
                    worldTransform, worldNormalMatrix, blockColors,
                    overlay, ambientOcclusion, scratch
            );
        }

        random.setSeed(seed);

        List<BakedQuad> unculledQuads = model.getQuads(
                state,
                null,
                random,
                ModelData.EMPTY,
                renderType
        );

        renderQuads(
                level, unculledQuads, state, tintPosition, pose, consumer,
                worldTransform, worldNormalMatrix, blockColors,
                overlay, ambientOcclusion, scratch
        );
    }

    private static void renderQuads(
            BlockAndTintGetter level,
            List<BakedQuad> quads,
            BlockState state,
            BlockPos tintPosition,
            PoseStack.Pose pose,
            VertexConsumer consumer,
            Matrix4f worldTransform,
            Matrix3f worldNormalMatrix,
            BlockColors blockColors,
            int overlay,
            boolean ambientOcclusion,
            Scratch scratch
    ) {
        for (BakedQuad quad : quads)
            renderQuad(
                    level, quad, state, tintPosition, pose, consumer,
                    worldTransform, worldNormalMatrix, blockColors,
                    overlay, ambientOcclusion, scratch
            );
    }

    private static void renderQuad(
            BlockAndTintGetter level,
            BakedQuad quad,
            BlockState state,
            BlockPos tintPosition,
            PoseStack.Pose pose,
            VertexConsumer consumer,
            Matrix4f worldTransform,
            Matrix3f worldNormalMatrix,
            BlockColors blockColors,
            int overlay,
            boolean ambientOcclusion,
            Scratch scratch
    ) {
        int[] vertices = quad.getVertices();
        Vector3f faceNormal = calculateFaceNormal(vertices);

        int tint = quad.isTinted()
                ? blockColors.getColor(state, level, tintPosition, quad.getTintIndex())
                : 0xFFFFFF;
        float tintR = ((tint >>> 16) & 0xFF) / 255.0f;
        float tintG = ((tint >>> 8) & 0xFF) / 255.0f;
        float tintB = (tint & 0xFF) / 255.0f;

        for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
            int offset = vertexIndex * IQuadTransformer.STRIDE;

            Vector3f localPosition = readPosition(vertices, vertexIndex);
            Vector3f localNormal = readNormal(vertices, offset);
            if (localNormal.lengthSquared() < 1.0e-8f)
                localNormal.set(faceNormal);
            else
                localNormal.normalize();

            Vector3f worldPosition = new Vector3f(localPosition).mulPosition(worldTransform);
            Vector3f worldNormal = worldNormalMatrix.transform(new Vector3f(localNormal)).normalize();

            float openness = sampleShadeBrightness(
                    level,
                    worldPosition.x,
                    worldPosition.y,
                    worldPosition.z,
                    scratch
            );
            openness = Mth.clamp(openness, 0.0f, 1.0f);

            float embeddedBlend = Mth.clamp((1.0f - openness) / 0.8f, 0.0f, 1.0f);

            sampleLight(
                    level,
                    worldPosition.x + worldNormal.x * NEAR_LIGHT_OFFSET,
                    worldPosition.y + worldNormal.y * NEAR_LIGHT_OFFSET,
                    worldPosition.z + worldNormal.z * NEAR_LIGHT_OFFSET,
                    scratch,
                    scratch.nearPositive
            );
            sampleLight(
                    level,
                    worldPosition.x + worldNormal.x * FAR_LIGHT_OFFSET,
                    worldPosition.y + worldNormal.y * FAR_LIGHT_OFFSET,
                    worldPosition.z + worldNormal.z * FAR_LIGHT_OFFSET,
                    scratch,
                    scratch.farPositive
            );

            float nearBlock = scratch.nearPositive[0];
            float nearSky = scratch.nearPositive[1];

            float farBlock = scratch.farPositive[0];
            float farSky = scratch.farPositive[1];

            float blockLight = nearBlock + (Math.max(nearBlock, farBlock) - nearBlock) * embeddedBlend;
            float skyLight = nearSky + (Math.max(nearSky, farSky) - nearSky) * embeddedBlend;

            int bakedLight = vertices[offset + IQuadTransformer.UV2];

            blockLight = Math.max(blockLight, LightTexture.block(bakedLight));
            skyLight = Math.max(skyLight, LightTexture.sky(bakedLight));

            int lightmap = packInterpolatedLight(blockLight, skyLight);

            float aoFactor = 1.0f;
            if (ambientOcclusion && quad.hasAmbientOcclusion())
                aoFactor = MIN_AMBIENT_FACTOR + (1.0f - MIN_AMBIENT_FACTOR) * openness;

            float directionalShade = level.getShade(
                    worldNormal.x,
                    worldNormal.y,
                    worldNormal.z,
                    quad.isShade()
            );

            float brightness = directionalShade * aoFactor;

            int vertexColor = vertices[offset + IQuadTransformer.COLOR];

            float red = (vertexColor & 0xFF) * tintR * brightness;
            float green = ((vertexColor >>> 8) & 0xFF) * tintG * brightness;
            float blue = ((vertexColor >>> 16) & 0xFF) * tintB * brightness;
            int alpha = (vertexColor >>> 24) & 0xFF;

            float u = Float.intBitsToFloat(vertices[offset + IQuadTransformer.UV0]);
            float v = Float.intBitsToFloat(vertices[offset + IQuadTransformer.UV0 + 1]);

            consumer
                    .vertex(
                            pose.pose(),
                            localPosition.x,
                            localPosition.y,
                            localPosition.z
                    ).color(
                            Mth.clamp(Math.round(red), 0, 255),
                            Mth.clamp(Math.round(green), 0, 255),
                            Mth.clamp(Math.round(blue), 0, 255),
                            alpha
                    ).uv(u, v)
                    .overlayCoords(overlay)
                    .uv2(lightmap)
                    .normal(
                            pose.normal(),
                            localNormal.x,
                            localNormal.y,
                            localNormal.z
                    ).endVertex();
        }
    }

    private static Vector3f readPosition(int[] vertices, int vertexIndex) {
        int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        return new Vector3f(
                Float.intBitsToFloat(vertices[offset]),
                Float.intBitsToFloat(vertices[offset + 1]),
                Float.intBitsToFloat(vertices[offset + 2])
        );
    }

    private static Vector3f readNormal(int[] vertices, int offset) {
        int packedNormal = vertices[offset + IQuadTransformer.NORMAL];
        return new Vector3f(
                ((byte) (packedNormal & 0xFF)) / 127.0f,
                ((byte) ((packedNormal >>> 8) & 0xff)) / 127.0f,
                ((byte) ((packedNormal >>> 16) & 0xFF)) / 127.0f
        );
    }

    private static Vector3f calculateFaceNormal(int[] vertices) {
        Vector3f a = readPosition(vertices, 0);
        Vector3f b = readPosition(vertices, 1);
        Vector3f c = readPosition(vertices, 2);

        Vector3f ab = new Vector3f(b).sub(a);
        Vector3f ac = new Vector3f(c).sub(a);

        ab.cross(ac);
        if (ab.lengthSquared() < 1.0e-8f)
            return new Vector3f(0.0f, 1.0f, 0.0f);

        return ab.normalize();
    }

    private static void sampleLight(
            BlockAndTintGetter level,
            float x,
            float y,
            float z,
            Scratch scratch,
            float[] result
    ) {
        float gx = x - 0.5f;
        float gy = y - 0.5f;
        float gz = z - 0.5f;

        int x0 = Mth.floor(gx);
        int y0 = Mth.floor(gy);
        int z0 = Mth.floor(gz);

        float fx = gx - x0;
        float fy = gy - y0;
        float fz = gz - z0;

        result[0] = 0.0f;
        result[1] = 0.0f;

        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 1;

        for (int dx = 0; dx <= 1; dx++) {
            float wx = dx == 0 ? 1.0f - fx : fx;
            for (int dy = 0; dy <= 1; dy++) {
                float wy = dy == 0 ? 1.0f - fy : fy;
                for (int dz = 0; dz <= 1; dz++) {
                    float wz = dz == 0 ? 1.0f - fz : fz;
                    float weight = wx * wy * wz;

                    BlockPos samplePos = scratch.position.set(
                            x0 + dx,
                            Mth.clamp(y0 + dy, minY, maxY),
                            z0 + dz
                    );
                    int packed = scratch.getPackedLight(level, samplePos);

                    result[0] += LightTexture.block(packed) * weight;
                    result[1] += LightTexture.sky(packed) * weight;
                }
            }
        }
    }

    private static float sampleShadeBrightness(
            BlockAndTintGetter level,
            float x,
            float y,
            float z,
            Scratch scratch
    ) {
        float gx = x - 0.5f;
        float gy = y - 0.5f;
        float gz = z - 0.5f;

        int x0 = Mth.floor(gx);
        int y0 = Mth.floor(gy);
        int z0 = Mth.floor(gz);

        float fx = gx - x0;
        float fy = gy - y0;
        float fz = gz - z0;

        float result = 0.0f;
        int minY = scratch.minBuildHeight;
        int maxY = scratch.maxBuildHeight;

        for (int dx = 0; dx <= 1; dx++) {
            float wx = dx == 0 ? 1.0f - fx : fx;
            for (int dy = 0; dy <= 1; dy++) {
                float wy = dy == 0 ? 1.0f - fy : fy;
                for (int dz = 0; dz <= 1; dz++) {
                    float wz = dz == 0 ? 1.0f - fz : fz;
                    float weight = wx * wy * wz;

                    BlockPos samplePos = scratch.position.set(
                            x0 + dx,
                            Mth.clamp(y0 + dy, minY, maxY),
                            z0 + dz
                    );
                    result += scratch.getShadeBrightness(level, samplePos) * weight;
                }
            }
        }

        return Mth.clamp(result, 0.0f, 1.0f);
    }

    private static int packInterpolatedLight(float block, float sky) {
        int blockCoordinate = Mth.clamp(
                Math.round(block * 16.0f),
                0,
                15 * 16
        );
        int skyCoordinate = Mth.clamp(
                Math.round(sky * 16.0f),
                0,
                15 * 16
        );
        return blockCoordinate | (skyCoordinate << 16);
    }


    private static final class Scratch {
        private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();

        private final RandomSource random = RandomSource.create();

        private final Long2IntOpenHashMap lightCache = new Long2IntOpenHashMap(256);
        private final Long2FloatOpenHashMap shadeCache = new Long2FloatOpenHashMap(256);

        private final float[] nearPositive = new float[2];
        private final float[] farPositive = new float[2];

        private int minBuildHeight;
        private int maxBuildHeight;

        private Scratch() {
            lightCache.defaultReturnValue(-1);
            shadeCache.defaultReturnValue(-1.0f);
        }

        private void resetCache(BlockAndTintGetter level) {
            lightCache.clear();
            shadeCache.clear();

            minBuildHeight = level.getMinBuildHeight();
            maxBuildHeight = level.getMaxBuildHeight() - 1;
        }

        private int getPackedLight(BlockAndTintGetter level, BlockPos pos) {
            long key = pos.asLong();

            int packed = lightCache.get(key);
            if (packed != -1)
                return packed;

            packed = LevelRenderer.getLightColor(level, pos);
            lightCache.put(key, packed);
            return packed;
        }

        private float getShadeBrightness(BlockAndTintGetter level, BlockPos pos) {
            long key = pos.asLong();

            float shade = shadeCache.get(key);
            if (shade >= 0.0f)
                return shade;

            shade = level.getBlockState(pos).getShadeBrightness(level, pos);
            shadeCache.put(key, shade);
            return shade;
        }
    }
}
