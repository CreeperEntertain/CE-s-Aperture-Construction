package net.centertain.ceac.block.custom.material_shapes;

import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class MaterialShapeSlope extends MaterialShape {
    private static final VoxelShape SHAPE = makeShape();

    public MaterialShapeSlope(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return SHAPE;
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();

        for (int x = 0; x < 16; x++) {
            shape = Shapes.or(
                    shape,
                    Block.box(
                            x,
                            0,
                            0,
                            x + 1,
                            x + 1,
                            16
                    )
            );
        }

        return shape;
    }
}
