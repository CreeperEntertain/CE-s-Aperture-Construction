package net.centertain.ceac.block.custom.material_shapes.quarter_slopes.second;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.block.custom.types.material_shapes.MaterialShapeRotatable24Way;
import net.centertain.ceac.material.shapes.models.quarter_slopes.second.QuarterCornerOuterSecondGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class QuarterCornerOuterSecond extends MaterialShapeRotatable24Way {
    private static final Map<Direction, VoxelShape[]> SHAPES =
            MaterialShapeVoxelHelper24Way.makeShapes(() -> QuarterCornerOuterSecondGeometry.COLLISION_SHAPE);

    public QuarterCornerOuterSecond(Properties properties) {
        super(properties, CategoryConstants.Sub.Shapes.SLOPES_QUARTER);
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
