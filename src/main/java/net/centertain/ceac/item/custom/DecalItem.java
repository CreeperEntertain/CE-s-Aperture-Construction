package net.centertain.ceac.item.custom;

import net.centertain.ceac.decal.DecalDefinition;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.screen.DecalItemScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        if (!selected) {
            decalDefinition = null;
            return;
        }
        String fullLocation = stack.getOrCreateTag().getString("SelectedTexture");
        if (decalDefinition == null || !Objects.equals(decalResourceLocationPath, fullLocation)) {
            decalResourceLocationPath = fullLocation;
            if (fullLocation.isEmpty())
                return;
            ResourceLocation textureLocation = DecalLoader.getResourceLocationFromFullString(fullLocation);
            if (textureLocation == null)
                return;
            decalDefinition = DecalLoader.getDefinitionFromResourceLocation(textureLocation);

            assert decalDefinition != null;
            System.out.println(decalDefinition.getName());
            System.out.println(decalDefinition.getResourceLocation().getPath());
        }

        // TODO: Actual placement preview and such
    }
}
