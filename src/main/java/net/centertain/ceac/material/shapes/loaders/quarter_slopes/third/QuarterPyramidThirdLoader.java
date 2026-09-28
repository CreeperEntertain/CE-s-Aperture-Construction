package net.centertain.ceac.material.shapes.loaders.quarter_slopes.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.third.QuarterPyramidThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterPyramidThirdLoader implements IGeometryLoader<QuarterPyramidThirdGeometry> {
    INSTANCE;

    @Override
    public QuarterPyramidThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterPyramidThirdGeometry();
    }
}
