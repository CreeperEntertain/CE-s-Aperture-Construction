package net.centertain.ceac.material;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.block.custom.MaterialShape;
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
        if (materials == null || materials.isEmpty())
            return original;

        List<BakedQuad> result = new ArrayList<>(original.size());

        for (BakedQuad quad : original) {
            int faceIndex = findFace(quad);

            MaterialShapeBlockEntity.MaterialAssignment assignment = materials.get(faceIndex);

            if (assignment == null)
                result.add(quad);
            else
                result.add(retexture(quad, assignment));
        }

        return result;
    }

    private int findFace(BakedQuad quad) {
        int[] vertices = quad.getVertices();

        for (int i = 0; i < shape.faces().size(); i++) {
            MaterialShapeFace face = shape.faces().get(i);
            if (sameVertices(vertices, face.getVertices()))
                return i;
        }

        return -1;
    }

    private boolean sameVertices(
            int[] quadVertices,
            List<Vec3> faceVertices
    ) {
        VertexFormat format = DefaultVertexFormat.BLOCK;
        int stride = format.getIntegerSize();
        int positionOffset = format.getOffset(0) / Integer.BYTES;
        if (faceVertices.size() != 4)
            return false;

        for (int i = 0; i < 4; i++) {
            int offset = i * stride + positionOffset;
            Vec3 vertex = faceVertices.get(i);

            if (Float.intBitsToFloat(quadVertices[offset]) != vertex.x)
                return false;
            if (Float.intBitsToFloat(quadVertices[offset + 1]) != vertex.y)
                return false;
            if (Float.intBitsToFloat(quadVertices[offset + 2]) != vertex.z)
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
