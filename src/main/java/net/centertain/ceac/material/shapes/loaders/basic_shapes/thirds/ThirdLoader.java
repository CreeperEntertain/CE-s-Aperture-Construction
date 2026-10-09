package net.centertain.ceac.material.shapes.loaders.basic_shapes.thirds;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.thirds.ThirdGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum ThirdLoader implements IGeometryLoader<ThirdGeometry> {
    INSTANCE;

    public static final VoxelShape CANONICAL_SHAPE =
            MaterialShapeVoxelHelper.fromBounds(new ThirdGeometry());

    @Override
    public ThirdGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new ThirdGeometry();
    }
}
