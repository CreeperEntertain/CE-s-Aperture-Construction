package net.centertain.ceac.material.shapes.loaders.half_slopes.top;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slopes.top.HalfRoofEndTopGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfRoofEndTopLoader implements IGeometryLoader<HalfRoofEndTopGeometry> {
    INSTANCE;

    @Override
    public HalfRoofEndTopGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfRoofEndTopGeometry();
    }
}
