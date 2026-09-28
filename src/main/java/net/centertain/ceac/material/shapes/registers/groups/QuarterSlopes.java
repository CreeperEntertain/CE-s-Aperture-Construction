package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.quarter_slopes.first.*;
import net.centertain.ceac.material.shapes.loaders.quarter_slopes.fourth.*;
import net.centertain.ceac.material.shapes.loaders.quarter_slopes.second.*;
import net.centertain.ceac.material.shapes.loaders.quarter_slopes.third.*;
import net.minecraftforge.client.event.ModelEvent;

public final class QuarterSlopes {
    private QuarterSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_quarter_slope_first", QuarterSlopeFirstLoader.INSTANCE);

        event.register("material_shape_quarter_corner_outer_first", QuarterCornerOuterFirstLoader.INSTANCE);
        event.register("material_shape_quarter_corner_inner_first", QuarterCornerInnerFirstLoader.INSTANCE);

        event.register("material_shape_quarter_roof_first", QuarterRoofFirstLoader.INSTANCE);
        event.register("material_shape_quarter_roof_end_first", QuarterRoofEndFirstLoader.INSTANCE);
        event.register("material_shape_quarter_pyramid_first", QuarterPyramidFirstLoader.INSTANCE);


        event.register("material_shape_quarter_slope_second", QuarterSlopeSecondLoader.INSTANCE);

        event.register("material_shape_quarter_corner_outer_second", QuarterCornerOuterSecondLoader.INSTANCE);
        event.register("material_shape_quarter_corner_inner_second", QuarterCornerInnerSecondLoader.INSTANCE);

        event.register("material_shape_quarter_roof_second", QuarterRoofSecondLoader.INSTANCE);
        event.register("material_shape_quarter_roof_end_second", QuarterRoofEndSecondLoader.INSTANCE);
        event.register("material_shape_quarter_pyramid_second", QuarterPyramidSecondLoader.INSTANCE);


        event.register("material_shape_quarter_slope_third", QuarterSlopeThirdLoader.INSTANCE);

        event.register("material_shape_quarter_corner_outer_third", QuarterCornerOuterThirdLoader.INSTANCE);
        event.register("material_shape_quarter_corner_inner_third", QuarterCornerInnerThirdLoader.INSTANCE);

        event.register("material_shape_quarter_roof_third", QuarterRoofThirdLoader.INSTANCE);
        event.register("material_shape_quarter_roof_end_third", QuarterRoofEndThirdLoader.INSTANCE);
        event.register("material_shape_quarter_pyramid_third", QuarterPyramidThirdLoader.INSTANCE);


        event.register("material_shape_quarter_slope_fourth", QuarterSlopeFourthLoader.INSTANCE);

        event.register("material_shape_quarter_corner_outer_fourth", QuarterCornerOuterFourthLoader.INSTANCE);
        event.register("material_shape_quarter_corner_inner_fourth", QuarterCornerInnerFourthLoader.INSTANCE);

        event.register("material_shape_quarter_roof_fourth", QuarterRoofFourthLoader.INSTANCE);
        event.register("material_shape_quarter_roof_end_fourth", QuarterRoofEndFourthLoader.INSTANCE);
        event.register("material_shape_quarter_pyramid_fourth", QuarterPyramidFourthLoader.INSTANCE);
    }
}
