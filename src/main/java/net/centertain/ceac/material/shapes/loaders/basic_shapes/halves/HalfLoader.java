package net.centertain.ceac.material.shapes.loaders.basic_shapes.halves;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.halves.HalfGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfLoader implements IGeometryLoader<HalfGeometry> {
    INSTANCE;

    public static final VoxelShape CANONICAL_SHAPE =
            MaterialShapeVoxelHelper.makeShape(new HalfGeometry(), false);

    @Override
    public HalfGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfGeometry();
    }
}
