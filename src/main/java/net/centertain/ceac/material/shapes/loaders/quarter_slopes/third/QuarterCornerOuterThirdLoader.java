package net.centertain.ceac.material.shapes.loaders.quarter_slopes.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.third.QuarterCornerOuterThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterCornerOuterThirdLoader implements IGeometryLoader<QuarterCornerOuterThirdGeometry> {
    INSTANCE;

    @Override
    public QuarterCornerOuterThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterCornerOuterThirdGeometry();
    }
}
