package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.full_slopes.SlopeLoader;
import net.minecraftforge.client.event.ModelEvent;

public final class FullSlopes {
    private FullSlopes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_slope", SlopeLoader.INSTANCE);
    }
}
