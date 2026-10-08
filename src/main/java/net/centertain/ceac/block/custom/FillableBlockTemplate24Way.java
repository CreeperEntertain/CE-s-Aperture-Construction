package net.centertain.ceac.block.custom;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

public abstract class FillableBlockTemplate24Way extends MaterialShapeRotatable24Way implements FillableBlock {
    private IntegerProperty fill;

    protected FillableBlockTemplate24Way(
            Properties properties,
            String subcategory
    ) {
        super(properties, subcategory);
        registerDefaultState(defaultBlockState().setValue(fill, 1));
    }

    @Override
    protected void createBlockStateDefinition(
            @NotNull StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        fill = IntegerProperty.create("fill", 0, (1 << getMaxFillCount()) - 1);
        builder.add(fill);
    }

    @Override
    public IntegerProperty getFillProperty() {
        return fill;
    }
}
