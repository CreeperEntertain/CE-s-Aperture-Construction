package net.centertain.ceac.material.shapes.loaders.basic_shapes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.HalfGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfLoader implements IGeometryLoader<HalfGeometry> {
    INSTANCE;

    @Override
    public HalfGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfGeometry();
    }
}
