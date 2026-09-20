package net.centertain.ceac.client.events;

import net.centertain.ceac.decal.client.ClientDecals;
import net.centertain.ceac.decal.client.DecalPlacement;
import net.centertain.ceac.decal.client.render.TranslucentRenderTargets;
import net.centertain.ceac.decal.server.DecalBreakage;
import net.centertain.ceac.material.MaterialPlacement;
import net.centertain.ceac.material.utility.MaterialShapeSelectionOutline;
import net.centertain.ceac.utility.Soundworks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        Soundworks.playLocalStereoSounds(event);

        // Translucent target resizing
        if (event.phase != TickEvent.Phase.END)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        TranslucentRenderTargets.resize(width, height);

        // Decal preview clearing
        Player player = minecraft.player;
        if (player == null) {
            DecalPlacement.decalItemPresent = false;
            DecalPlacement.decalItemSeenThisTick = false;
            DecalPlacement.forceCleanup();
            return;
        }
        if (DecalPlacement.decalItemPresent && !DecalPlacement.decalItemSeenThisTick)
            DecalPlacement.forceCleanup();
        DecalPlacement.decalItemPresent = DecalPlacement.decalItemSeenThisTick;
        DecalPlacement.decalItemSeenThisTick = false;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        DecalBreakage.breakFloatingDecalsInRange(event);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        DecalBreakage.breakSuffocatingDecalsInRange(event);
    }

    @SubscribeEvent
    public static void onGameShuttingDown(GameShuttingDownEvent event) {
        TranslucentRenderTargets.destroy();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientDecals.clear();
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (!MaterialPlacement.getAdjustOffset())
            DecalPlacement.swapPrecisePlacement(event);
        if (!DecalPlacement.getPrecisePlacement())
            MaterialPlacement.swapAdjustOffset(event);
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        DecalPlacement.rotateAbstraction(event);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        DecalPlacement.suppressPrecisePlacementKeys(event);
        MaterialPlacement.suppressAdjustOffsetKeys(event);
    }

    @SubscribeEvent
    public static void renderSelection(RenderHighlightEvent.Block event) {
        MaterialShapeSelectionOutline.replaceSelectionOutline(event);
    }
}
