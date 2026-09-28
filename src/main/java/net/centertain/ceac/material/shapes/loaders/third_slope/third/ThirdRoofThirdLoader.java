package net.centertain.ceac.material.shapes.loaders.third_slope.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.third.ThirdRoofThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdRoofThirdLoader implements IGeometryLoader<ThirdRoofThirdGeometry> {
    INSTANCE;

    @Override
    public ThirdRoofThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdRoofThirdGeometry();
    }
}
