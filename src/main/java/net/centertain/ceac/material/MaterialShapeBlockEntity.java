package net.centertain.ceac.material;

import net.centertain.ceac.block_entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag materialsTag = new CompoundTag();

        for (Map.Entry<Integer, MaterialAssignment> entry : materials.entrySet()) {
            MaterialAssignment assignment = entry.getValue();

            CompoundTag materialTag = new CompoundTag();

            ResourceLocation id = ModMaterials.REGISTRY.get().getKey(assignment.material);
            if (id == null)
                continue;

            materialTag.putString("Id", id.toString());
            materialTag.putInt("X", assignment.x);
            materialTag.putInt("Y", assignment.y);

            materialsTag.put(String.valueOf(entry.getKey()), materialTag);
        }

        tag.put("Materials", materialsTag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        Map<Integer, MaterialAssignment> loaded = new HashMap<>();

        CompoundTag materialsTag = tag.getCompound("Materials");

        for (String key : materialsTag.getAllKeys()) {
            CompoundTag materialTag = materialsTag.getCompound(key);

            ResourceLocation id = ResourceLocation.tryParse(materialTag.getString("Id"));
            if (id == null)
                continue;

            Material material = ModMaterials.REGISTRY.get().getValue(id);
            if (material == null)
                continue;

            loaded.put(Integer.parseInt(key), new MaterialAssignment(
                    material,
                    materialTag.getInt("X"),
                    materialTag.getInt("Y")
            ));
        }

        materials = Map.copyOf(loaded);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(
            Connection connection,
            ClientboundBlockEntityDataPacket packet
    ) {
        super.onDataPacket(connection, packet);
        requestModelDataUpdate();

        if (level == null)
            return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(
                worldPosition,
                state,
                state,
                Block.UPDATE_CLIENTS
        );
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        requestModelDataUpdate();

        if (level == null)
            return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(
                worldPosition,
                state,
                state,
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
