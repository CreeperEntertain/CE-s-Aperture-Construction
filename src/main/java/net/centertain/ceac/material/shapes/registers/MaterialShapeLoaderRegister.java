package net.centertain.ceac.material.shapes.registers;

import net.centertain.ceac.material.shapes.registers.groups.FullSlopes;
import net.centertain.ceac.material.shapes.registers.groups.HalfSlopes;
import net.centertain.ceac.material.shapes.registers.groups.QuarterSlopes;
import net.centertain.ceac.material.shapes.registers.groups.ThirdSlopes;
import net.minecraftforge.client.event.ModelEvent;

public final class MaterialShapeLoaderRegister {
    private MaterialShapeLoaderRegister() {}

    public static void registerLoaders(ModelEvent.RegisterGeometryLoaders event) {
        FullSlopes.register(event);
        HalfSlopes.register(event);
        ThirdSlopes.register(event);
        QuarterSlopes.register(event);
    }
}
