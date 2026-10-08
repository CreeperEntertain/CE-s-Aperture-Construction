package net.centertain.ceac.block.custom.material_shapes.basic_shapes.bit;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.BitLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.BitPillarLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.BitPillarGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BitPillar extends FillableMaterialShapeTemplate24Way {
    public BitPillar(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeShapes(() -> BitPillarGeometry.COLLISION_SHAPE, false)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return BitPillarLoader.CANONICAL_SHAPE;
    }
}
