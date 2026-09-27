package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.quarter_slopes.first.QuarterSlopeFirstLoader;
import net.centertain.ceac.material.shapes.loaders.quarter_slopes.fourth.QuarterSlopeFourthLoader;
import net.centertain.ceac.material.shapes.loaders.quarter_slopes.second.QuarterSlopeSecondLoader;
import net.centertain.ceac.material.shapes.loaders.quarter_slopes.third.QuarterSlopeThirdLoader;
import net.minecraftforge.client.event.ModelEvent;

public final class QuarterSlopes {
    private QuarterSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_quarter_slope_first", QuarterSlopeFirstLoader.INSTANCE);
        event.register("material_shape_quarter_slope_second", QuarterSlopeSecondLoader.INSTANCE);
        event.register("material_shape_quarter_slope_third", QuarterSlopeThirdLoader.INSTANCE);
        event.register("material_shape_quarter_slope_fourth", QuarterSlopeFourthLoader.INSTANCE);
    }
}
