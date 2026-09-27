package net.centertain.ceac.material.shapes.loaders.full_slopes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.full_slopes.FullCornerOuterGeometry;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum FullCornerOuterLoader implements IGeometryLoader<FullCornerOuterGeometry> {
    INSTANCE;

    @Override
    public FullCornerOuterGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new FullCornerOuterGeometry();
    }
}
