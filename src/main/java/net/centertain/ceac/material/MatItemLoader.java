package net.centertain.ceac.material;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum MatItemLoader implements IGeometryLoader<MatItemGeometry> {
    INSTANCE;

    @Override
    public MatItemGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new MatItemGeometry();
    }
}
