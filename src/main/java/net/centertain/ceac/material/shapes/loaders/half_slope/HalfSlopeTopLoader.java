package net.centertain.ceac.material.shapes.loaders.half_slope;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slope.HalfSlopeTopGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfSlopeTopLoader implements IGeometryLoader<HalfSlopeTopGeometry> {
    INSTANCE;

    @Override
    public HalfSlopeTopGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfSlopeTopGeometry();
    }
}
