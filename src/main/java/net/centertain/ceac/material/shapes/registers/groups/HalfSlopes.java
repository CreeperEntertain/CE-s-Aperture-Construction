package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.half_slopes.bottom.HalfSlopeBottomLoader;
import net.centertain.ceac.material.shapes.loaders.half_slopes.top.HalfSlopeTopLoader;
import net.minecraftforge.client.event.ModelEvent;

public final class HalfSlopes {
    private HalfSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_half_slope_bottom", HalfSlopeBottomLoader.INSTANCE);
        event.register("material_shape_half_slope_top", HalfSlopeTopLoader.INSTANCE);
    }
}
