package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.material.Material;
import net.centertain.ceac.material.MaterialBreakingParticle;
import net.centertain.ceac.material.MaterialShapeBlockEntity;
import net.centertain.ceac.material.MaterialShapeFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.function.Supplier;

public abstract class MatItem extends Item {
    private final Supplier<Material> material;
    private final String category;
    private final double price;

    private static final String OFFSET_X = "MaterialOffsetX";
    private static final String OFFSET_Y = "MaterialOffsetY";

    protected MatItem(
            @Nullable String category,
            double price,
            Properties properties,
            Supplier<Material> material
    ) {
        super(properties.stacksTo(1));
        this.material = material;
        this.category = category == null
                ? "Materials"
                : category;
        this.price = price;
    }

    public Material getMaterial() {
        return material.get();
    }

    public Vector2i getMaterialCoordinateOffset(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        return new Vector2i(
                tag.getInt(OFFSET_X),
                tag.getInt(OFFSET_Y)
        );
    }

    public Vector2i getMaterialCoordinateWithOffset(
            ItemStack stack,
            Vector2i materialCoordinate
    ) {
        Vector2i offset = getMaterialCoordinateOffset(stack);

        int width = material.get().getTilingSize().x;
        int height = material.get().getTilingSize().y;

        return new Vector2i(
                Math.floorMod(materialCoordinate.x + offset.x, width),
                Math.floorMod(materialCoordinate.y + offset.y, height)
        );
    }
    public final String getCategory() {
        return category;
    }
    public final double getPrice() {
        return price;
    }

    public void setMaterialCoordinateOffset(
            ItemStack stack,
            Vector2i offset
    ) {
        int width = material.get().getTilingSize().x;
        int height = material.get().getTilingSize().y;

        CompoundTag tag = stack.getOrCreateTag();

        tag.putInt(OFFSET_X, Math.floorMod(offset.x, width));
        tag.putInt(OFFSET_Y, Math.floorMod(offset.y, height));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof MaterialShape shape))
            return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return InteractionResult.PASS;

        Player player = context.getPlayer();

        if (player == null)
            return InteractionResult.PASS;

        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0F);
        Vec3 localOrigin = origin.subtract(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        localOrigin = shape.transformPointToLocal(state, localOrigin);
        direction = shape.transformDirectionToLocal(state, direction);

        MaterialShapeFace face = findFace(
                shape,
                localOrigin,
                direction,
                player.getBlockReach()
        );

        if (face == null)
            return InteractionResult.PASS;
        if (!shape.canApplyMaterial(state, face, context.getItemInHand()))
            return InteractionResult.PASS;

        Vector2i materialCoordinate = material.get().getCoordinate(face, pos);

        if (!level.isClientSide) {
            serverSide(
                    shape,
                    face,
                    blockEntity,
                    level,
                    pos,
                    state,
                    context.getItemInHand(),
                    materialCoordinate
            );
        }

        spawnMaterialParticles(
                level,
                pos,
                state,
                face,
                shape,
                context.getItemInHand(),
                materialCoordinate
        );

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void serverSide(
            MaterialShape shape,
            MaterialShapeFace face,
            MaterialShapeBlockEntity blockEntity,
            Level level,
            BlockPos pos,
            BlockState state,
            ItemStack stack,
            Vector2i materialCoordinate
    ) {
        int faceIndex = shape.getFaces().indexOf(face);

        blockEntity.setMaterial(
                faceIndex,
                material.get(),
                getMaterialCoordinateWithOffset(stack, materialCoordinate)
        );

        blockEntity.setChanged();
        blockEntity.requestModelDataUpdate();

        level.sendBlockUpdated(
                pos,
                state,
                state,
                Block.UPDATE_CLIENTS
        );

        Vec3 center = shape
                .transformPointToWorld(
                        state,
                        faceCenter(face)
                )
                .add(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                );

        SoundType soundType = material.get().getSoundType();

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                soundType.getVolume(),
                soundType.getPitch()
        );
    }

    private void spawnMaterialParticles(
            Level level,
            BlockPos pos,
            BlockState state,
            MaterialShapeFace face,
            MaterialShape shape,
            ItemStack stack,
            Vector2i materialCoordinate
    ) {
        if (!(level instanceof ClientLevel clientLevel))
            return;

        Vector2i coordinate = getMaterialCoordinateWithOffset(stack, materialCoordinate);

        ResourceLocation texture = material.get().getTexture(coordinate);

        if (texture == null)
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        RandomSource random = clientLevel.getRandom();

        for (int i = 0; i < 16; i++) {
            Vec3 point = shape.transformPointToWorld(state, randomPointOnFace(face, random));

            double xd = random.nextDouble() - 0.5D;
            double yd = random.nextDouble() - 0.5D;
            double zd = random.nextDouble() - 0.5D;

            Minecraft.getInstance().particleEngine.add(new MaterialBreakingParticle(
                    clientLevel,
                    pos.getX() + point.x,
                    pos.getY() + point.y,
                    pos.getZ() + point.z,
                    xd,
                    yd,
                    zd,
                    state,
                    pos,
                    sprite
            ));
        }
    }

    private Vec3 randomPointOnFace(
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

    private Vec3 randomPointOnTriangle(
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

    private Vec3 faceCenter(MaterialShapeFace face) {
        Vec3 center = Vec3.ZERO;

        for (Vec3 vertex : face.getVertices())
            center = center.add(vertex);

        return center.scale(1.0 / face.getVertices().size());
    }

    private @Nullable MaterialShapeFace findFace(
            MaterialShape shape,
            Vec3 origin,
            Vec3 direction,
            double reach
    ) {
        double closest = reach;
        MaterialShapeFace result = null;

        for (MaterialShapeFace face : shape.getFaces()) {
            List<Vec3> vertices = face.getVertices();

            if (vertices.size() < 3)
                continue;

            Vec3 a = vertices.get(0);

            for (int i = 1; i < vertices.size() - 1; i++) {
                double distance = rayTriangle(
                        origin,
                        direction,
                        a,
                        vertices.get(i),
                        vertices.get(i + 1),
                        reach
                );
                if (distance >= 0.0 && distance < closest) {
                    closest = distance;
                    result = face;
                }
            }
        }

        return result;
    }

    private double rayTriangle(
            Vec3 origin,
            Vec3 direction,
            Vec3 a,
            Vec3 b,
            Vec3 c,
            double reach
    ) {
        final double epsilon = 1.0E-7;

        Vec3 edge1 = b.subtract(a);
        Vec3 edge2 = c.subtract(a);

        Vec3 h = direction.cross(edge2);
        double determinant = edge1.dot(h);

        if (Math.abs(determinant) < epsilon)
            return -1.0;

        double inverse = 1.0 / determinant;

        Vec3 s = origin.subtract(a);
        double u = inverse * s.dot(h);

        if (u < 0.0 || u > 1.0)
            return -1.0;

        Vec3 q = s.cross(edge1);
        double v = inverse * direction.dot(q);

        if (v < 0.0 || u + v > 1.0)
            return -1.0;

        double distance = inverse * edge2.dot(q);

        return distance >= 0.0 && distance <= reach
                ? distance
                : -1.0;
    }
}
