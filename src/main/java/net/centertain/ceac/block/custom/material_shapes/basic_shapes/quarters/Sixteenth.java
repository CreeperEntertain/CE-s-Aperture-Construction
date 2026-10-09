package net.centertain.ceac.block.custom.material_shapes.basic_shapes.quarters;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters.SixteenthLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.quarters.SixteenthGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Sixteenth extends FillableMaterialShapeTemplate24Way {
    public Sixteenth(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeCuboidShapes(() -> SixteenthGeometry.COLLISION_SHAPE)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return SixteenthLoader.CANONICAL_SHAPE;
    }
}
