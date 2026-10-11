package net.centertain.ceac.block.custom.material_shapes.round.thin_cylinders;

import net.centertain.ceac.block.custom.types.combinable.CombinableMaterialShapeTemplate6Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.models.round.thin_cylinders.ThinCylinderGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper6Way;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class ThinCylinder extends CombinableMaterialShapeTemplate6Way {
    private static final Map<Direction, VoxelShape> SHAPES =
            MaterialShapeVoxelHelper6Way.makeShapes(() -> ThinCylinderGeometry.COLLISION_SHAPE);

    public ThinCylinder(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.ROUND,
                SHAPES
        );
    }
}
