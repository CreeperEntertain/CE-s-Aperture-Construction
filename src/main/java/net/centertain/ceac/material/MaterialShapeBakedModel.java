package net.centertain.ceac.material;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.block.custom.material_shapes.MaterialShapeSlope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaterialShapeBakedModel extends BakedModelWrapper<BakedModel> {
    private static final double DIRECTION_BIAS = 1.0e-6;

    private final MaterialShape shape;

    public MaterialShapeBakedModel(
            BakedModel originalModel,
            MaterialShape shape
    ) {
        super(originalModel);
        this.shape = shape;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            @NotNull RandomSource random,
            @NotNull ModelData data,
            @Nullable RenderType renderType
    ) {
        List<BakedQuad> original = originalModel.getQuads(
                state,
                side,
                random,
                data,
                renderType
        );

        Map<Integer, MaterialShapeBlockEntity.MaterialAssignment> materials =
                data.get(MaterialShapeBlockEntity.MATERIALS);

        List<BakedQuad> result = new ArrayList<>(original.size());

        for (BakedQuad quad : original) {
            int faceIndex = findFace(quad);

            MaterialShapeBlockEntity.MaterialAssignment assignment = materials == null
                    ? null
                    : materials.get(faceIndex);

            if (assignment != null)
                quad = retexture(quad, assignment);

            result.add(transformQuad(quad, state));
        }

        return result;
    }

    private BakedQuad transformQuad(
            BakedQuad quad,
            BlockState state
    ) {
        if (!(state.getBlock() instanceof MaterialShapeSlope))
            return quad;

        Direction facing = state.getValue(MaterialShapeSlope.FACING);
        int rotation = state.getValue(MaterialShapeSlope.ROTATION);

        Vec3 forward = direction(facing);

        Vec3 baseX = forward.scale(-1.0);
        Vec3 baseY = switch (facing) {
            case UP -> new Vec3(0, 0, -1);
            case DOWN -> new Vec3(0, 0, 1);
            default -> new Vec3(0, 1, 0);
        };
        Vec3 baseZ = baseY.cross(forward).normalize();

        //noinspection UnnecessaryLocalVariable
        Vec3 x = baseX; // Thanks for the suggestion, compiler, but I like my code readable.
        Vec3 y = baseY;

        for (int i = 0; i < rotation; i++)
            y = y.cross(forward).add(forward.scale(y.dot(forward)));

        Vec3 z = y.cross(forward).normalize();

        int[] vertices = quad.getVertices().clone();
        VertexFormat format = DefaultVertexFormat.BLOCK;

        int stride = format.getIntegerSize();
        int uvOffset = format.getOffset(2) / Integer.BYTES;
        int normalOffset = format.getOffset(4) / Integer.BYTES;

        Vec3[] original = new Vec3[4];
        Vec3[] transformed = new Vec3[4];

        TextureAtlasSprite sprite = quad.getSprite();

        for (int i = 0; i < 4; i++) {
            int offset = i * stride;

            original[i] = new Vec3(
                    Float.intBitsToFloat(vertices[offset]),
                    Float.intBitsToFloat(vertices[offset + 1]),
                    Float.intBitsToFloat(vertices[offset + 2])
            );

            transformed[i] = transformPoint(original[i], x, y, z);

            vertices[offset] = Float.floatToRawIntBits((float) transformed[i].x);
            vertices[offset + 1] = Float.floatToRawIntBits((float) transformed[i].y);
            vertices[offset + 2] = Float.floatToRawIntBits((float) transformed[i].z);
        }

        Vec3 referenceNormal = original[1]
                .subtract(original[0])
                .cross(original[2].subtract(original[0]))
                .normalize();

        referenceNormal = transformDirection(
                referenceNormal,
                baseX,
                baseY,
                baseZ
        ).normalize();

        Direction projection = Direction.getNearest(
                referenceNormal.x,
                referenceNormal.y * (1.0 - DIRECTION_BIAS),
                referenceNormal.z
        );

        double minU = Double.POSITIVE_INFINITY;
        double maxU = Double.NEGATIVE_INFINITY;
        double minV = Double.POSITIVE_INFINITY;
        double maxV = Double.NEGATIVE_INFINITY;

        double[] projectedU = new double[4];
        double[] projectedV = new double[4];

        for (int i = 0; i < 4; i++) {
            projectedU[i] = getTextureU(projection, transformed[i]);
            projectedV[i] = getTextureV(projection, transformed[i]);

            minU = Math.min(minU, projectedU[i]);
            maxU = Math.max(maxU, projectedU[i]);
            minV = Math.min(minV, projectedV[i]);
            maxV = Math.max(maxV, projectedV[i]);
        }

        double uSize = maxU - minU;
        double vSize = maxV - minV;

        if (uSize > 1.0e-7 && vSize > 1.0e-7)
            for (int i = 0; i < 4; i++) {
                double u = (projectedU[i] - minU) / uSize;
                double v = (projectedV[i] - minV) / vSize;

                int offset = i * stride + uvOffset;

                vertices[offset] = Float.floatToRawIntBits(sprite.getU(u * 16.0));
                vertices[offset + 1] = Float.floatToRawIntBits(sprite.getV(v * 16.0));
            }

        Vec3 normal = transformed[1]
                .subtract(transformed[0])
                .cross(transformed[2].subtract(transformed[0]))
                .normalize();

        int packedNormal = packNormal(normal);

        for (int i = 0; i < 4; i++)
            vertices[i * stride + normalOffset] = packedNormal;

        return new BakedQuad(
                vertices,
                quad.getTintIndex(),
                Direction.getNearest(
                        normal.x,
                        normal.y,
                        normal.z
                ),
                quad.getSprite(),
                quad.isShade(),
                quad.hasAmbientOcclusion()
        );
    }

    private Vec3 transformDirection(
            Vec3 direction,
            Vec3 x,
            Vec3 y,
            Vec3 z
    ) {
        return x.scale(direction.x)
                .add(y.scale(direction.y))
                .add(z.scale(direction.z));
    }

    private double getTextureU(
            Direction direction,
            Vec3 point
    ) {
        return switch (direction) {
            case UP, DOWN, SOUTH -> point.x;
            case NORTH -> 1.0 - point.x;
            case WEST -> point.z;
            case EAST -> 1.0 - point.z;
        };
    }

    private double getTextureV(
            Direction direction,
            Vec3 point
    ) {
        return switch (direction) {
            case UP -> point.z;
            case DOWN -> 1.0 - point.z;
            case NORTH, SOUTH, WEST, EAST -> 1.0 - point.y;
        };
    }

    private Vec3 transformPoint(
            Vec3 point,
            Vec3 x,
            Vec3 y,
            Vec3 z
    ) {
        Vec3 local = point.subtract(0.5, 0.5, 0.5);

        return new Vec3(0.5, 0.5, 0.5)
                .add(x.scale(local.x))
                .add(y.scale(local.y))
                .add(z.scale(local.z));
    }

    private Vec3 direction(Direction direction) {
        return new Vec3(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ()
        );
    }

    private int packNormal(Vec3 normal) {
        int x = ((byte) (normal.x * 127.0)) & 0xFF;
        int y = ((byte) (normal.y * 127.0)) & 0xFF;
        int z = ((byte) (normal.z * 127.0)) & 0xFF;

        return x | (y << 8) | (z << 16);
    }

    private int findFace(BakedQuad quad) {
        List<Vec3> quadVertices = getQuadVertices(quad);
        for (int i = 0; i < shape.getFaces().size(); i++)
            if (sameVertices(quadVertices, shape.getFaces().get(i).getVertices()))
                return i;
        return -1;
    }

    private List<Vec3> getQuadVertices(BakedQuad quad) {
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

        return points;
    }

    private boolean sameVertices(
            List<Vec3> a,
            List<Vec3> b
    ) {
        if (a.size() != b.size())
            return false;
        for (Vec3 vertex : a) {
            boolean found = false;
            for (Vec3 other : b)
                if (vertex.distanceToSqr(other) < 1.0E-10) {
                    found = true;
                    break;
                }
            if (!found)
                return false;
        }
        return true;
    }

    private BakedQuad retexture(
            BakedQuad original,
            MaterialShapeBlockEntity.MaterialAssignment assignment
    ) {
        ResourceLocation location = assignment.material().getTexture(new Vector2i(assignment.x(), assignment.y()));
        TextureAtlasSprite target = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(location);

        int[] vertices = original.getVertices().clone();

        VertexFormat format = DefaultVertexFormat.BLOCK;
        int stride = format.getIntegerSize();
        int uvOffset = format.getOffset(2) / Integer.BYTES;

        TextureAtlasSprite source = original.getSprite();

        for (int i = 0; i < 4; i++) {
            int offset = i * stride + uvOffset;

            float u = Float.intBitsToFloat(vertices[offset]);
            float v = Float.intBitsToFloat(vertices[offset + 1]);

            double normalizedU = (u - source.getU0()) / (source.getU1() - source.getU0());
            double normalizedV = (v - source.getV0()) / (source.getV1() - source.getV0());

            vertices[offset] = Float.floatToRawIntBits(target.getU(normalizedU * 16.0));
            vertices[offset + 1] = Float.floatToRawIntBits(target.getV(normalizedV * 16.0));
        }

        return new BakedQuad(
                vertices,
                original.getTintIndex(),
                original.getDirection(),
                target,
                original.isShade(),
                original.hasAmbientOcclusion()
        );
    }
}
