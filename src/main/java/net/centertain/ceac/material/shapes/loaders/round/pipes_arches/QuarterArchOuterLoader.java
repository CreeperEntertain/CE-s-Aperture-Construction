package net.centertain.ceac.material.shapes.loaders.round.pipes_arches;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.round.pipes_arches.QuarterArchOuterGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterArchOuterLoader implements IGeometryLoader<QuarterArchOuterGeometry> {
    INSTANCE;

    @Override
    public QuarterArchOuterGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterArchOuterGeometry();
    }
}
