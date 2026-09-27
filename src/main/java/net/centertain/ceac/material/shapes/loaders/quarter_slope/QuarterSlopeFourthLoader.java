package net.centertain.ceac.material.shapes.loaders.quarter_slope;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slope.QuarterSlopeFourthGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterSlopeFourthLoader implements IGeometryLoader<QuarterSlopeFourthGeometry> {
    INSTANCE;

    @Override
    public QuarterSlopeFourthGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterSlopeFourthGeometry();
    }
}
