package net.centertain.ceac.material.shapes.loaders.quarter_slopes.first;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.first.QuarterCornerInnerFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterCornerInnerFirstLoader implements IGeometryLoader<QuarterCornerInnerFirstGeometry> {
    INSTANCE;

    @Override
    public QuarterCornerInnerFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterCornerInnerFirstGeometry();
    }
}
