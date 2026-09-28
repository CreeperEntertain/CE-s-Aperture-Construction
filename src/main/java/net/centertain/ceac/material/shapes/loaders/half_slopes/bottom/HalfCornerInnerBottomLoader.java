package net.centertain.ceac.material.shapes.loaders.half_slopes.bottom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slopes.bottom.HalfCornerInnerBottomGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfCornerInnerBottomLoader implements IGeometryLoader<HalfCornerInnerBottomGeometry> {
    INSTANCE;

    @Override
    public HalfCornerInnerBottomGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfCornerInnerBottomGeometry();
    }
}
