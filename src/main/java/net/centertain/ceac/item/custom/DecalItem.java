package net.centertain.ceac.item.custom;

import net.centertain.ceac.decal.AbstractDecal;
import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.DecalDefinition;
import net.centertain.ceac.decal.DecalPreviewer;
import net.centertain.ceac.decal.client.ClientDecals;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.DecalPlacement;
import net.centertain.ceac.screen.DecalItemScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class DecalItem extends Item {
    public DecalItem (Properties properties) {
        super(properties);
    }

    private @Nullable DecalDefinition decalDefinition = null;
    private @Nullable String decalResourceLocationPath = null;

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;
        assert hitResult != null;
        if (hitResult.getType() != HitResult.Type.MISS)
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide)
            minecraft.setScreen(new DecalItemScreen(hand));

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            @NotNull Level level,
            @NotNull Entity entity,
            int slot,
            boolean selected
    ) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!(level.isClientSide && entity instanceof Player player))
            return;
        assert Minecraft.getInstance().gameMode != null;
        if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR) {
            cleanup();
            return;
        }

        boolean offHand = player.getOffhandItem() == stack;
        if (!selected && !offHand)
            return;
        if (offHand && player.getMainHandItem().getItem() instanceof DecalItem)
            return;
        DecalPlacement.markDecalItemPresent();

        String fullLocation = stack.getOrCreateTag().getString("SelectedTexture");
        if (decalDefinition == null || !Objects.equals(decalResourceLocationPath, fullLocation)) {
            decalResourceLocationPath = fullLocation;
            if (fullLocation.isEmpty()) {
                cleanup();
                return;
            }
            ResourceLocation textureLocation = DecalLoader.getResourceLocationFromFullString(fullLocation);
            if (textureLocation == null) {
                cleanup();
                return;
            }
            decalDefinition = DecalLoader.getDefinitionFromResourceLocation(textureLocation);
            if (decalDefinition == null) { // To make the compiler shut the hell ip
                cleanup();                 // It couldn't ever be null at this stage, but fuck me ig
                return;
            }
        }

        BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            cleanup();
            return;
        }
        Vec3 surfacePosition = hitResult.getLocation();
        Vec3 surfaceNormal = new Vec3(
                hitResult.getDirection().getStepX(),
                hitResult.getDirection().getStepY(),
                hitResult.getDirection().getStepZ()
        );
        Vec3 placementPosition = surfacePosition.add(surfaceNormal.scale(0.001));
        double grid = 1.0 / 16.0;
        Vec3 snappedPosition = new Vec3(
                Math.round(placementPosition.x / grid) * grid,
                Math.round(placementPosition.y / grid) * grid,
                Math.round(placementPosition.z / grid) * grid
        );

        double blockDepth = 1.0;
        byte rotation = 0;

        AbstractDecal abstraction = DecalPlacement.getTempAbstractDecal();

        if (abstraction != null) {
            blockDepth = abstraction.getBlockDepth();
            rotation = abstraction.getRotation();
        }

        Set<BlockPos> attachedBlockSet = Decal.getAttachedBlockSet(
                level,
                snappedPosition,
                surfaceNormal,
                decalDefinition.getWidth(),
                decalDefinition.getHeight(),
                blockDepth,
                rotation
        );
        Decal decal = new Decal(
                UUID.randomUUID(),
                snappedPosition,
                surfaceNormal,
                ClientDecals.getHighestRenderingOrder() + 1,
                false,
                decalDefinition.getWidth(),
                decalDefinition.getHeight(),
                blockDepth,
                rotation,
                decalDefinition.getResourceLocation(),
                attachedBlockSet
        );
        DecalPlacement.setTempDecal(decal);
        DecalPlacement.setTempAbstractDecal(AbstractDecal.getAbstractFromDeltaChanges(decal, abstraction));
        DecalPlacement.setDecalPreview(new DecalPreviewer(decal));
    }

    private void cleanup() {
        DecalPlacement.setTempDecal(null);
        DecalPlacement.setDecalPreview(null);
    }
}
