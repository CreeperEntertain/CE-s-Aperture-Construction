package net.centertain.ceac.block.custom.material_shapes.quarter_slopes.fourth;

import net.centertain.ceac.block.custom.MaterialShapeRotatable24Way;
import net.centertain.ceac.material.shapes.models.quarter_slopes.fourth.QuarterPyramidFourthGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class QuarterPyramidFourth extends MaterialShapeRotatable24Way {
    private static final Map<Direction, VoxelShape[]> SHAPES =
            MaterialShapeVoxelHelper24Way.makeShapes(() -> QuarterPyramidFourthGeometry.COLLISION_SHAPE);

    public QuarterPyramidFourth(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation") // Literally what the docs told me to use. Why would you deprecate something that's
    @Override                        // the only real way to do the thing? No, really. Enlighten me.
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(ROTATION)];
    }
}
