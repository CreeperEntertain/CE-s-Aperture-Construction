package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.third_slope.first.ThirdSlopeFirstLoader;
import net.centertain.ceac.material.shapes.loaders.third_slope.second.ThirdSlopeSecondLoader;
import net.centertain.ceac.material.shapes.loaders.third_slope.third.ThirdSlopeThirdLoader;
import net.minecraftforge.client.event.ModelEvent;

public final class ThirdSlopes {
    private ThirdSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_third_slope_first", ThirdSlopeFirstLoader.INSTANCE);
        event.register("material_shape_third_slope_second", ThirdSlopeSecondLoader.INSTANCE);
        event.register("material_shape_third_slope_third", ThirdSlopeThirdLoader.INSTANCE);
    }
}
