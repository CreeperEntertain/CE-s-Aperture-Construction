package net.centertain.ceac.material.shapes.loaders.round.cylinders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.cylinders.CylinderEndGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum CylinderEndLoader implements IGeometryLoader<CylinderEndGeometry> {
    INSTANCE;

    @Override
    public CylinderEndGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new CylinderEndGeometry();
    }
}
