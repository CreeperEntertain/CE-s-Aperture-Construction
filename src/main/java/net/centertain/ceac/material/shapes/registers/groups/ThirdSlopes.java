package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.third_slope.first.*;
import net.centertain.ceac.material.shapes.loaders.third_slope.second.*;
import net.centertain.ceac.material.shapes.loaders.third_slope.third.ThirdCornerInnerThirdLoader;
import net.centertain.ceac.material.shapes.loaders.third_slope.third.ThirdCornerOuterThirdLoader;
import net.centertain.ceac.material.shapes.loaders.third_slope.third.ThirdPyramidThirdLoader;
import net.centertain.ceac.material.shapes.loaders.third_slope.third.ThirdSlopeThirdLoader;
import net.minecraftforge.client.event.ModelEvent;

public final class ThirdSlopes {
    private ThirdSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_third_slope_first", ThirdSlopeFirstLoader.INSTANCE);

        event.register("material_shape_third_corner_outer_first", ThirdCornerOuterFirstLoader.INSTANCE);
        event.register("material_shape_third_corner_inner_first", ThirdCornerInnerFirstLoader.INSTANCE);

        event.register("material_shape_third_roof_first", ThirdRoofFirstLoader.INSTANCE);
        event.register("material_shape_third_roof_end_first", ThirdRoofEndFirstLoader.INSTANCE);
        event.register("material_shape_third_pyramid_first", ThirdPyramidFirstLoader.INSTANCE);


        event.register("material_shape_third_slope_second", ThirdSlopeSecondLoader.INSTANCE);

        event.register("material_shape_third_corner_outer_second", ThirdCornerOuterSecondLoader.INSTANCE);
        event.register("material_shape_third_corner_inner_second", ThirdCornerInnerSecondLoader.INSTANCE);

        event.register("material_shape_third_roof_second", ThirdRoofSecondLoader.INSTANCE);
        event.register("material_shape_third_roof_end_second", ThirdRoofEndSecondLoader.INSTANCE);
        event.register("material_shape_third_pyramid_second", ThirdPyramidSecondLoader.INSTANCE);


        event.register("material_shape_third_slope_third", ThirdSlopeThirdLoader.INSTANCE);

        event.register("material_shape_third_corner_outer_third", ThirdCornerOuterThirdLoader.INSTANCE);
        event.register("material_shape_third_corner_inner_third", ThirdCornerInnerThirdLoader.INSTANCE);
        event.register("material_shape_third_pyramic_third", ThirdPyramidThirdLoader.INSTANCE);
    }
}
