package net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.quarters.QuarterGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum QuarterLoader implements IGeometryLoader<QuarterGeometry> {
    INSTANCE;

    public static final VoxelShape CANONICAL_SHAPE =
            MaterialShapeVoxelHelper.fromBounds(new QuarterGeometry());

    @Override
    public QuarterGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new QuarterGeometry();
    }
}
