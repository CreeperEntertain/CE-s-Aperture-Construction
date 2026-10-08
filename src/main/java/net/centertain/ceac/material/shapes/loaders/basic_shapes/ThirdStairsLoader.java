package net.centertain.ceac.material.shapes.loaders.basic_shapes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.ThirdStairsGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdStairsLoader implements IGeometryLoader<ThirdStairsGeometry> {
    INSTANCE;

    @Override
    public ThirdStairsGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdStairsGeometry();
    }
}
