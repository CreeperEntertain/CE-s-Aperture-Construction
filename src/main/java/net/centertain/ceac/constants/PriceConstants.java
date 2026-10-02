package net.centertain.ceac.constants;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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

    public static @Nullable Double get(Item item) {
        return PRICES.get(item);
    }

    public static @Nullable Double get(ItemStack stack) {
        return get(stack.getItem());
    }
}
