package net.centertain.ceac.material.shapes.loaders.round.thin_cylinders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.thin_cylinders.ThinCylinderEndGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThinCylinderEndLoader implements IGeometryLoader<ThinCylinderEndGeometry> {
    INSTANCE;

    @Override
    public ThinCylinderEndGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThinCylinderEndGeometry();
    }
}
