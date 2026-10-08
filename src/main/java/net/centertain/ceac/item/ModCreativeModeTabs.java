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

public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    @SuppressWarnings("unused")
    public static final RegistryObject<CreativeModeTab> CEAC_TAB = CREATIVE_MODE_TABS.register("ceac_tab",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.DECAL.get()))
                    .title(Component.translatable("creativetab.ceac_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.PURCHASING_TERMINAL.get().asItem().getDefaultInstance());

                        pOutput.accept(ModItems.DECAL.get());
                        pOutput.accept(ModItems.SCRAPER.get());
                        pOutput.accept(ModItems.WRENCH.get());



                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_BlOCK.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_BIT.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_BIT_PILLAR.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_STAIRS.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_NINTH.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_NINTH_PILLAR.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_STAIRS.get());


                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_SLOPE.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_FULL_CORNER_OUTER.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_FULL_CORNER_INNER.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_FULL_ROOF.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_FULL_ROOF_END.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_FULL_PYRAMID.get());


                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_SLOPE_BOTTOM.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_OUTER_BOTTOM.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_INNER_BOTTOM.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_BOTTOM.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_END_BOTTOM.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_PYRAMID_BOTTOM.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_SLOPE_TOP.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_OUTER_TOP.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_CORNER_INNER_TOP.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_TOP.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_ROOF_END_TOP.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_HALF_PYRAMID_TOP.get());


                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_SLOPE_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_OUTER_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_INNER_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_END_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_PYRAMID_FIRST.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_SLOPE_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_OUTER_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_INNER_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_END_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_PYRAMID_SECOND.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_SLOPE_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_OUTER_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_CORNER_INNER_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_ROOF_END_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_THIRD_PYRAMID_THIRD.get());


                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_FIRST.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_FIRST.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_SECOND.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_SECOND.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_THIRD.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_THIRD.get());

                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_SLOPE_FOURTH.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_OUTER_FOURTH.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_CORNER_INNER_FOURTH.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_FOURTH.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_ROOF_END_FOURTH.get());
                        pOutput.accept(ModBlocks.MATERIAL_SHAPE_QUARTER_PYRAMID_FOURTH.get());



                        pOutput.accept(ModItems.OBSERVATION_CONCRETE_WALL.get());
                    })
                    .build()
    );

    private ModCreativeModeTabs() {}

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
