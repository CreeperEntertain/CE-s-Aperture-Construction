package net.centertain.ceac.material.shapes.loaders.quarter_slopes.first;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.first.QuarterRoofFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterRoofFirstLoader implements IGeometryLoader<QuarterRoofFirstGeometry> {
    INSTANCE;

    @Override
    public QuarterRoofFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterRoofFirstGeometry();
    }
}
