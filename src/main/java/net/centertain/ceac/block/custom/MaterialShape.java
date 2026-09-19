package net.centertain.ceac.block.custom;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.material.Material;
import net.centertain.ceac.material.MaterialShapeBlockEntity;
import net.centertain.ceac.material.MaterialShapeFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public abstract class MaterialShape extends Block implements EntityBlock {
    private final List<MaterialShapeFace> faces;

    protected MaterialShape(Properties properties) {
        super(properties);
        this.faces = new ArrayList<>();
    }

    public final List<MaterialShapeFace> faces() {
        return faces;
    }

    @Override
    public BlockEntity newBlockEntity(
            @NotNull BlockPos pos,
            @NotNull BlockState state
    ) {
        return new MaterialShapeBlockEntity(pos, state);
    }

    public void createFaces(BakedModel model) {
        faces.clear();

        BlockState state = defaultBlockState();

        RandomSource random = RandomSource.create();

        for (BakedQuad quad : model.getQuads(
                state,
                null,
                random,
                ModelData.EMPTY,
                null
        ))
            faces.add(createFace(quad));

        for (Direction side : Direction.values())
            for (BakedQuad quad : model.getQuads(
                    state,
                    side,
                    random,
                    ModelData.EMPTY,
                    null
            ))
                faces.add(createFace(quad));
    }

    private MaterialShapeFace createFace(BakedQuad quad) {
        int [] vertices = quad.getVertices();
        VertexFormat format = DefaultVertexFormat.BLOCK;

        int stride = format.getIntegerSize();
        int positionOffset = format.getOffset(0) / Integer.BYTES;

        List<Vec3> points = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            int offset = i * stride + positionOffset;
            points.add(new Vec3(
                    Float.intBitsToFloat(vertices[offset]),
                    Float.intBitsToFloat(vertices[offset + 1]),
                    Float.intBitsToFloat(vertices[offset + 2])
            ));
        }

        return new MaterialShapeFace(null, points);
    }

    public final boolean applyMaterial(
            Level level,
            BlockPos pos,
            MaterialShapeFace face,
            Material material,
            Vector2i materialCoordinate
    ) {
        int index = faces.indexOf(face);

        if (index< 0)
            return false;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return false;

        blockEntity.setMaterial(index, material, materialCoordinate);
        return true;
    }

    public boolean canApplyMaterial(
            BlockState state,
            MaterialShapeFace face,
            ItemStack stack
    ) {
        return true;
    }
    public void applyMaterial(
            BlockState state,
            MaterialShapeFace face,
            ItemStack stack
    ) {}
    public void removeMaterial(
            BlockState state,
            MaterialShapeFace face
    ) {}
}
