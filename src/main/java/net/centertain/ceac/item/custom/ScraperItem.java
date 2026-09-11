package net.centertain.ceac.item.custom;

import net.centertain.ceac.decal.Decal;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ScraperItem extends Item {
    public ScraperItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);
        if (!(player instanceof ServerPlayer serverPlayer))
            return InteractionResultHolder.pass(stack);
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;
        assert hitResult != null;
        if (hitResult.getType() != HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(stack);
        BlockPos pos = BlockPos.containing(hitResult.getLocation());

        Set<UUID> decalIds = Decal.getDecalIdsOnBlockPos(pos, level);
        if (decalIds.isEmpty())
            return InteractionResultHolder.pass(stack);
        Map<UUID, Decal> decalMap = Decal.getDecalsInChunk(pos, level);
        List<Decal> decalList = new ArrayList<>();
        for (UUID id : decalIds)
            if (decalMap.containsKey(id))
                decalList.add(decalMap.get(id));
        decalList.sort(Comparator.comparing(Decal::getRenderingOrder).reversed()); // Highest to lowest
        Decal.removeFromWorld(decalList.get(0).getId(), pos, level, serverPlayer);

        return InteractionResultHolder.success(stack);
    }
}
