package net.centertain.ceac.block.custom.material_shapes.half_slopes.bottom;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.block.custom.types.material_shapes.MaterialShapeRotatable24Way;
import net.centertain.ceac.material.shapes.models.half_slopes.bottom.HalfRoofBottomGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HalfRoofBottom extends MaterialShapeRotatable24Way {
    private static final Map<Direction, VoxelShape[]> SHAPES =
            MaterialShapeVoxelHelper24Way.makeShapes(() -> HalfRoofBottomGeometry.COLLISION_SHAPE);

    public HalfRoofBottom(Properties properties) {
        super(properties, CategoryConstants.Sub.Shapes.SLOPES_HALF);
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
