package net.centertain.ceac.material.shapes.loaders.third_slope;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.third_slopes.second.ThirdSlopeSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdSlopeSecondLoader implements IGeometryLoader<ThirdSlopeSecondGeometry> {
    INSTANCE;

    @Override
    public ThirdSlopeSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdSlopeSecondGeometry();
    }
}
