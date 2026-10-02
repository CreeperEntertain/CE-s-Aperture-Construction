package net.centertain.ceac.constants;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

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
        //PRICES.put(Items.);
        PRICES.put(Items.COPPER_INGOT, 25.0);
        PRICES.put(Items.IRON_INGOT, 100.0);
    }
}
