package net.centertain.ceac.material.shapes.loaders.full_slopes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.full_slopes.FullCornerInnerGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum FullCornerInnerLoader implements IGeometryLoader<FullCornerInnerGeometry> {
    INSTANCE;

    @Override
    public FullCornerInnerGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new FullCornerInnerGeometry();
    }
}
