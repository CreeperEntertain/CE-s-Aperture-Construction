package net.centertain.ceac.block.custom;

import net.centertain.ceac.material.MaterialShapeFace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class MaterialShape extends Block {
    private final List<MaterialShapeFace> faces;

    protected MaterialShape(Properties properties, VoxelShape shape) {
        super(properties);
        this.faces = createFaces(shape);
    }

    private static List<MaterialShapeFace> createFaces(@Nullable VoxelShape shape) {
        List<MaterialShapeFace> faces = new ArrayList<>();

        if (shape == null)
            return faces;

        for (AABB box : shape.toAabbs()) {
            Vec3 min = new Vec3(box.minX, box.minY, box.minZ);
            Vec3 max = new Vec3(box.maxX, box.maxY, box.maxZ);

            // Down
            faces.add(new MaterialShapeFace(null, List.of(
                    new Vec3(min.x, min.y, min.z),
                    new Vec3(max.x, min.y, min.z),
                    new Vec3(max.x, min.y, max.z),
                    new Vec3(min.x, min.y, max.z)
            )));

            // Up
            faces.add(new MaterialShapeFace(null, List.of(
                    new Vec3(min.x, max.y, min.z),
                    new Vec3(min.x, max.y, max.z),
                    new Vec3(max.x, max.y, max.z),
                    new Vec3(max.x, max.y, min.z)
            )));

            // North
            faces.add(new MaterialShapeFace(null, List.of(
                    new Vec3(min.x, min.y, min.z),
                    new Vec3(min.x, max.y, min.z),
                    new Vec3(max.x, max.y, min.z),
                    new Vec3(max.x, min.y, min.z)
            )));

            // South
            faces.add(new MaterialShapeFace(null, List.of(
                    new Vec3(min.x, min.y, max.z),
                    new Vec3(max.x, min.y, max.z),
                    new Vec3(max.x, max.y, max.z),
                    new Vec3(min.x, max.y, max.z)
            )));

            // West
            faces.add(new MaterialShapeFace(null, List.of(
                    new Vec3(min.x, min.y, min.z),
                    new Vec3(min.x, min.y, max.z),
                    new Vec3(min.x, max.y, max.z),
                    new Vec3(min.x, max.y, min.z)
            )));

            // East
            faces.add(new MaterialShapeFace(null, List.of(
                    new Vec3(max.x, min.y, min.z),
                    new Vec3(max.x, max.y, min.z),
                    new Vec3(max.x, max.y, max.z),
                    new Vec3(max.x, min.y, max.z)
            )));
        }

        return faces;
    }

    public final List<MaterialShapeFace> faces() {
        return faces;
    }

    public boolean canApplyMaterial(
            BlockState state,
            MaterialShapeFace face,
            ItemStack stack
    ) {
        return true;
    }

    public void applyMaterial(
            BlockState state,
            MaterialShapeFace face,
            ItemStack stack
    ) {}

    public void removeMaterial(
            BlockState state,
            MaterialShapeFace face
    ) {}
}
