package net.centertain.ceac.material.shapes.loaders.basic_shapes.halves;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.halves.StairsGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum StairsLoader implements IGeometryLoader<StairsGeometry> {
    INSTANCE;

    @Override
    public StairsGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new StairsGeometry();
    }
}
