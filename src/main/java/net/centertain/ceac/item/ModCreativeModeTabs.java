package net.centertain.ceac.item;

import net.centertain.ceac.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.centertain.ceac.CeacMod.MOD_ID;

@SuppressWarnings("unused")
public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public static final RegistryObject<CreativeModeTab> BASICS = CREATIVE_MODE_TABS.register("basics",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.DECAL.get()))
                    .title(Component.translatable("creativetab.basics"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.PROP_PLACER.get());

                        output.accept(ModBlocks.PURCHASING_TERMINAL.get().asItem().getDefaultInstance());

                        output.accept(ModItems.DECAL.get());
                        output.accept(ModItems.SCRAPER.get());
                        output.accept(ModItems.WRENCH.get());
                        output.accept(ModItems.CROWBAR.get());
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> SHAPES = CREATIVE_MODE_TABS.register("shapes",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModBlocks.MATERIAL_SHAPE_BlOCK.get()))
                    .title(Component.translatable("creativetab.shapes"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.MATERIAL_SHAPE_BlOCK.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_BIT.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_BIT_PILLAR.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_STAIRS.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_NINTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_NINTH_PILLAR.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_STAIRS.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_SIXTEENTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_SIXTEENTH_PILLAR.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_STAIRS.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_EIGHTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_LAYER.get());


                        output.accept(ModBlocks.MATERIAL_SHAPE_EIGHTH_SPHERE.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CYLINDER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ARCH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ARCH_OUTER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ARCH_INNER.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_SPHERE.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_CYLINDER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_CYLINDER_END.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_CYLINDER_BEND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_CYLINDER_T_CROSS.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_THIN_CYLINDER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIN_CYLINDER_END.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIN_CYLINDER_BEND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIN_CYLINDER_T_CROSS.get());


                        output.accept(ModBlocks.MATERIAL_SHAPE_SLOPE.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_FULL_CORNER_OUTER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_FULL_CORNER_INNER.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_FULL_ROOF.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_FULL_ROOF_END.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_FULL_PYRAMID.get());


                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_SLOPE_BOTTOM.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_OUTER_BOTTOM.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_INNER_BOTTOM.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_BOTTOM.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_END_BOTTOM.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_PYRAMID_BOTTOM.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_SLOPE_TOP.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_OUTER_TOP.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_INNER_TOP.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_TOP.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_END_TOP.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_HALF_PYRAMID_TOP.get());


                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_SLOPE_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_OUTER_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_INNER_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_END_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_PYRAMID_FIRST.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_SLOPE_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_OUTER_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_INNER_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_END_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_PYRAMID_SECOND.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_SLOPE_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_OUTER_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_INNER_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_END_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_THIRD_PYRAMID_THIRD.get());


                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_FIRST.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_FIRST.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_SECOND.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_SECOND.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_THIRD.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_THIRD.get());

                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_FOURTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FOURTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_FOURTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_FOURTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_FOURTH.get());
                        output.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_FOURTH.get());
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> MATERIALS = CREATIVE_MODE_TABS.register("materials",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.OBSERVATION_CONCRETE_WALL.get()))
                    .title(Component.translatable("creativetab.materials"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BARE_BACKPANEL.get());
                        output.accept(ModItems.BARE_BACKPANEL_LARGE.get());
                        output.accept(ModItems.GREEN_BACKPANEL.get());
                        output.accept(ModItems.GREEN_BACKPANEL_LARGE.get());
                        output.accept(ModItems.ORANGE_BACKPANEL.get());
                        output.accept(ModItems.ORANGE_BACKPANEL_LARGE.get());
                        output.accept(ModItems.RUSTY_BACKPANEL.get());
                        output.accept(ModItems.RUSTY_BACKPANEL_LARGE.get());
                        output.accept(ModItems.WEATHERED_BACKPANEL.get());
                        output.accept(ModItems.WEATHERED_BACKPANEL_LARGE.get());
                        output.accept(ModItems.WHITE_BACKPANEL.get());
                        output.accept(ModItems.WHITE_BACKPANEL_LARGE.get());
                        output.accept(ModItems.YELLOW_BACKPANEL.get());
                        output.accept(ModItems.YELLOW_BACKPANEL_LARGE.get());

                        output.accept(ModItems.BARE_CONCRETE_CAST.get());
                        output.accept(ModItems.CHECKERED_CONCRETE_FLOOR_TILES.get());
                        output.accept(ModItems.CINDERBLOCKS.get());
                        output.accept(ModItems.DARK_CONCRETE_FLOOR_TILES.get());
                        output.accept(ModItems.DIRTY_CONCRETE_FLOOR.get());
                        output.accept(ModItems.DIRTY_CONCRETE_FLOOR_TILES.get());
                        output.accept(ModItems.DIRTY_REBAR_CONCRETE_BLOCKS.get());
                        output.accept(ModItems.FACTORY_CONCRETE_WALL.get());
                        output.accept(ModItems.GLUED_CONCRETE_PANELS.get());
                        output.accept(ModItems.HEAVY_CONCRETE_CEILING.get());
                        output.accept(ModItems.LIGHT_CONCRETE_FLOOR_TILES.get());
                        output.accept(ModItems.MODULAR_CONCRETE.get());
                        output.accept(ModItems.OBSERVATION_CONCRETE_WALL.get());
                        output.accept(ModItems.PAINTED_CONCRETE_BLOCK_PILLAR.get());
                        output.accept(ModItems.PAINTED_CONCRETE_TILES.get());
                        output.accept(ModItems.PAINTED_FACTORY_CONCRETE_WALL.get());
                        output.accept(ModItems.REBAR_CONCRETE.get());
                        output.accept(ModItems.REBAR_CONCRETE_BLOCKS.get());
                        output.accept(ModItems.RUSTED_REBAR_CONCRETE.get());
                        output.accept(ModItems.SMALL_DIRTY_CONCRETE_FLOOR_TILES.get());
                        output.accept(ModItems.STAINED_HEAVY_CONCRETE_CEILING.get());
                        output.accept(ModItems.STAINED_MODULAR_CONCRETE.get());
                        output.accept(ModItems.STAINED_REBAR_CONCRETE.get());
                        output.accept(ModItems.WEATHERED_REBAR_CONCRETE.get());
                        output.accept(ModItems.WITHERED_MODULAR_CONCRETE.get());
                        output.accept(ModItems.WITHERED_REBAR_CONCRETE_BLOCKS.get());
                    })
                    .build()
    );


    private ModCreativeModeTabs() {}

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
