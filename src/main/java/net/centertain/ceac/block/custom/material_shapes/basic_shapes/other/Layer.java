package net.centertain.ceac.block.custom.material_shapes.basic_shapes.other;

import net.centertain.ceac.block.custom.types.fillable.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.other.LayerLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.other.LayerGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Layer extends FillableMaterialShapeTemplate24Way {
    public Layer(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeCuboidShapes(() -> LayerGeometry.COLLISION_SHAPE)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return LayerLoader.CANONICAL_SHAPE;
    }
}
