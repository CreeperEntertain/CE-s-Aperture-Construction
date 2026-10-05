package net.centertain.ceac.material.shapes.registers;

import net.centertain.ceac.material.shapes.registers.groups.*;
import net.minecraftforge.client.event.ModelEvent;

public final class MaterialShapeLoaderRegister {
    private MaterialShapeLoaderRegister() {}

    public static void registerLoaders(ModelEvent.RegisterGeometryLoaders event) {
        BasicShapes.register(event);
        FullSlopes.register(event);
        HalfSlopes.register(event);
        ThirdSlopes.register(event);
        QuarterSlopes.register(event);
    }
}
