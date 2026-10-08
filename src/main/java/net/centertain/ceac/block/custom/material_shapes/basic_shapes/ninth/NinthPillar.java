package net.centertain.ceac.block.custom.material_shapes.basic_shapes.ninth;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.NinthPillarLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.NinthPillarGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NinthPillar extends FillableMaterialShapeTemplate24Way {
    public NinthPillar(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeCuboidShapes(() -> NinthPillarGeometry.COLLISION_SHAPE)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return NinthPillarLoader.CANONICAL_SHAPE;
    }
}
