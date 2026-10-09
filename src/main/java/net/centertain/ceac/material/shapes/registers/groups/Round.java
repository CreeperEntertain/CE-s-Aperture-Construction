package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.round.pipes_arches.*;
import net.centertain.ceac.material.shapes.loaders.round.cylinders.*;
import net.centertain.ceac.material.shapes.loaders.round.thin_cylinders.*;
import net.minecraftforge.client.event.ModelEvent;

public final class Round {
    private Round() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_eighth_sphere", EighthSphereLoader.INSTANCE);
        event.register("material_shape_quarter_cylinder", QuarterCylinderLoader.INSTANCE);
        event.register("material_shape_quarter_arch", QuarterArchLoader.INSTANCE);
        event.register("material_shape_quarter_arch_outer", QuarterArchOuterLoader.INSTANCE);
        event.register("material_shape_quarter_arch_inner", QuarterArchInnerLoader.INSTANCE);

        event.register("material_shape_sphere", SphereLoader.INSTANCE);
        event.register("material_shape_cylinder", CylinderLoader.INSTANCE);
        event.register("material_shape_cylinder_end", CylinderEndLoader.INSTANCE);
        event.register("material_shaoe_cylinder_bend", CylinderBendLoader.INSTANCE);
        event.register("material_shape_cylinder_t_cross", CylinderTCrossLoader.INSTANCE);

        event.register("material_shape_thin_cylinder", ThinCylinderLoader.INSTANCE);
        event.register("material_shape_thin_cylinder_end", ThinCylinderEndLoader.INSTANCE);
        event.register("material_shape_thin_cylinder_bend", ThinCylinderBendLoader.INSTANCE);
        event.register("material_shape_thin_cylinder_t_cross", ThinCylinderTCrossLoader.INSTANCE);
    }
}
