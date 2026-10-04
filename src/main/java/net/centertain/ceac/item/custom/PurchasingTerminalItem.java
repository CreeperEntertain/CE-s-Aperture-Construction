package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.ModBlocks;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class PurchasingTerminalItem extends BlockItem {
    public PurchasingTerminalItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    public static void registerItemProperties() {
        ItemProperties.register(
                ModBlocks.PURCHASING_TERMINAL.get().asItem(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "broken"),
                (stack, level, entity, id) -> {
                    if (!stack.hasTag())
                        return 0.0f;
                    CompoundTag blockStateTag = stack.getOrCreateTagElement(BlockItem.BLOCK_STATE_TAG);
                    return blockStateTag.getBoolean("broken")
                            ? 1.0f
                            : 0.0f;
                }
        );
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.getOrCreateTagElement(BLOCK_STATE_TAG)
                .putBoolean("broken", false);
        return stack;
    }
}
