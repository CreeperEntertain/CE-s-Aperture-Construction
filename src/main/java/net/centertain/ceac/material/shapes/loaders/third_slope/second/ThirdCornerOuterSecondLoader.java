package net.centertain.ceac.material.shapes.loaders.third_slope.second;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.second.ThirdCornerOuterSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdCornerOuterSecondLoader implements IGeometryLoader<ThirdCornerOuterSecondGeometry> {
    INSTANCE;

    @Override
    public ThirdCornerOuterSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdCornerOuterSecondGeometry();
    }
}
