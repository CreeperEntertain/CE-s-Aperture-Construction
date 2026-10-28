package net.centertain.ceac.block;

import net.centertain.ceac.block.custom.material_shapes.round.cylinders.*;
import net.centertain.ceac.block.custom.material_shapes.round.pipes_arches.*;
import net.centertain.ceac.block.custom.material_shapes.round.thin_cylinders.ThinCylinder;
import net.centertain.ceac.block.custom.material_shapes.round.thin_cylinders.ThinCylinderBend;
import net.centertain.ceac.block.custom.material_shapes.round.thin_cylinders.ThinCylinderEnd;
import net.centertain.ceac.block.custom.material_shapes.round.thin_cylinders.ThinCylinderTCross;
import net.centertain.ceac.block.custom.types.combinable.CombinableBlock;
import net.centertain.ceac.block.custom.types.fillable.FillableBlock;
import net.centertain.ceac.block.custom.PurchasingTerminal;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.halves.*;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.other.Eighth;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.other.Layer;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.quarters.Quarter;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.quarters.QuarterStairs;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.quarters.Sixteenth;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.quarters.SixteenthPillar;
import net.centertain.ceac.block.custom.material_shapes.basic_shapes.thirds.*;
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
import net.centertain.ceac.item.custom.types.CombinableBlockItem;
import net.centertain.ceac.item.custom.types.FillableBlockItem;
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
    private static final BlockBehaviour.Properties MATERIAL_SHAPE_PROPERTIES = BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);


    public static final RegistryObject<Block> PURCHASING_TERMINAL = registerBlock("purchasing_terminal", () -> new PurchasingTerminal(MATERIAL_SHAPE_PROPERTIES), block -> new PurchasingTerminalItem(block, new Item.Properties()));


    public static final RegistryObject<Block> MATERIAL_SHAPE_BlOCK = registerBlock("material_shapes/basic_shapes/block", () -> new net.centertain.ceac.block.custom.material_shapes.basic_shapes.Block(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_BIT = registerBlock("material_shapes/basic_shapes/halves/bit", () -> new Bit(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_BIT_PILLAR = registerBlock("material_shapes/basic_shapes/halves/bit_pillar", () -> new BitPillar(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF = registerBlock("material_shapes/basic_shapes/halves/half", () -> new Half(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_STAIRS = registerBlock("material_shapes/basic_shapes/halves/stairs", () -> new Stairs(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_NINTH = registerBlock("material_shapes/basic_shapes/thirds/ninth", () -> new Ninth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_NINTH_PILLAR = registerBlock("material_shapes/basic_shapes/thirds/ninth_pillar", () -> new NinthPillar(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD = registerBlock("material_shapes/basic_shapes/thirds/third", () -> new Third(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_STAIRS = registerBlock("material_shapes/basic_shapes/thirds/third_stairs", () -> new ThirdStairs(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_SIXTEENTH = registerBlock("material_shapes/basic_shapes/quarters/sixteenth", () -> new Sixteenth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_SIXTEENTH_PILLAR = registerBlock("material_shapes/basic_shapes/quarters/sixteenth_pillar", () -> new SixteenthPillar(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER = registerBlock("material_shapes/basic_shapes/quarters/quarter", () -> new Quarter(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_STAIRS = registerBlock("material_shapes/basic_shapes/quarters/quarter_stairs", () -> new QuarterStairs(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_EIGHTH = registerBlock("material_shapes/basic_shapes/other/eighth", () -> new Eighth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_LAYER = registerBlock("material_shapes/basic_shapes/other/layer", () -> new Layer(MATERIAL_SHAPE_PROPERTIES));


    public static final RegistryObject<Block> MATERIAL_SHAPE_EIGHTH_SPHERE = registerBlock("material_shape_eighth_sphere", () -> new EighthSphere(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CYLINDER = registerBlock("material_shape_quarter_cylinder", () -> new QuarterCylinder(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ARCH = registerBlock("material_shape_quarter_arch", () -> new QuarterArch(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ARCH_OUTER = registerBlock("material_shape_quarter_arch_outer", () -> new QuarterArchOuter(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ARCH_INNER = registerBlock("material_shape_quarter_arch_inner", () -> new QuarterArchInner(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_SPHERE = registerBlock("material_shape_sphere", () -> new Sphere(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_CYLINDER = registerBlock("material_shape_cylinder", () -> new Cylinder(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_CYLINDER_END = registerBlock("material_shape_cylinder_end", () -> new CylinderEnd(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_CYLINDER_BEND = registerBlock("material_shape_cylinder_bend", () -> new CylinderBend(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_CYLINDER_T_CROSS = registerBlock("material_shape_cylinder_t_cross", () -> new CylinderTCross(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIN_CYLINDER = registerBlock("material_shape_thin_cylinder", () -> new ThinCylinder(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIN_CYLINDER_END = registerBlock("material_shape_thin_cylinder_end", () -> new ThinCylinderEnd(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIN_CYLINDER_BEND = registerBlock("material_shape_thin_cylinder_bend", () -> new ThinCylinderBend(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIN_CYLINDER_T_CROSS = registerBlock("material_shape_thin_cylinder_t_cross", () -> new ThinCylinderTCross(MATERIAL_SHAPE_PROPERTIES));


    public static final RegistryObject<Block> MATERIAL_SHAPE_SLOPE = registerBlock("material_shape_slope", () -> new Slope(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_CORNER_OUTER = registerBlock("material_shape_full_corner_outer", () -> new FullCornerOuter(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_CORNER_INNER = registerBlock("material_shape_full_corner_inner", () -> new FullCornerInner(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_ROOF = registerBlock("material_shape_full_roof", () -> new FullRoof(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_ROOF_END = registerBlock("material_shape_full_roof_end", () -> new FullRoofEnd(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_FULL_PYRAMID = registerBlock("material_shape_full_pyramid", () -> new FullPyramid(MATERIAL_SHAPE_PROPERTIES));


    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_BOTTOM = registerBlock("material_shape_half_slope_bottom", () -> new HalfSlopeBottom(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_OUTER_BOTTOM = registerBlock("material_shape_half_corner_outer_bottom", () -> new HalfCornerOuterBottom(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_INNER_BOTTOM = registerBlock("material_shape_half_corner_inner_bottom", () -> new HalfCornerInnerBottom(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_BOTTOM = registerBlock("material_shape_half_roof_bottom", () -> new HalfRoofBottom(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_END_BOTTOM = registerBlock("material_shape_half_roof_end_bottom", () -> new HalfRoofEndBottom(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_PYRAMID_BOTTOM = registerBlock("material_shape_half_pyramid_bottom", () -> new HalfPyramidBottom(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_SLOPE_TOP = registerBlock("material_shape_half_slope_top", () -> new HalfSlopeTop(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_OUTER_TOP = registerBlock("material_shape_half_corner_outer_top", () -> new HalfCornerOuterTop(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_CORNER_INNER_TOP = registerBlock("material_shape_half_corner_inner_top", () -> new HalfCornerInnerTop(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_TOP = registerBlock("material_shape_half_roof_top", () -> new HalfRoofTop(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_ROOF_END_TOP = registerBlock("material_shape_half_roof_end_top", () -> new HalfRoofEndTop(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_HALF_PYRAMID_TOP = registerBlock("material_shape_half_pyramid_top", () -> new HalfPyramidTop(MATERIAL_SHAPE_PROPERTIES));


    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_FIRST = registerBlock("material_shape_third_slope_first", () -> new ThirdSlopeFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_OUTER_FIRST = registerBlock("material_shape_third_corner_outer_first", () -> new ThirdCornerOuterFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_INNER_FIRST = registerBlock("material_shape_third_corner_inner_first", () -> new ThirdCornerInnerFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_FIRST = registerBlock("material_shape_third_roof_first", () -> new ThirdRoofFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_END_FIRST = registerBlock("material_shape_third_roof_end_first", () -> new ThirdRoofEndFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_PYRAMID_FIRST = registerBlock("material_shape_third_pyramid_first", () -> new ThirdPyramidFirst(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_SECOND = registerBlock("material_shape_third_slope_second", () -> new ThirdSlopeSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_OUTER_SECOND = registerBlock("material_shape_third_corner_outer_second", () -> new ThirdCornerOuterSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_INNER_SECOND = registerBlock("material_shape_third_corner_inner_second", () -> new ThirdCornerInnerSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_SECOND = registerBlock("material_shape_third_roof_second", () -> new ThirdRoofSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_END_SECOND = registerBlock("material_shape_third_roof_end_second", () -> new ThirdRoofEndSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_PYRAMID_SECOND = registerBlock("material_shape_third_pyramid_second", () -> new ThirdPyramidSecond(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_SLOPE_THIRD = registerBlock("material_shape_third_slope_third", () -> new ThirdSlopeThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_OUTER_THIRD = registerBlock("material_shape_third_corner_outer_third", () -> new ThirdCornerOuterThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_CORNER_INNER_THIRD = registerBlock("material_shape_third_corner_inner_third", () -> new ThirdCornerInnerThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_THIRD = registerBlock("material_shape_third_roof_third", () -> new ThirdRoofThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_ROOF_END_THIRD = registerBlock("material_shape_third_roof_end_third", () -> new ThirdRoofEndThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_THIRD_PYRAMID_THIRD = registerBlock("material_shape_third_pyramid_third", () -> new ThirdPyramidThird(MATERIAL_SHAPE_PROPERTIES));


    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_FIRST = registerBlock("material_shape_quarter_slope_first", () -> new QuarterSlopeFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FIRST = registerBlock("material_shape_quarter_corner_outer_first", () -> new QuarterCornerOuterFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_FIRST = registerBlock("material_shape_quarter_corner_inner_first", () -> new QuarterCornerInnerFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_FIRST = registerBlock("material_shape_quarter_roof_first", () -> new QuarterRoofFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_FIRST = registerBlock("material_shape_quarter_roof_end_first", () -> new QuarterRoofEndFirst(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_FIRST = registerBlock("material_shape_quarter_pyramid_first", () -> new QuarterPyramidFirst(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_SECOND = registerBlock("material_shape_quarter_slope_second", () -> new QuarterSlopeSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_SECOND = registerBlock("material_shape_quarter_corner_outer_second", () -> new QuarterCornerOuterSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_SECOND = registerBlock("material_shape_quarter_corner_inner_second", () -> new QuarterCornerInnerSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_SECOND = registerBlock("material_shape_quarter_roof_second", () -> new QuarterRoofSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_SECOND = registerBlock("material_shape_quarter_roof_end_second", () -> new QuarterRoofEndSecond(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_SECOND = registerBlock("material_shape_quarter_pyramid_second", () -> new QuarterPyramidSecond(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_THIRD = registerBlock("material_shape_quarter_slope_third", () -> new QuarterSlopeThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_THIRD = registerBlock("material_shape_quarter_corner_outer_third", () -> new QuarterCornerOuterThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_THIRD = registerBlock("material_shape_quarter_corner_inner_third", () -> new QuarterCornerInnerThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_THIRD = registerBlock("material_shape_quarter_roof_third", () -> new QuarterRoofThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_THIRD = registerBlock("material_shape_quarter_roof_end_third", () -> new QuarterRoofEndThird(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_THIRD = registerBlock("material_shape_quarter_pyramid_third", () -> new QuarterPyramidThird(MATERIAL_SHAPE_PROPERTIES));

    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_SLOPE_FOURTH = registerBlock("material_shape_quarter_slope_fourth", () -> new QuarterSlopeFourth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FOURTH = registerBlock("material_shape_quarter_corner_outer_fourth", () -> new QuarterCornerOuterFourth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_CORNER_INNER_FOURTH = registerBlock("material_shape_quarter_corner_inner_fourth", () -> new QuarterCornerInnerFourth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_FOURTH = registerBlock("material_shape_quarter_roof_fourth", () -> new QuarterRoofFourth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_ROOF_END_FOURTH = registerBlock("material_shape_quarter_roof_end_fourth", () -> new QuarterRoofEndFourth(MATERIAL_SHAPE_PROPERTIES));
    public static final RegistryObject<Block> MATERIAL_SHAPE_QUARTER_PYRAMID_FOURTH = registerBlock("material_shape_quarter_pyramid_fourth", () -> new QuarterPyramidFourth(MATERIAL_SHAPE_PROPERTIES));


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
            if (blockInstance instanceof CombinableBlock)
                return new CombinableBlockItem(blockInstance, new Item.Properties());
            return new BlockItem(blockInstance, new Item.Properties());
        });
    }

    private ModBlocks() {}

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
