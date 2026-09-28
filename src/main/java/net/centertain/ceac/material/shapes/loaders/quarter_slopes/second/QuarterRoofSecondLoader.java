package net.centertain.ceac.material.shapes.loaders.quarter_slopes.second;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.second.QuarterRoofSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterRoofSecondLoader implements IGeometryLoader<QuarterRoofSecondGeometry> {
    INSTANCE;

    @Override
    public QuarterRoofSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterRoofSecondGeometry();
    }
}
