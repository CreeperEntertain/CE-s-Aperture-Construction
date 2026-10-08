package net.centertain.ceac.material.shapes.utility;

import net.minecraft.world.item.context.BlockPlaceContext;

public final class FillableBlockPlaceContext extends BlockPlaceContext {
    private int initialFillIndex = -1;

    public FillableBlockPlaceContext(BlockPlaceContext context) {
        super(context);
    }

    public int getInitialFillIndex() {
        return initialFillIndex;
    }

    public void setInitialFillIndex(int initialFillIndex) {
        this.initialFillIndex = initialFillIndex;
    }
}
