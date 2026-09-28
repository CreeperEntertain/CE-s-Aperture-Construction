package net.centertain.ceac.material.shapes.loaders.quarter_slopes.first;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.first.QuarterRoofEndFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterRoofEndFirstLoader implements IGeometryLoader<QuarterRoofEndFirstGeometry> {
    INSTANCE;

    @Override
    public QuarterRoofEndFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterRoofEndFirstGeometry();
    }
}
