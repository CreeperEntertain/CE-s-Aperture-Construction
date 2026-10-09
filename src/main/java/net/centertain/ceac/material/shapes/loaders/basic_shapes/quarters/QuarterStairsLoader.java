package net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.quarters.QuarterStairsGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterStairsLoader implements IGeometryLoader<QuarterStairsGeometry> {
    INSTANCE;

    @Override
    public QuarterStairsGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterStairsGeometry();
    }
}
