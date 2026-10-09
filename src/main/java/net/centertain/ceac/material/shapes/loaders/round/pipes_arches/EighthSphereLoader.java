package net.centertain.ceac.material.shapes.loaders.round.pipes_arches;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.pipes_arches.EighthSphereGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum EighthSphereLoader implements IGeometryLoader<EighthSphereGeometry> {
    INSTANCE;

    @Override
    public EighthSphereGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new EighthSphereGeometry();
    }
}
