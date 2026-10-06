package net.centertain.ceac.material.shapes.utility;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.block.custom.FillableBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class FillableModelBuilder {
    private FillableModelBuilder() {}

    public static List<BakedQuad> buildQuads(
            FillableBlock fillable,
            BlockState state,
            Direction side,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        BakedModel pieceModel = fillable.getFillModel(state);
        List<BakedQuad> source = pieceModel.getQuads(
                state,
                side,
                random,
                data,
                renderType
        );

        int mask = fillable.getFillMask(state);

        FillableBlock.FillDefinition definition = FillableBlock.FillDefinition.fromModel(pieceModel, state);

        List<BakedQuad> result = new ArrayList<>();

        for (int index = 0; index < definition.size(); index++) {
            if ((mask & (1 << index)) == 0)
                continue;

            Vec3 offset = definition.offset(index);

            for (BakedQuad quad : source) {
                Direction face = quad.getDirection();

                if (!isExposed(definition, mask, index, face))
                    continue;

                result.add(translate(quad, offset));
            }
        }

        return result;
    }

    private static boolean isExposed(
            FillableBlock.FillDefinition definition,
            int mask,
            int index,
            Direction face
    ) {
        if (face == null)
            return true;

        int x = index % definition.x();
        int yz = index / definition.x();
        int y = yz % definition.y();
        int z = yz / definition.y();

        int neighborX = x + face.getStepX();
        int neighborY = y + face.getStepY();
        int neighborZ = z + face.getStepZ();

        if (!definition.contains(
                neighborX,
                neighborY,
                neighborZ
        ))
            return true;

        int neighborIndex = definition.index(
                neighborX,
                neighborY,
                neighborZ
        );

        return (mask & (1 << neighborIndex)) == 0;
    }

    private static BakedQuad translate(
            BakedQuad original,
            Vec3 offset
    ) {
        int[] vertices = original.getVertices().clone();

        VertexFormat format = DefaultVertexFormat.BLOCK;
        int stride = format.getIntegerSize();
        int positionOffset = format.getOffset(0) / Integer.BYTES;

        for (int i = 0; i < 4; i++) {
            int vertexOffset = i * stride + positionOffset;

            float x = Float.intBitsToFloat(vertices[vertexOffset]);
            float y = Float.intBitsToFloat(vertices[vertexOffset + 1]);
            float z = Float.intBitsToFloat(vertices[vertexOffset + 2]);

            vertices[vertexOffset] = Float.floatToRawIntBits((float) (x + offset.x));
            vertices[vertexOffset + 1] = Float.floatToRawIntBits((float) (y + offset.y));
            vertices[vertexOffset + 2] = Float.floatToRawIntBits((float) (z + offset.z));
        }

        return new BakedQuad(
                vertices,
                original.getTintIndex(),
                original.getDirection(),
                original.getSprite(),
                original.isShade(),
                original.hasAmbientOcclusion()
        );
    }
}
