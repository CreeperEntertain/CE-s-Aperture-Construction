package net.centertain.ceac.material.shapes.loaders.quarter_slopes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.third.QuarterSlopeThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterSlopeThirdLoader implements IGeometryLoader<QuarterSlopeThirdGeometry> {
    INSTANCE;

    @Override
    public QuarterSlopeThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterSlopeThirdGeometry();
    }
}
