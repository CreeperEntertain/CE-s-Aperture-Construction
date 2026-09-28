package net.centertain.ceac.material.shapes.loaders.third_slope.first;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.first.ThirdRoofFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdRoofFirstLoader implements IGeometryLoader<ThirdRoofFirstGeometry> {
    INSTANCE;

    @Override
    public ThirdRoofFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdRoofFirstGeometry();
    }
}
