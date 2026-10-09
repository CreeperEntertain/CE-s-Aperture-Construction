package net.centertain.ceac.material.shapes.loaders.basic_shapes.halves;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.halves.BitPillarGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum BitPillarLoader implements IGeometryLoader<BitPillarGeometry> {
    INSTANCE;

    public static final VoxelShape CANONICAL_SHAPE =
            MaterialShapeVoxelHelper.makeShape(new BitPillarGeometry(), false);

    @Override
    public BitPillarGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new BitPillarGeometry();
    }
}
