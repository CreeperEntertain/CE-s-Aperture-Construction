package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeQuarterSlopeFourthGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeQuarterSlopeFourthLoader implements IGeometryLoader<MaterialShapeQuarterSlopeFourthGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeQuarterSlopeFourthGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeQuarterSlopeFourthGeometry();
    }
}
