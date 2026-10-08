package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.basic_shapes.*;
import net.minecraftforge.client.event.ModelEvent;

public final class BasicShapes {
    private BasicShapes() {}

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("material_shape_bit", BitLoader.INSTANCE);
        event.register("material_shape_bit_pillar", BitPillarLoader.INSTANCE);
        event.register("material_shape_half", HalfLoader.INSTANCE);
        event.register("material_shape_stairs", StairsLoader.INSTANCE);

        event.register("material_shape_ninth", NinthLoader.INSTANCE);
        event.register("material_shape_ninth_pillar", NinthPillarLoader.INSTANCE);
        event.register("material_shape_third", ThirdLoader.INSTANCE);
        event.register("material_shape_third_stairs", ThirdStairsLoader.INSTANCE);
    }
}
