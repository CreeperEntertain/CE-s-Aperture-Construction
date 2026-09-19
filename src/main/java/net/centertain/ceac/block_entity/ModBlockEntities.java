package net.centertain.ceac.block_entity;

import net.centertain.ceac.block.ModBlocks;
import net.centertain.ceac.material.MaterialShapeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<MaterialShapeBlockEntity>>
            MATERIAL_SHAPE = BLOCK_ENTITIES.register(
                    "material_shape",
                    () -> BlockEntityType.Builder.of(MaterialShapeBlockEntity::new,
                            ModBlocks.MATERIAL_SHAPE_BlOCK.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
