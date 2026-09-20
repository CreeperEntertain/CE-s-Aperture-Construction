package net.centertain.ceac.material.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.models.MaterialShapeSlopeGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeSlopeLoader implements IGeometryLoader<MaterialShapeSlopeGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeSlopeGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeSlopeGeometry();
    }
}
