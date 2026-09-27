package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeQuarterSlopeThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeQuarterSlopeThirdLoader implements IGeometryLoader<MaterialShapeQuarterSlopeThirdGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeQuarterSlopeThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeQuarterSlopeThirdGeometry();
    }
}
