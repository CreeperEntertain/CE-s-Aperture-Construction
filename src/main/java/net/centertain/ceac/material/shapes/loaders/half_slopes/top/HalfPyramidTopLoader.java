package net.centertain.ceac.material.shapes.loaders.half_slopes.top;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slopes.top.HalfPyramidTopGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfPyramidTopLoader implements IGeometryLoader<HalfPyramidTopGeometry> {
    INSTANCE;

    @Override
    public HalfPyramidTopGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfPyramidTopGeometry();
    }
}
