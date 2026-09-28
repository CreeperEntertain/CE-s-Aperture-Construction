package net.centertain.ceac.block.custom;

import net.centertain.ceac.CategoryConstants;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BasicBlock extends Block {
    private final String category;
    private final @Nullable String subcategory;
    private final double price;

    public BasicBlock(Properties properties, double price) {
        super(properties);
        this.category = CategoryConstants.Main.BASICS;
        this.subcategory = null;
        this.price = price;
    }

    public BasicBlock(Properties properties, String category, double price) {
        super(properties);
        this.category = category;
        this.subcategory = null;
        this.price = price;
    }

    public BasicBlock(Properties properties, String category, @Nullable String subcategory, double price) {
        super(properties);
        this.category = category;
        this.subcategory = subcategory;
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
