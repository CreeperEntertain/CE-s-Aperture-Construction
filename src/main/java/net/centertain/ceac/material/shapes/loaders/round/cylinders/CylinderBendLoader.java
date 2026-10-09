package net.centertain.ceac.material.shapes.loaders.round.cylinders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.cylinders.CylinderBendGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum CylinderBendLoader implements IGeometryLoader<CylinderBendGeometry> {
    INSTANCE;

    @Override
    public CylinderBendGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new CylinderBendGeometry();
    }
}
