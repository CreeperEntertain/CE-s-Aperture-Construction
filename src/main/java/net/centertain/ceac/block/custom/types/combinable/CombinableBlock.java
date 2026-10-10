package net.centertain.ceac.block.custom.types.combinable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CombinableBlock {
    BooleanProperty X_FILLED = BooleanProperty.create("x_filled");
    BooleanProperty Y_FILLED = BooleanProperty.create("y_filled");
    BooleanProperty Z_FILLED = BooleanProperty.create("z_filled");

    static BooleanProperty propertyForAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> X_FILLED;
            case Y -> Y_FILLED;
            case Z -> Z_FILLED;
        };
    }

    static int pieceIndexForAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> 0;
            case Y -> 1;
            case Z -> 2;
        };
    }

    default boolean isFilled(BlockState state, Direction.Axis axis) {
        return state.getValue(propertyForAxis(axis));
    }

    default int getPieceCount(BlockState state) {
        int count = 0;
        for (Direction.Axis axis : Direction.Axis.values())
            if (isFilled(state, axis))
                count++;
        return count;
    }

    Direction.Axis getInsertionAxis(BlockState state, Direction clickedFace);

    VoxelShape getAxisShape(BlockState state, Direction.Axis axis);

    default VoxelShape getCombinableShape(BlockState state) {
        VoxelShape shape = Shapes.empty();
        for (Direction.Axis axis : Direction.Axis.values())
            if (isFilled(state, axis))
                shape = Shapes.or(shape, getAxisShape(state, axis));
        return shape;
    }
}