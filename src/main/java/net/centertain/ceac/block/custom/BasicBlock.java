package net.centertain.ceac.block.custom;

import net.centertain.ceac.constants.CategoryConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
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

    @Override
    public boolean onDestroyedByPlayer(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            boolean willHarvest,
            @NotNull FluidState fluid
    ) {
        if (this instanceof FillableBlock fillable
                && fillable.tryDestroyFilledPiece(
                state,
                level,
                pos,
                player,
                willHarvest,
                fluid
        ))
            return false;

        playerWillDestroy(
                level,
                pos,
                state,
                player
        );

        return level.setBlock(
                pos,
                fluid.createLegacyBlock(),
                level.isClientSide
                        ? UPDATE_ALL_IMMEDIATE
                        : UPDATE_ALL
        );
    }
}
