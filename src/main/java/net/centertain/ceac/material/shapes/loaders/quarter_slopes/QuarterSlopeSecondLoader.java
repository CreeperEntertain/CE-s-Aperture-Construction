package net.centertain.ceac.material.shapes.loaders.quarter_slopes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.QuarterSlopeSecondGeometry;
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
