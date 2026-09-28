package net.centertain.ceac.material.shapes.loaders.half_slopes.bottom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slopes.bottom.HalfPyramidBottomGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfPyramidBottomLoader implements IGeometryLoader<HalfPyramidBottomGeometry> {
    INSTANCE;

    @Override
    public HalfPyramidBottomGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfPyramidBottomGeometry();
    }
}
