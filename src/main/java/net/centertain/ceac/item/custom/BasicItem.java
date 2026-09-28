package net.centertain.ceac.item.custom;

import net.centertain.ceac.constants.CategoryConstants;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public abstract class BasicItem extends Item {
    private final String category;
    private final @Nullable String subcategory;
    private final double price;

    public BasicItem(Properties properties, double price) {
        super(properties);
        this.category = CategoryConstants.Main.BASICS;
        this.subcategory = null;
        this.price = price;
    }

    public BasicItem(Properties properties, String category, double price) {
        super(properties);
        this.category = category;
        this.subcategory = null;
        this.price = price;
    }

    public BasicItem(Properties properties, String category, @Nullable String subcategory, double price) {
        super(properties);
        this.category = subcategory;
        this.subcategory = null;
        this.price = price;
    }

    public final String getCategory() {
        return category;
    }
    public final @Nullable String getSubcategory() {
        return subcategory;
    }
    public final double getPrice() {
        return price;
    }
}
