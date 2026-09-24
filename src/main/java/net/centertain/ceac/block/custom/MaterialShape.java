package net.centertain.ceac.block.custom;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.material.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class MaterialShape extends Block implements EntityBlock {
    private final List<MaterialShapeFace> faces;
    private final String category;
    private final double price;

    private static final String DEFAULT_CATEGORY = "Material Shapes";
    private static final double DEFAULT_PRICE = 10.0;

    protected MaterialShape(
            @Nullable String category,
            @Nullable Double price,
            Properties properties
    ) {
        super(properties
                .sound(SoundType.NETHERITE_BLOCK)
        );
        this.faces = new ArrayList<>();
        this.category = category == null
                ? DEFAULT_CATEGORY
                : category;
        this.price = price == null
                ? DEFAULT_PRICE
                : price;
    }

    protected MaterialShape(Properties properties) {
        super(properties
                .sound(SoundType.NETHERITE_BLOCK)
        );
        this.faces = new ArrayList<>();
        this.category = DEFAULT_CATEGORY;
        this.price = DEFAULT_PRICE;
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
            BlockPos pos
    ) {
        position = transformPointToLocal(level.getBlockState(pos), position);
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

    @Override
    public void initializeClient(@NotNull Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            @Override
            public boolean addHitEffects(
                    @NotNull BlockState state,
                    @NotNull Level level,
                    @NotNull HitResult target,
                    @NotNull ParticleEngine manager) {
                if (!(level instanceof ClientLevel clientLevel))
                    return false;
                if (!(target instanceof BlockHitResult blockHit))
                    return false;

                BlockPos pos = blockHit.getBlockPos();
                Vec3 hit = blockHit.getLocation();
                Vec3 localHit = hit.subtract(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                );

                MaterialShapeFace face = getNearestFace(transformPointToLocal(state, localHit));
                if (face == null)
                    return false;

                int faceIndex = faces.indexOf(face);
                if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
                    return false;

                Material material = blockEntity.getMaterial(faceIndex);
                if (material == null)
                    return false;

                Vector2i coordinate = material.getCoordinate(face, pos, state);
                ResourceLocation texture = material.getTexture(coordinate);
                if (texture == null)
                    return false;

                TextureAtlasSprite sprite = Minecraft.getInstance()
                        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
                RandomSource random = clientLevel.getRandom();

                List<Vec3> vertices = face.getVertices();
                Vec3 a = vertices.get(0);
                Vec3 b = vertices.get(1);
                Vec3 c = vertices.get(2);

                Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();
                Vec3 localPoint = randomPointOnFace(face, random).add(normal.scale(0.1));
                Vec3 worldPoint = transformPointToWorld(state, localPoint).add(pos.getX(), pos.getY(), pos.getZ());

                manager.add(new MaterialBreakingParticle(
                        clientLevel,
                        worldPoint.x,
                        worldPoint.y,
                        worldPoint.z,
                        0.0,
                        0.0,
                        0.0,
                        state,
                        pos,
                        sprite
                ).setPower(0.2f).scale(0.6f));

                return true;
            }
        });
    }

    @Override
    public boolean addRunningEffects(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        Vec3 localPosition = entity.position().subtract(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
        MaterialShapeFace face = getNearestFace(transformPointToLocal(state, localPosition));
        if (face == null)
            return false;

        int faceIndex = faces.indexOf(face);
        if (faceIndex < 0)
            return false;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return false;

        Material material = blockEntity.getMaterial(faceIndex);
        if (material == null)
            return false;

        Vector2i coordinate = material.getCoordinate(face, pos, state);
        ResourceLocation texture = material.getTexture(coordinate);
        if (texture == null)
            return false;
        if (!(level instanceof ClientLevel clientLevel))
            return true;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

        Vec3 point = projectOntoFace(face, localPosition);

        List<Vec3> vertices = face.getVertices();

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

        Vec3 worldPoint = transformPointToWorld(state, point.add(normal.scale(0.1)))
                .add(pos.getX(), pos.getY(), pos.getZ());
        Vec3 movement = entity.getDeltaMovement();

        Minecraft.getInstance().particleEngine.add(
                new MaterialBreakingParticle(
                        clientLevel,
                        worldPoint.x,
                        worldPoint.y,
                        worldPoint.z,
                        movement.x * -4.0,
                        1.5,
                        movement.z * -4.0,
                        state,
                        pos,
                        sprite
                )
        );

        return true;
    }

    @Override
    public boolean addLandingEffects(
            @NotNull BlockState state1,
            @NotNull ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull BlockState state2, // What the fuck, Mojang?
            @NotNull LivingEntity entity,
            int numberOfParticles
    ) {
        Vec3 localPosition = entity.position().subtract(pos.getX(), pos.getY(), pos.getZ());
        localPosition = transformPointToLocal(state2, localPosition);

        MaterialShapeFace face = getNearestFace(localPosition);
        if (face == null)
            return false;

        int faceIndex = faces.indexOf(face);
        if (faceIndex < 0)
            return false;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return false;

        Material material = blockEntity.getMaterial(faceIndex);
        if (material == null)
            return false;

        Vector2i coordinate = material.getCoordinate(face, pos, state2);
        ResourceLocation texture = material.getTexture(coordinate);
        if (texture == null)
            return false;

        Vec3 point = projectOntoFace(face, localPosition);

        List<Vec3> vertices = face.getVertices();

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

        Vec3 worldPoint = transformPointToWorld(state2, point.add(normal.scale(0.1)))
                .add(pos.getX(), pos.getY(), pos.getZ());

        level.sendParticles(
                new MaterialParticleOptions(texture, pos),
                worldPoint.x,
                worldPoint.y,
                worldPoint.z,
                numberOfParticles,
                0.0,
                0.0,
                0.0,
                0.15
        );

        return true;
    }

    private Vec3 projectOntoFace(
            MaterialShapeFace face,
            Vec3 point
    ) {
        List<Vec3> vertices = face.getVertices();

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

        double distance = point.subtract(a).dot(normal);

        return point.subtract(normal.scale(distance));
    }

    public static Vec3 randomPointOnFace(
            MaterialShapeFace face,
            RandomSource random
    ) {
        List<Vec3> vertices = face.getVertices();

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        if (vertices.size() == 3 || random.nextBoolean())
            return randomPointOnTriangle(a, b, c, random);

        return randomPointOnTriangle(
                a,
                c,
                vertices.get(3),
                random
        );
    }

    public static Vec3 randomPointOnTriangle(
            Vec3 a,
            Vec3 b,
            Vec3 c,
            RandomSource random
    ) {
        double u = random.nextDouble();
        double v = random.nextDouble();

        if (u + v > 1.0) {
            u = 1.0 - u;
            v = 1.0 - v;
        }

        return a
                .add(b.subtract(a).scale(u))
                .add(c.subtract(a).scale(v));
    }

    public boolean rotateFromViewDirection(
            Level level,
            BlockPos pos,
            Vec3 viewDirection,
            boolean counterclockwise
    ) {
        return false;
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

    public boolean canApplyMaterial(
            BlockState state,
            MaterialShapeFace face,
            ItemStack stack
    ) {
        return true;
    }
}
