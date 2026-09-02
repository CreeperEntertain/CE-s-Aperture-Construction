package net.centertain.ceac.decal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DecalTestEvent {
    private DecalTestEvent() {}

    //@SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        DecalTest.addTestDecal(
                serverLevel,
                player,
                event.getPos()
        );
    }
}
