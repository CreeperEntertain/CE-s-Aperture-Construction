package net.centertain.ceac.item.custom;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.constants.PriceConstants;
import net.centertain.ceac.material.*;
import net.centertain.ceac.material.particle.MaterialBreakingParticle;
import net.centertain.ceac.material.preview.MaterialPlacement;
import net.centertain.ceac.material.preview.MaterialPreviewer;
import net.centertain.ceac.material.shapes.MaterialShapeBlockEntity;
import net.centertain.ceac.material.shapes.MaterialShapeFace;
import net.centertain.ceac.material.shapes.MaterialShapeFaceInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
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

public abstract class MatItem extends BasicItem {
    private final Supplier<Material> material;

    private static final String OFFSET_X = "MaterialOffsetX";
    private static final String OFFSET_Y = "MaterialOffsetY";

    private static final String DEFAULT_CATEGORY = CategoryConstants.Main.MATERIALS;
    private static final double DEFAULT_PRICE = PriceConstants.DEFAULT_MAT_ITEM;

    protected MatItem(
            @Nullable String category,
            String subcategory,
            @Nullable Double price,
            Properties properties,
            Supplier<Material> material
    ) {
        super(
                properties.stacksTo(1),
                category == null
                        ? DEFAULT_CATEGORY
                        : category,
                subcategory,
                price == null
                        ? DEFAULT_PRICE
                        : price
        );
        this.material = material;
    }

    protected MatItem(
        Properties properties,
        String subcategory,
        Supplier<Material> material
    ) {
        super(
                properties.stacksTo(1),
                DEFAULT_CATEGORY,
                subcategory,
                DEFAULT_PRICE
        );
        this.material = material;
    }

    public Material getMaterial() {
        return material.get();
    }

    public @NotNull BakedModel getBakedModel() {
        return Minecraft.getInstance().getItemRenderer().getModel(
                new ItemStack(this),
                Minecraft.getInstance().level,
                null,
                0
        );
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
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull Entity entity,
            int slot,
            boolean selected
    ) {
        if (!(entity instanceof Player player))
            return;
        if (!level.isClientSide)
            return;
        boolean offhand = player.getOffhandItem() == stack;
        if (!selected && !offhand)
            return;
        if (offhand && player.getMainHandItem().getItem() instanceof MatItem)
            return;
        MaterialPlacement.markMatItemPresent();
        if (!MaterialPlacement.getAdjustOffset()) {
            MaterialPreviewer.destroy();
            return;
        }

        HitResult hit = player.pick(
                player.getBlockReach(),
                1.0f,
                false
        );
        if (!(hit instanceof BlockHitResult blockHit)) {
            MaterialPlacement.forceCleanup();
            return;
        }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof MaterialShape shape)) {
            MaterialPlacement.forceCleanup();
            return;
        }

        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0f);
        Vec3 localOrigin = origin.subtract(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        localOrigin = shape.transformPointToLocal(state, localOrigin);
        direction = shape.transformDirectionToLocal(state, direction);

        FaceHit faceHit = findFace(
                shape,
                state,
                localOrigin,
                direction,
                player.getBlockReach()
        );
        if (faceHit == null) {
            MaterialPlacement.forceCleanup();
            return;
        }

        MaterialShapeFaceInstance instance = faceHit.instance;
        MaterialShapeFace face = instance.face();

        Vector2i materialCoordinate = material.get().getCoordinate(face, pos, state);

        MaterialPreviewer.update(
                material.get(),
                getMaterialCoordinateWithOffset(stack, materialCoordinate),
                pos,
                instance
        );
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

        FaceHit faceHit = findFace(
                shape,
                state,
                localOrigin,
                direction,
                player.getBlockReach()
        );
        if (faceHit == null)
            return InteractionResult.PASS;

        MaterialShapeFaceInstance instance = faceHit.instance;
        MaterialShapeFace face = instance.face();

        if (face == null)
            return InteractionResult.PASS;
        if (!shape.canApplyMaterial(state, face, context.getItemInHand()))
            return InteractionResult.PASS;

        Vector2i materialCoordinate = material.get().getCoordinate(face, pos, state);

        if (!level.isClientSide)
            serverSide(
                    shape,
                    face,
                    blockEntity,
                    level,
                    pos,
                    state,
                    context.getItemInHand(),
                    materialCoordinate,
                    instance
            );

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
            Vector2i materialCoordinate,
            MaterialShapeFaceInstance instance
    ) {
        blockEntity.setMaterial(
                instance.key(),
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
            Vec3 point = shape.transformPointToWorld(state, MaterialShape.randomPointOnFace(face, random));

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

    private Vec3 faceCenter(MaterialShapeFace face) {
        Vec3 center = Vec3.ZERO;

        for (Vec3 vertex : face.getVertices())
            center = center.add(vertex);

        return center.scale(1.0 / face.getVertices().size());
    }

    private @Nullable FaceHit findFace(
            MaterialShape shape,
            BlockState state,
            Vec3 origin,
            Vec3 direction,
            double reach
    ) {
        double closest = reach;
        @Nullable FaceHit result = null;

        for (MaterialShapeFaceInstance instance : shape.getFaceInstances(state)) {
            List<Vec3> vertices = instance.getVertices();
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
                    result = new FaceHit(instance, distance);
                }
            }
        }

        return result;
    }

    private record FaceHit(
            MaterialShapeFaceInstance instance,
            double distance
    ) {}

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
