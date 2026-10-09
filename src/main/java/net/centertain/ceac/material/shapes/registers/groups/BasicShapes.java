package net.centertain.ceac.material.shapes.registers.groups;

import net.centertain.ceac.material.shapes.loaders.basic_shapes.halves.BitLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.halves.BitPillarLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.halves.HalfLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.halves.StairsLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.other.EighthLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.other.LayerLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters.QuarterLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters.QuarterStairsLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters.SixteenthLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters.SixteenthPillarLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.thirds.NinthLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.thirds.NinthPillarLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.thirds.ThirdLoader;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.thirds.ThirdStairsLoader;
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

        event.register("material_shape_sixteenth", SixteenthLoader.INSTANCE);
        event.register("material_shape_sixteenth_pillar", SixteenthPillarLoader.INSTANCE);
        event.register("material_shape_quarter", QuarterLoader.INSTANCE);
        event.register("material_shape_quarter_stairs", QuarterStairsLoader.INSTANCE);

        event.register("material_shape_eighth", EighthLoader.INSTANCE);
        event.register("material_shape_layer", LayerLoader.INSTANCE);
    }
}
