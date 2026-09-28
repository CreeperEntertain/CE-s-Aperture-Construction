package net.centertain.ceac.item.custom;

import net.centertain.ceac.CategoryConstants;
import net.minecraft.world.item.Item;

public abstract class BasicItem extends Item {
<<<<<<< Updated upstream
    private final String category;
=======
    protected final String category;
    protected final @Nullable String subcategory;
    protected final double price;
>>>>>>> Stashed changes

    public BasicItem(Properties properties, double price) {
        super(properties);
        this.category = CategoryConstants.Main.BASICS;
<<<<<<< Updated upstream
=======
        this.subcategory = null;
        this.price = price;
>>>>>>> Stashed changes
    }

    public BasicItem(Properties properties, String category, double price) {
        super(properties);
        this.category = category;
<<<<<<< Updated upstream
=======
        this.subcategory = null;
        this.price = price;
    }

    public BasicItem(Properties properties, String category, @Nullable String subcategory, double price) {
        super(properties);
        this.category = category;
        this.subcategory = subcategory;
        this.price = price;
>>>>>>> Stashed changes
    }

    public final String getCategory() {
        return category;
    }
<<<<<<< Updated upstream
=======
    public final @Nullable String getSubcategory() {
        return subcategory;
    }
    public final double getPrice() {
        return price;
    }
>>>>>>> Stashed changes
}
