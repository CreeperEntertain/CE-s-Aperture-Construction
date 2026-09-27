package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeThirdSlopeSecondGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeThirdSlopeSecondLoader implements IGeometryLoader<MaterialShapeThirdSlopeSecondGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeThirdSlopeSecondGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeThirdSlopeSecondGeometry();
    }
}
