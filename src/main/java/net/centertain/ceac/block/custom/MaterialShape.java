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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

    private Vec3 lastActionLocation = new Vec3(0.0, 0.0, 0.0);

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

    public SoundType getSoundType(
            Vec3 position,
            LevelReader level,
            BlockPos pos,
            boolean setLocation
    ) {
        if (setLocation)
            lastActionLocation = position;
        MaterialShapeFace face = getNearestFace(position);
        if (face == null)
            return this.soundType;
        int faceIndex = faces.indexOf(face);
        if (faceIndex < 0)
            return this.soundType;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return this.soundType;
        Material material = blockEntity.getMaterial(faceIndex);
        if (material == null)
            return this.soundType;
        return material.getSoundType();
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

    public final @Nullable MaterialShapeFace getNearestFace(Vec3 hitPosition) {
        final double epsilon = 1.0e-6;

        @Nullable MaterialShapeFace closestFace = null;
        double shortestDistance = Double.POSITIVE_INFINITY;

        for (MaterialShapeFace face : faces) {
            List<Vec3> vertices = face.getVertices();
            if (vertices.size() < 3)
                continue;

            Vec3 a = vertices.get(0);
            Vec3 b = vertices.get(1);
            Vec3 c = vertices.get(2);

            Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

            // Signed distance between point & plane
            double signedDistance = hitPosition.subtract(a).dot(normal);

            // Face plane projection
            Vec3 projected = hitPosition.subtract(normal.scale(signedDistance));

            // Projection onto least parallel plane to face
            double nx = Math.abs(normal.x);
            double ny = Math.abs(normal.y);
            double nz = Math.abs(normal.z);

            int droppedAxis = nx >= ny && nx >= nz ? 0 : ny >= nz ? 1 : 2;

            double px;
            double py;

            switch (droppedAxis) {
                case 0 -> {
                    px = projected.y;
                    py = projected.z;
                } case 1 -> {
                    px = projected.x;
                    py = projected.z;
                } default -> {
                    px = projected.x;
                    py = projected.y;
                }
            }

            // Point-in-polygon test using raycasting
            boolean inside = false;

            for (int i = 0; i < vertices.size(); i++) {
                Vec3 v0 = vertices.get(i);
                Vec3 v1 = vertices.get((i + 1) % vertices.size());

                double x0;
                double y0;
                double x1;
                double y1;

                switch (droppedAxis) {
                    case 0 -> {
                        //noinspection SuspiciousNameCombination
                        x0 = v0.y;
                        y0 = v0.z;
                        //noinspection SuspiciousNameCombination
                        x1 = v1.y;
                        y1 = v1.z;
                    } case 1 -> {
                        x0 = v0.x;
                        y0 = v0.z;
                        x1 = v1.x;
                        y1 = v1.z;
                    } default -> {
                        x0 = v0.x;
                        y0 = v0.y;
                        x1 = v1.x;
                        y1 = v1.y;
                    }
                }

                // Is projected point on this edge?
                double edgeX = x1 - x0;
                double edgeY = y1 - y0;
                double pointX = px - x0;
                double pointY = py - y0;

                double cross = edgeX * pointY - edgeY * pointX;

                if (Math.abs(cross) <= epsilon) {
                    double dot = pointX * edgeX + pointY * edgeY;
                    double lengthSquared = edgeX * edgeX + edgeY * edgeY;

                    if (dot >= -epsilon && dot <= lengthSquared + epsilon) {
                        inside = true;
                        break;
                    }
                }

                // Standard raycasting toggle
                if ((y0 > py) != (y1 > py)) {
                    double intersectionX = (x1 - x0) * (py - y0) / (y1 - y0) + x0;
                    if (px < intersectionX)
                        inside = !inside;
                }
            }

            if (!inside)
                continue;

            double distance = Math.abs(signedDistance);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                closestFace = face;
            }
        }

        return closestFace;
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
