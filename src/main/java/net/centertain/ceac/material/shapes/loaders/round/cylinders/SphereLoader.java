package net.centertain.ceac.material.shapes.loaders.round.cylinders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.cylinders.SphereGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum SphereLoader implements IGeometryLoader<SphereGeometry> {
    INSTANCE;

    @Override
    public SphereGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new SphereGeometry();
    }
}
