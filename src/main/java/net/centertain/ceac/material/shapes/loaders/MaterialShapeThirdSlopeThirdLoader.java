package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeThirdSlopeThirdGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeThirdSlopeThirdLoader implements IGeometryLoader<MaterialShapeThirdSlopeThirdGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeThirdSlopeThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeThirdSlopeThirdGeometry();
    }
}
