package net.centertain.ceac.material.shapes.loaders.quarter_slopes.fourth;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.quarter_slopes.fourth.QuarterCornerInnerFourthGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterCornerInnerFourthLoader implements IGeometryLoader<QuarterCornerInnerFourthGeometry> {
    INSTANCE;

    @Override
    public QuarterCornerInnerFourthGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterCornerInnerFourthGeometry();
    }
}
