package net.centertain.ceac.item.custom;

import net.centertain.ceac.CategoryConstants;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public abstract class BasicItem extends Item {
    protected final String category;
    protected final @Nullable String subcategory;

    public BasicItem(Properties properties) {
        super(properties);
        this.category = CategoryConstants.Main.BASICS;
        this.subcategory = null;
    }

    public BasicItem(Properties properties, String category) {
        super(properties);
        this.category = category;
        this.subcategory = null;
    }

    public BasicItem(Properties properties, String category, @Nullable String subcategory) {
        super(properties);
        this.category = category;
        this.subcategory = subcategory;
    }

    public final String getCategory() {
        return category;
    }
    public final @Nullable String getSubcategory() {
        return subcategory;
    }
}
