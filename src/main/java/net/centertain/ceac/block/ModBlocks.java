package net.centertain.ceac.block;

import net.centertain.ceac.block.custom.FillableBlock;
import net.centertain.ceac.block.custom.PurchasingTerminal;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.Bit;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.Half;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.Stairs;
import net.centertain.ceac.block.custom.material_shapes.full_slopes.*;
import net.centertain.ceac.block.custom.material_shapes.half_slopes.bottom.*;
import net.centertain.ceac.block.custom.material_shapes.half_slopes.top.*;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.first.*;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.fourth.*;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.second.*;
import net.centertain.ceac.block.custom.material_shapes.quarter_slopes.third.*;
import net.centertain.ceac.block.custom.material_shapes.third_slopes.first.*;
import net.centertain.ceac.block.custom.material_shapes.third_slopes.second.*;
import net.centertain.ceac.block.custom.material_shapes.third_slopes.third.*;
import net.centertain.ceac.item.ModItems;
import net.centertain.ceac.item.custom.FillableBlockItem;
import net.centertain.ceac.item.custom.PurchasingTerminalItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);


    public static final RegistryObject<Block> PURCHASING_TERMINAL = registerBlock("purchasing_terminal", () -> new PurchasingTerminal(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), block -> new PurchasingTerminalItem(block, new Item.Properties()));


    public static final RegistryObject<Block> MATERIAL_SHAPE_BlOCK = registerBlock("material_shape_block", () -> new net.centertain.ceac.block.custom.material_shapes.basic_shapes.Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_BIT = registerBlock("material_shape_bit", () -> new Bit(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF = registerBlock("material_shape_half", () -> new Half(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_STAIRS = registerBlock("material_shape_stairs", () -> new Stairs(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_SLOPE = registerBlock("material_shape_slope", () -> new Slope(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_CORNER_OUTER = registerBlock("material_shape_full_corner_outer", () -> new FullCornerOuter(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_CORNER_INNER = registerBlock("material_shape_full_corner_inner", () -> new FullCornerInner(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_ROOF = registerBlock("material_shape_full_roof", () -> new FullRoof(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_ROOF_END = registerBlock("material_shape_full_roof_end", () -> new FullRoofEnd(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_PYRAMID = registerBlock("material_shape_full_pyramid", () -> new FullPyramid(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_BOTTOM = registerBlock("material_shape_half_slope_bottom", () -> new HalfSlopeBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_OUTER_BOTTOM = registerBlock("material_shape_half_corner_outer_bottom", () -> new HalfCornerOuterBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_INNER_BOTTOM = registerBlock("material_shape_half_corner_inner_bottom", () -> new HalfCornerInnerBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_BOTTOM = registerBlock("material_shape_half_roof_bottom", () -> new HalfRoofBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_END_BOTTOM = registerBlock("material_shape_half_roof_end_bottom", () -> new HalfRoofEndBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_PYRAMID_BOTTOM = registerBlock("material_shape_half_pyramid_bottom", () -> new HalfPyramidBottom(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_TOP = registerBlock("material_shape_half_slope_top", () -> new HalfSlopeTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_OUTER_TOP = registerBlock("material_shape_half_corner_outer_top", () -> new HalfCornerOuterTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_INNER_TOP = registerBlock("material_shape_half_corner_inner_top", () -> new HalfCornerInnerTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_TOP = registerBlock("material_shape_half_roof_top", () -> new HalfRoofTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_END_TOP = registerBlock("material_shape_half_roof_end_top", () -> new HalfRoofEndTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_PYRAMID_TOP = registerBlock("material_shape_half_pyramid_top", () -> new HalfPyramidTop(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_FIRST = registerBlock("material_shape_third_slope_first", () -> new ThirdSlopeFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_OUTER_FIRST = registerBlock("material_shape_third_corner_outer_first", () -> new ThirdCornerOuterFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_INNER_FIRST = registerBlock("material_shape_third_corner_inner_first", () -> new ThirdCornerInnerFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_FIRST = registerBlock("material_shape_third_roof_first", () -> new ThirdRoofFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_END_FIRST = registerBlock("material_shape_third_roof_end_first", () -> new ThirdRoofEndFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_PYRAMID_FIRST = registerBlock("material_shape_third_pyramid_first", () -> new ThirdPyramidFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_SECOND = registerBlock("material_shape_third_slope_second", () -> new ThirdSlopeSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_OUTER_SECOND = registerBlock("material_shape_third_corner_outer_second", () -> new ThirdCornerOuterSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_INNER_SECOND = registerBlock("material_shape_third_corner_inner_second", () -> new ThirdCornerInnerSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_SECOND = registerBlock("material_shape_third_roof_second", () -> new ThirdRoofSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_END_SECOND = registerBlock("material_shape_third_roof_end_second", () -> new ThirdRoofEndSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_PYRAMID_SECOND = registerBlock("material_shape_third_pyramid_second", () -> new ThirdPyramidSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_THIRD = registerBlock("material_shape_third_slope_third", () -> new ThirdSlopeThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_OUTER_THIRD = registerBlock("material_shape_third_corner_outer_third", () -> new ThirdCornerOuterThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_INNER_THIRD = registerBlock("material_shape_third_corner_inner_third", () -> new ThirdCornerInnerThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_THIRD = registerBlock("material_shape_third_roof_third", () -> new ThirdRoofThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_END_THIRD = registerBlock("material_shape_third_roof_end_third", () -> new ThirdRoofEndThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_PYRAMID_THIRD = registerBlock("material_shape_third_pyramid_third", () -> new ThirdPyramidThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_FIRST = registerBlock("material_shape_quarter_slope_first", () -> new QuarterSlopeFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FIRST = registerBlock("material_shape_quarter_corner_outer_first", () -> new QuarterCornerOuterFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_FIRST = registerBlock("material_shape_quarter_corner_inner_first", () -> new QuarterCornerInnerFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_FIRST = registerBlock("material_shape_quarter_roof_first", () -> new QuarterRoofFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_FIRST = registerBlock("material_shape_quarter_roof_end_first", () -> new QuarterRoofEndFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_FIRST = registerBlock("material_shape_quarter_pyramid_first", () -> new QuarterPyramidFirst(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_SECOND = registerBlock("material_shape_quarter_slope_second", () -> new QuarterSlopeSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_SECOND = registerBlock("material_shape_quarter_corner_outer_second", () -> new QuarterCornerOuterSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_SECOND = registerBlock("material_shape_quarter_corner_inner_second", () -> new QuarterCornerInnerSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_SECOND = registerBlock("material_shape_quarter_roof_second", () -> new QuarterRoofSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_SECOND = registerBlock("material_shape_quarter_roof_end_second", () -> new QuarterRoofEndSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_SECOND = registerBlock("material_shape_quarter_pyramid_second", () -> new QuarterPyramidSecond(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_THIRD = registerBlock("material_shape_quarter_slope_third", () -> new QuarterSlopeThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_THIRD = registerBlock("material_shape_quarter_corner_outer_third", () -> new QuarterCornerOuterThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_THIRD = registerBlock("material_shape_quarter_corner_inner_third", () -> new QuarterCornerInnerThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_THIRD = registerBlock("material_shape_quarter_roof_third", () -> new QuarterRoofThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_THIRD = registerBlock("material_shape_quarter_roof_end_third", () -> new QuarterRoofEndThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_THIRD = registerBlock("material_shape_quarter_pyramid_third", () -> new QuarterPyramidThird(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_FOURTH = registerBlock("material_shape_quarter_slope_fourth", () -> new QuarterSlopeFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FOURTH = registerBlock("material_shape_quarter_corner_outer_fourth", () -> new QuarterCornerOuterFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_FOURTH = registerBlock("material_shape_quarter_corner_inner_fourth", () -> new QuarterCornerInnerFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_FOURTH = registerBlock("material_shape_quarter_roof_fourth", () -> new QuarterRoofFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_FOURTH = registerBlock("material_shape_quarter_roof_end_fourth", () -> new QuarterRoofEndFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_FOURTH = registerBlock("material_shape_quarter_pyramid_fourth", () -> new QuarterPyramidFourth(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));


    private static <T extends Block> RegistryObject<T> registerBlock(
            String name,
            Supplier<T> block
    ) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        registerBlockItem(name, result);
        return result;
    }
    @SuppressWarnings("SameParameterValue")
    private static <T extends Block> RegistryObject<T> registerBlock(
            String name,
            Supplier<T> block,
            Function<T, Item> item
    ) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> item.apply(result.get()));
        return result;
    }
    @SuppressWarnings("UnusedReturnValue")
    private static <T extends Block> RegistryObject<Item> registerBlockItem(
            String name,
            RegistryObject<T> block
    ) {
        return ModItems.ITEMS.register(name, () -> {
            T blockInstance = block.get();
            if (blockInstance instanceof FillableBlock)
                return new FillableBlockItem(blockInstance, new Item.Properties());
            return new BlockItem(blockInstance, new Item.Properties());
        });
    }

    private ModBlocks() {}

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
