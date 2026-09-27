package net.centertain.ceac.material.shapes.loaders.third_slope.first;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.first.ThirdSlopeFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdSlopeFirstLoader implements IGeometryLoader<ThirdSlopeFirstGeometry> {
    INSTANCE;

    @Override
    public ThirdSlopeFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdSlopeFirstGeometry();
    }
}
