package net.centertain.ceac.material.shapes.loaders.quarter_slope;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slope.QuarterSlopeSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterSlopeSecondLoader implements IGeometryLoader<QuarterSlopeSecondGeometry> {
    INSTANCE;

    @Override
    public QuarterSlopeSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterSlopeSecondGeometry();
    }
}
