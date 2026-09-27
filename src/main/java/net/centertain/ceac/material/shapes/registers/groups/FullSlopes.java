package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.full_slopes.*;
import net.minecraftforge.client.event.ModelEvent;

public final class FullSlopes {
    private FullSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_slope", SlopeLoader.INSTANCE);

        event.register("material_shape_full_corner_outer", FullCornerOuterLoader.INSTANCE);
        event.register("material_shape_full_corner_inner", FullCornerInnerLoader.INSTANCE);

        event.register("material_shape_full_roof", FullRoofLoader.INSTANCE);
        event.register("material_shape_full_roof_end", FullRoofEndLoader.INSTANCE);
        event.register("material_shape_full_pyramid", FullPyramidLoader.INSTANCE);
    }
}
