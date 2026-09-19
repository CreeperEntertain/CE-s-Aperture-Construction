package net.centertain.ceac.material;

import net.centertain.ceac.block_entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class MaterialShapeBlockEntity extends BlockEntity {
    public static final ModelProperty<Map<Integer, MaterialAssignment>> MATERIALS = new ModelProperty<>();

    private Map<Integer, MaterialAssignment> materials = Map.of();

    public MaterialShapeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATERIAL_SHAPE.get(), pos, state);
    }

    public void setMaterial(
            int face,
            Material material,
            Vector2i materialCoordinate
    ) {
        Map<Integer, MaterialAssignment> updated = new HashMap<>(materials);

        updated.put(face, new MaterialAssignment(
                material,
                materialCoordinate.x,
                materialCoordinate.y
        ));

        materials = Map.copyOf(updated);

        setChanged();
        requestModelDataUpdate();

        if (level != null)
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_CLIENTS
            );
    }

    public void removeMaterial(int face) {
        if (!materials.containsKey(face))
            return;

        Map<Integer, MaterialAssignment> updated = new HashMap<>(materials);

        updated.remove(face);
        materials = Map.copyOf(updated);

        setChanged();
        requestModelDataUpdate();

        if (level != null)
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_CLIENTS
            );
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(MATERIALS, materials).build();
    }

    public record MaterialAssignment(
            Material material,
            int x,
            int y
    ) {}
}
