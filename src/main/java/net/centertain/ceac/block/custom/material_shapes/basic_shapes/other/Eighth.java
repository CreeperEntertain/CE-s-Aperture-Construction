package net.centertain.ceac.block.custom.material_shapes.basic_shapes.other;

import net.centertain.ceac.block.custom.types.fillable.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.other.EighthLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.other.EighthGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Eighth extends FillableMaterialShapeTemplate24Way {
    public Eighth(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeCuboidShapes(() -> EighthGeometry.COLLISION_SHAPE)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return EighthLoader.CANONICAL_SHAPE;
    }
}
