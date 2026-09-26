package net.centertain.ceac.block;

import net.centertain.ceac.block.custom.material_shapes.MaterialShapeBlock;
import net.centertain.ceac.block.custom.material_shapes.MaterialShapeHalfSlopeBottom;
import net.centertain.ceac.block.custom.material_shapes.MaterialShapeSlope;
import net.centertain.ceac.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);


    public static final RegistryObject<Block> MATERIAL_SHAPE_BlOCK = registerBlock("material_shape_block",
            () -> new MaterialShapeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_SLOPE = registerBlock("material_shape_slope",
            () -> new MaterialShapeSlope(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_BOTTOM = registerBlock("material_shape_half_slope_bottom",
            () -> new MaterialShapeHalfSlopeBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    private static <T extends Block> RegistryObject<T> registerBlock(
            String name,
            Supplier<T> block
    ) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        registerBlockItem(name, result);
        return result;
    }
    @SuppressWarnings("UnusedReturnValue")
    private static <T extends Block> RegistryObject<Item> registerBlockItem(
            String name,
            RegistryObject<T> block
    ) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private ModBlocks() {}

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
