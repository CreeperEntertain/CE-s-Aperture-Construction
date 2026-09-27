package net.centertain.ceac.material.shapes.loaders.quarter_slopes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.first.QuarterSlopeFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterSlopeFirstLoader implements IGeometryLoader<QuarterSlopeFirstGeometry> {
    INSTANCE;

    @Override
    public QuarterSlopeFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterSlopeFirstGeometry();
    }
}
