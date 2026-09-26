package net.centertain.ceac.material.shapes.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.MaterialShapeHalfSlopeBottomGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MaterialShapeHalfSlopeBottomLoader implements IGeometryLoader<MaterialShapeHalfSlopeBottomGeometry> {
    INSTANCE;

    @Override
    public MaterialShapeHalfSlopeBottomGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MaterialShapeHalfSlopeBottomGeometry();
    }
}
