package net.centertain.ceac.block.custom.material_shapes.basic_shapes;

import net.centertain.ceac.block.custom.MaterialShapeRotatable6Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.models.basic_shapes.HalfGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper6Way;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Half extends MaterialShapeRotatable6Way {
    private static final Map<Direction, VoxelShape> SHAPES =
            MaterialShapeVoxelHelper6Way.makeShapes(() -> HalfGeometry.COLLISION_SHAPE, false);

    public Half(Properties properties) {
        super(properties, CategoryConstants.Sub.Shapes.BASIC);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return SHAPES.get(state.getValue(FACING));
    }
}
