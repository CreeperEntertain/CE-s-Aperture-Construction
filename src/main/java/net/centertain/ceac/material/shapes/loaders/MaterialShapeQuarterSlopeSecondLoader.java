package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeQuarterSlopeSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeQuarterSlopeSecondLoader implements IGeometryLoader<MaterialShapeQuarterSlopeSecondGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeQuarterSlopeSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeQuarterSlopeSecondGeometry();
    }
}
