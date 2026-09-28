package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.half_slopes.bottom.*;
import net.centertain.ceac.material.shapes.loaders.half_slopes.top.*;
import net.minecraftforge.client.event.ModelEvent;

public final class HalfSlopes {
    private HalfSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_half_slope_bottom", HalfSlopeBottomLoader.INSTANCE);

        event.register("material_shape_half_corner_outer_bottom", HalfCornerOuterBottomLoader.INSTANCE);
        event.register("material_shape_half_corner_inner_bottom", HalfCornerInnerBottomLoader.INSTANCE);

        event.register("material_shape_half_roof_bottom", HalfRoofBottomLoader.INSTANCE);
        event.register("material_shape_half_roof_end_bottom", HalfRoofEndBottomLoader.INSTANCE);
        event.register("material_shape_half_pyramid_bottom", HalfPyramidBottomLoader.INSTANCE);


        event.register("material_shape_half_slope_top", HalfSlopeTopLoader.INSTANCE);

        event.register("material_shape_half_corner_outer_top", HalfCornerOuterTopLoader.INSTANCE);
        event.register("material_shape_half_corner_inner_top", HalfCornerInnerTopLoader.INSTANCE);

        event.register("material_shape_half_roof_top", HalfRoofTopLoader.INSTANCE);
        event.register("material_shape_half_roof_end_top", HalfRoofEndTopLoader.INSTANCE);
        event.register("material_shape_half_pyramid_top", HalfPyramidTopLoader.INSTANCE);
    }
}
