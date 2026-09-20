package net.centertain.ceac.material.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.models.ModelShapeSlopeGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ModelShapeSlopeLoader implements IGeometryLoader<ModelShapeSlopeGeometry> {
    INSTANCE;

    @Override
    public ModelShapeSlopeGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ModelShapeSlopeGeometry();
    }
}
