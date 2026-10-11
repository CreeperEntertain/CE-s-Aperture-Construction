package net.centertain.ceac.block.custom.material_shapes.round.cylinders;

import net.centertain.ceac.block.custom.types.material_shapes.MaterialShapeStatic;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.models.round.cylinders.SphereGeometry;

public class Sphere extends MaterialShapeStatic {
    public Sphere(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Shapes.ROUND,
                () -> SphereGeometry.COLLISION_SHAPE
        );
    }
}
