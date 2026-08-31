package net.centertain.ceac.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> CEAC_TAB = CREATIVE_MODE_TABS.register("ceac_tab",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.DECAL.get()))
                    .title(Component.translatable("creativetab.ceac_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.DECAL.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
