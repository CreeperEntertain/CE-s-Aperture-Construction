package net.centertain.ceac.material.shapes.loaders.round.pipes_arches;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.pipes_arches.QuarterArchInnerGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterArchInnerLoader implements IGeometryLoader<QuarterArchInnerGeometry> {
    INSTANCE;

    @Override
    public QuarterArchInnerGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterArchInnerGeometry();
    }
}
