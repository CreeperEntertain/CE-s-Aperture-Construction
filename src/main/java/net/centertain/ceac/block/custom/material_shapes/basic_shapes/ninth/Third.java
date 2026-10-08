package net.centertain.ceac.block.custom.material_shapes.basic_shapes.ninth;

import net.centertain.ceac.block.custom.FillableMaterialShapeTemplate24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.ThirdLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.ThirdGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Third extends FillableMaterialShapeTemplate24Way {
    public Third(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.BASIC,
                MaterialShapeVoxelHelper24Way.makeCuboidShapes(() -> ThirdGeometry.COLLISION_SHAPE)
        );
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return ThirdLoader.CANONICAL_SHAPE;
    }
}
