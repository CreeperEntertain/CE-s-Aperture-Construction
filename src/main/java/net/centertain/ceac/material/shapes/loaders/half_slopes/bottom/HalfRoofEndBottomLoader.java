package net.centertain.ceac.material.shapes.loaders.half_slopes.bottom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.half_slopes.bottom.HalfRoofEndBottomGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum HalfRoofEndBottomLoader implements IGeometryLoader<HalfRoofEndBottomGeometry> {
    INSTANCE;

    @Override
    public HalfRoofEndBottomGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new HalfRoofEndBottomGeometry();
    }
}
