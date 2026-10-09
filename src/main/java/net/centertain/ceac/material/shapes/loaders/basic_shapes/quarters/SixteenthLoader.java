package net.centertain.ceac.material.shapes.loaders.basic_shapes.quarters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.centertain.ceac.material.shapes.models.basic_shapes.quarters.SixteenthGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum SixteenthLoader implements IGeometryLoader<SixteenthGeometry> {
    INSTANCE;

    public static final VoxelShape CANONICAL_SHAPE =
            MaterialShapeVoxelHelper.fromBounds(new SixteenthGeometry());

    @Override
    public SixteenthGeometry read(
            JsonObject jsonObject,
            JsonDeserializationContext deserializationContext
    ) throws JsonParseException {
        return new SixteenthGeometry();
    }
}
