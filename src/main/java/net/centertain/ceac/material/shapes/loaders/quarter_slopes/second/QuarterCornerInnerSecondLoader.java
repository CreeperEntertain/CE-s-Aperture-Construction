package net.centertain.ceac.material.shapes.loaders.quarter_slopes.second;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.second.QuarterCornerInnerSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterCornerInnerSecondLoader implements IGeometryLoader<QuarterCornerInnerSecondGeometry> {
    INSTANCE;

    @Override
    public QuarterCornerInnerSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterCornerInnerSecondGeometry();
    }
}
