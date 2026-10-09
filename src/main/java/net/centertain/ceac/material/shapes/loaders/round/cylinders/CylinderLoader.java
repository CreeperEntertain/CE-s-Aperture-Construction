package net.centertain.ceac.material.shapes.loaders.round.cylinders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.cylinders.CylinderGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum CylinderLoader implements IGeometryLoader<CylinderGeometry> {
    INSTANCE;

    @Override
    public CylinderGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new CylinderGeometry();
    }
}
