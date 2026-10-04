package net.centertain.ceac.constants;

import net.centertain.ceac.block.custom.BasicBlock;
import net.centertain.ceac.item.custom.BasicItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PriceConstants {
    public static final double DEFAULT_MAT_ITEM = 1000.0;
    public static final double DEFAULT_SHAPE = 50.0;
    public static final double DEFAULT_TOOL = 500.0;

    public static final double DECAL = 150.0;

    private PriceConstants() {}

    private static final Map<Item, Double> PRICES = new HashMap<>();

    static {
        PRICES.put(Items.COAL, 10.0);
        PRICES.put(Items.CHARCOAL, 10.0);
        PRICES.put(Items.REDSTONE, 20.0);
        PRICES.put(Items.LAPIS_LAZULI, 25.0);
        PRICES.put(Items.QUARTZ, 35.0);
        PRICES.put(Items.COPPER_INGOT, 35.0);
        PRICES.put(Items.AMETHYST_SHARD, 40.0);
        PRICES.put(Items.IRON_INGOT, 100.0);
        PRICES.put(Items.GOLD_INGOT, 150.0);
        PRICES.put(Items.EMERALD, 250.0);
        PRICES.put(Items.DIAMOND, 500.0);
        PRICES.put(Items.NETHERITE_SCRAP, 600.0);
        PRICES.put(Items.NETHERITE_INGOT, 3000.0);
    }

    public static @Nullable Double get(@Nullable Item item) {
        Double potentialPrice = PRICES.get(item);
        if (potentialPrice == null)
            potentialPrice = getOtherPrice(item);
        return potentialPrice;
    }

    public static @Nullable Double get(@Nullable ItemStack stack) {
        if (stack == null)
            return null;
        return get(stack.getItem());
    }

    private static @Nullable Double getOtherPrice(@Nullable Item item) {
        Double value = null;
        if (item instanceof BasicItem basicItem)
            value = basicItem.getPrice();
        if (item instanceof BlockItem blockItem)
            if (blockItem.getBlock() instanceof BasicBlock basicBlock)
                value = basicBlock.getPrice();
        if (value == null)
            return null;
        return value * 0.85;
    }


    public static @NotNull List<Map.Entry<@NotNull Item, @NotNull Double>> getEntries() {
        return new ArrayList<>(PRICES.entrySet());
    }

    public static @NotNull List<Map.Entry<@NotNull Item, @NotNull Double>> getSortedEntries() {
        var entries = getEntries();
        entries.sort(Map.Entry.comparingByValue());
        return entries;
    }
}
