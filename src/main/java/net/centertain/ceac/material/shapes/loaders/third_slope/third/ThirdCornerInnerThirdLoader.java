package net.centertain.ceac.material.shapes.loaders.third_slope.third;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.third.ThirdCornerInnerThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdCornerInnerThirdLoader implements IGeometryLoader<ThirdCornerInnerThirdGeometry> {
    INSTANCE;

    @Override
    public ThirdCornerInnerThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdCornerInnerThirdGeometry();
    }
}
