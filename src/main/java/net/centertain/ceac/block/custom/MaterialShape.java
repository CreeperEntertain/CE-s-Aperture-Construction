package net.centertain.ceac.block.custom;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.material.Material;
import net.centertain.ceac.material.MaterialShapeBlockEntity;
import net.centertain.ceac.material.MaterialShapeFace;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public abstract class MaterialShape extends Block implements EntityBlock {
    private final List<MaterialShapeFace> faces;
    private final String category;
    private final double price;

    protected MaterialShape(
            @Nullable String category,
            double price,
            Properties properties
    ) {
        super(properties
                .sound(SoundType.NETHERITE_BLOCK)
        );
        this.faces = new ArrayList<>();
        this.category = category == null
                ? "Material Shapes"
                : category;
        this.price = price;
    }

    public final List<MaterialShapeFace> getFaces() {
        return faces;
    }
    public final String getCategory() {
        return category;
    }
    public final double getPrice() {
        return price;
    }

    public Vec3 transformPointToLocal(BlockState state, Vec3 point) {
        return point;
    }
    public Vec3 transformDirectionToLocal(BlockState state, Vec3 direction) {
        return direction;
    }
    public Vec3 transformPointToWorld(BlockState state, Vec3 point) {
        return point;
    }

    @Override
    public BlockEntity newBlockEntity(
            @NotNull BlockPos pos,
            @NotNull BlockState state
    ) {
        return new MaterialShapeBlockEntity(pos, state);
    }

    public abstract boolean rotateFromViewDirection(Vec3 viewDirection, boolean clockwise);

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
        return new MaterialShapeFace(this, null, getQuadVertices(quad));
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

        return List.copyOf(points);
    }

    public final MaterialShapeFace getNearestFace(
            Vec3 hitPosition
    ) {
        List<MaterialShapeFace> filteredFaces = new ArrayList<>();

        for (MaterialShapeFace face : faces)
            if (isPointOnFace(face, hitPosition))
                filteredFaces.add(face);

        return filteredFaces.get(0);
    }

    private boolean isPointOnFace(
            MaterialShapeFace face,
            Vec3 hitPosition
    ) {
        List<Vec3> vertices = face.getVertices();

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

        // TODO: Figure this shit out

        return true;
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
