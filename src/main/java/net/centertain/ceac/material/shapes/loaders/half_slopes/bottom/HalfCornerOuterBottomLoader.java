package net.centertain.ceac.material.shapes.loaders.half_slopes.bottom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slopes.bottom.HalfCornerOuterBottomGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfCornerOuterBottomLoader implements IGeometryLoader<HalfCornerOuterBottomGeometry> {
    INSTANCE;

    @Override
    public HalfCornerOuterBottomGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfCornerOuterBottomGeometry();
    }
}
