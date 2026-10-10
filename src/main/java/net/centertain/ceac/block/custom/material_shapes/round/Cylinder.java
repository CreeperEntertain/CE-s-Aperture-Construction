package net.centertain.ceac.block.custom.material_shapes.round;

import net.centertain.ceac.block.custom.types.combinable.CombinableMaterialShapeTemplate6Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.models.round.cylinders.CylinderGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper6Way;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class Cylinder extends CombinableMaterialShapeTemplate6Way {
    private static final Map<Direction, VoxelShape> SHAPES =
            MaterialShapeVoxelHelper6Way.makeShapes(() -> CylinderGeometry.COLLISION_SHAPE);

    public Cylinder(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.ROUND,
                SHAPES
        );
    }
}
