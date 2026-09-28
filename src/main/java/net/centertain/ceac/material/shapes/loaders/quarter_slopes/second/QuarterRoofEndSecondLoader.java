package net.centertain.ceac.material.shapes.loaders.quarter_slopes.second;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.second.QuarterRoofEndSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterRoofEndSecondLoader implements IGeometryLoader<QuarterRoofEndSecondGeometry> {
    INSTANCE;

    @Override
    public QuarterRoofEndSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterRoofEndSecondGeometry();
    }
}
