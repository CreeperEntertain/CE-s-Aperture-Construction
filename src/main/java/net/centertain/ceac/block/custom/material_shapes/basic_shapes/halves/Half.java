package net.centertain.ceac.block.custom.material_shapes.basic_shapes.halves;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.halves.HalfLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.halves.HalfGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Half extends FillableMaterialShapeTemplate24Way {
    public Half(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeShapes(() -> HalfGeometry.COLLISION_SHAPE, false)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return HalfLoader.CANONICAL_SHAPE;
    }
}
