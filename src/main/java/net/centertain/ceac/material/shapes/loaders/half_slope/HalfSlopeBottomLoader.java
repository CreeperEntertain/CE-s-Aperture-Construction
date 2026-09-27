package net.centertain.ceac.material.shapes.loaders.half_slope;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slope.HalfSlopeBottomGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfSlopeBottomLoader implements IGeometryLoader<HalfSlopeBottomGeometry> {
    INSTANCE;

    @Override
    public HalfSlopeBottomGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfSlopeBottomGeometry();
    }
}
