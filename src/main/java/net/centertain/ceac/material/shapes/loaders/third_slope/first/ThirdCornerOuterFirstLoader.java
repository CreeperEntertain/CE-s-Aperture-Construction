package net.centertain.ceac.material.shapes.loaders.third_slope.first;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.first.ThirdCornerOuterFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdCornerOuterFirstLoader implements IGeometryLoader<ThirdCornerOuterFirstGeometry> {
    INSTANCE;

    @Override
    public ThirdCornerOuterFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdCornerOuterFirstGeometry();
    }
}
