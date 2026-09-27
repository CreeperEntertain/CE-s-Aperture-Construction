package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeQuarterSlopeFirstGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeQuarterSlopeFirstLoader implements IGeometryLoader<MaterialShapeQuarterSlopeFirstGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeQuarterSlopeFirstGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeQuarterSlopeFirstGeometry();
    }
}
