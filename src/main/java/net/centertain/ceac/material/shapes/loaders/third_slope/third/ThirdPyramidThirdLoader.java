package net.centertain.ceac.material.shapes.loaders.third_slope.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.third.ThirdPyramidThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdPyramidThirdLoader implements IGeometryLoader<ThirdPyramidThirdGeometry> {
    INSTANCE;

    @Override
    public ThirdPyramidThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdPyramidThirdGeometry();
    }
}
