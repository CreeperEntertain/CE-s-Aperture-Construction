package net.centertain.ceac.item.custom;

import net.centertain.ceac.CategoryConstants;
import net.minecraft.world.item.Item;

public abstract class BasicItem extends Item {
    private final String category;

    public BasicItem(Properties properties) {
        super(properties);
        this.category = CategoryConstants.Main.BASICS;
    }

    public BasicItem(Properties properties, String category) {
        super(properties);
        this.category = category;
    }

    public final String getCategory() {
        return category;
    }
}
