package net.centertain.ceac.block;

import net.centertain.ceac.block.custom.material_shapes.*;
import net.centertain.ceac.block.custom.material_shapes.full_slopes.*;
import net.centertain.ceac.block.custom.material_shapes.half_slopes.bottom.HalfSlopeBottom;
import net.centertain.ceac.block.custom.material_shapes.half_slopes.top.HalfSlopeTop;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.first.QuarterSlopeFirst;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.fourth.QuarterSlopeFourth;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.second.QuarterSlopeSecond;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.third.QuarterSlopeThird;
import net.centertain.ceac.block.custom.material_shapes.third_slopes.first.ThirdSlopeFirst;
import net.centertain.ceac.block.custom.material_shapes.third_slopes.second.ThirdSlopeSecond;
import net.centertain.ceac.block.custom.material_shapes.third_slopes.third.ThirdSlopeThird;
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
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_SLOPE = registerBlock("material_shape_slope",
            () -> new Slope(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_CORNER_OUTER = registerBlock("material_shape_full_corner_outer",
            () -> new FullCornerOuter(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_CORNER_INNER = registerBlock("material_shape_full_corner_inner",
            () -> new FullCornerInner(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_ROOF = registerBlock("material_shape_full_roof",
            () -> new FullRoof(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_ROOF_END = registerBlock("material_shape_full_roof_end",
            () -> new FullRoofEnd(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_PYRAMID = registerBlock("material_shape_full_pyramid",
            () -> new FullPyramid(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_BOTTOM = registerBlock("material_shape_half_slope_bottom",
            () -> new HalfSlopeBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_TOP = registerBlock("material_shape_half_slope_top",
            () -> new HalfSlopeTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_FIRST = registerBlock("material_shape_third_slope_first",
            () -> new ThirdSlopeFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_SECOND = registerBlock("material_shape_third_slope_second",
            () -> new ThirdSlopeSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_THIRD = registerBlock("material_shape_third_slope_third",
            () -> new ThirdSlopeThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_FIRST = registerBlock("material_shape_quarter_slope_first",
            () -> new QuarterSlopeFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_SECOND = registerBlock("material_shape_quarter_slope_second",
            () -> new QuarterSlopeSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_THIRD = registerBlock("material_shape_quarter_slope_third",
            () -> new QuarterSlopeThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_FOURTH = registerBlock("material_shape_quarter_slope_fourth",
            () -> new QuarterSlopeFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


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
