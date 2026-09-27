package net.centertain.ceac.material.shapes.loaders.full_slopes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.full_slopes.FullRoofGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum FullRoofLoader implements IGeometryLoader<FullRoofGeometry> {
    INSTANCE;

    @Override
    public FullRoofGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new FullRoofGeometry();
    }
}
