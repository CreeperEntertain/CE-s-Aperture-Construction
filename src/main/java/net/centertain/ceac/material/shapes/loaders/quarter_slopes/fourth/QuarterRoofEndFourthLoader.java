package net.centertain.ceac.material.shapes.loaders.quarter_slopes.fourth;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.fourth.QuarterRoofEndFourthGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterRoofEndFourthLoader implements IGeometryLoader<QuarterRoofEndFourthGeometry> {
    INSTANCE;

    @Override
    public QuarterRoofEndFourthGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterRoofEndFourthGeometry();
    }
}
