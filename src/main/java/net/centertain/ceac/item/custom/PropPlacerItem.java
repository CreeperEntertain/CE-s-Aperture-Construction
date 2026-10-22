package net.centertain.ceac.item.custom;

import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.client.render.PropPreviewer;
import net.centertain.ceac.prop.framework.enums.PropCollisionMode;
import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.framework.enums.PropMotionMode;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.UUID;

public class PropPlacerItem extends Item {
    public PropPlacerItem(Properties properties) {
        super(properties);
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

        boolean offhand = player.getOffhandItem() == stack;
        if (!selected && !offhand)
            return;
        if (offhand && player.getMainHandItem().getItem() instanceof PropPlacerItem)
            return;
        PropPreviewer.render(ModProps.TEST_PROP.getId());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.MISS)
            return InteractionResultHolder.pass(stack);

        if (!level.isClientSide)
            placeAt(level, player, hand, hit.getLocation(), hit.getDirection());

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        if (!context.getLevel().isClientSide) {
            boolean placed = placeAt(
                    context.getLevel(),
                    player,
                    context.getHand(),
                    context.getClickLocation(),
                    context.getClickedFace()
            );
            if (!placed)
                return InteractionResult.FAIL;

            SoundType soundType = ModProps.TEST_PROP.get().getSoundType();
            Vec3 location = context.getClickLocation();
            context.getLevel().playSound(
                    null,
                    location.x,
                    location.y,
                    location.z,
                    soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F,
                    soundType.getPitch() * 0.8F
            );
        }

        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean placeAt(
            Level level,
            Player player,
            InteractionHand hand,
            Vec3 hitLocation,
            Direction face
    ) {
        if (level.isClientSide)
            return false;

        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof PropPlacerItem))
            return false;

        Vec3 position = hitLocation.add(Vec3.atLowerCornerOf(face.getNormal()).scale(0.001));
        Matrix4f transform = new Matrix4f().translation(
                (float) position.x,
                (float) position.y,
                (float) position.z
        );
        Prop prop = new Prop(
                UUID.randomUUID(),
                ModProps.TEST_PROP.getId(),
                transform,
                PropCollisionMode.SOLID,
                PropMotionMode.DYNAMIC
        );
        Prop.placeInWorld(prop, level);
        if (!player.getAbilities().instabuild)
            stack.shrink(1);

        return true;
    }
}
