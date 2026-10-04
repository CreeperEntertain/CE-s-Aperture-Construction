package net.centertain.ceac.item.custom;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class PurchasingTerminalItem extends BlockItem {
    public PurchasingTerminalItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTagElement(BLOCK_STATE_TAG)
                .putBoolean("broken", false);
        return stack;
    }
}
