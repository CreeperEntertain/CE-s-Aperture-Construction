package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeThirdSlopeFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeThirdSlopeFirstLoader implements IGeometryLoader<MaterialShapeThirdSlopeFirstGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeThirdSlopeFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeThirdSlopeFirstGeometry();
    }
}
