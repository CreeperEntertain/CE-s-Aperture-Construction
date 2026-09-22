package net.centertain.ceac.material;

import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;

public class MaterialShapeFace {
    private final MaterialShape owner;
    private @Nullable Material material;
    private Vector2i materialCoordinate;
    private final List<Vec3> vertices;

    public MaterialShapeFace(
            MaterialShape owner,
            @Nullable Material material,
            List<Vec3> vertices
    ) {
        this.owner = owner;
        this.material = material;
        this.materialCoordinate = new Vector2i(0, 0);
        this.vertices = List.copyOf(vertices);
    }

    public MaterialShape getOwner() {
        return owner;
    }
    public @Nullable Material getMaterial() {
        return material;
    }
    public Vector2i getMaterialCoordinate() {
        return materialCoordinate;
    }
    public List<Vec3> getVertices() {
        return vertices;
    }

    public void setMaterial(
            @Nullable Material material,
            BlockPos pos,
            BlockState state
    ) {
        this.material = material;
        this.materialCoordinate = material == null
                ? new Vector2i(0, 0)
                : material.getCoordinate(this, pos, state);
    }
    public boolean setMaterialCoordinate(Vector2i materialCoordinate) {
        if (material == null)
            return false;
        if (!material.containsCoordinate(materialCoordinate))
            return false;
        this.materialCoordinate = materialCoordinate;
        return true;
    }
}
