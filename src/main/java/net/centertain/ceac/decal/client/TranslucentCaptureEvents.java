package net.centertain.ceac.decal.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class TranslucentCaptureEvents {
    private TranslucentCaptureEvents() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)
            return;
        if (Minecraft.getInstance().level == null)
            return;
        TranslucentCapture.render(event);
    }
}
