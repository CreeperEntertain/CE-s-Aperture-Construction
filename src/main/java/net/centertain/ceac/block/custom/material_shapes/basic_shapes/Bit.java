package net.centertain.ceac.block.custom.material_shapes.basic_shapes;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.BitLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.BitGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Bit extends FillableMaterialShapeTemplate24Way {
    public Bit(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeShapes(() -> BitGeometry.COLLISION_SHAPE, false)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return BitLoader.CANONICAL_SHAPE;
    }
}
