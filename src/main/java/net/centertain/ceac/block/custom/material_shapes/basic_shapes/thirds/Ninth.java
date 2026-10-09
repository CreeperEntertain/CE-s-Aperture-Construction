package net.centertain.ceac.block.custom.material_shapes.basic_shapes.thirds;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.thirds.NinthLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.thirds.NinthGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Ninth extends FillableMaterialShapeTemplate24Way {
    public Ninth(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeCuboidShapes(() -> NinthGeometry.COLLISION_SHAPE)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return NinthLoader.CANONICAL_SHAPE;
    }
}
