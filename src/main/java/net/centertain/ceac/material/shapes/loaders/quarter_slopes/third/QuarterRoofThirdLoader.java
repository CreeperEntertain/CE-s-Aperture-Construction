package net.centertain.ceac.material.shapes.loaders.quarter_slopes.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.third.QuarterRoofThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterRoofThirdLoader implements IGeometryLoader<QuarterRoofThirdGeometry> {
    INSTANCE;

    @Override
    public QuarterRoofThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterRoofThirdGeometry();
    }
}
