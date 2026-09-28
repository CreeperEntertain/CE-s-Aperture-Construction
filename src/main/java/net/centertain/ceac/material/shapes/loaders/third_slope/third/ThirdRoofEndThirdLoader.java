package net.centertain.ceac.material.shapes.loaders.third_slope.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.third.ThirdRoofEndThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdRoofEndThirdLoader implements IGeometryLoader<ThirdRoofEndThirdGeometry> {
    INSTANCE;

    @Override
    public ThirdRoofEndThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdRoofEndThirdGeometry();
    }
}
