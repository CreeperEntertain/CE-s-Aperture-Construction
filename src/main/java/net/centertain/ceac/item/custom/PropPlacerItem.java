package net.centertain.ceac.item.custom;

import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.client.render.PropPreviewer;
import net.centertain.ceac.prop.framework.enums.PropCollisionMode;
import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.framework.enums.PropMotionMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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

        if (!level.isClientSide) {
            Vec3 normal = Vec3.atLowerCornerOf(hit.getDirection().getNormal());
            Vec3 position = hit.getLocation().add(normal.scale(0.001));
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
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
