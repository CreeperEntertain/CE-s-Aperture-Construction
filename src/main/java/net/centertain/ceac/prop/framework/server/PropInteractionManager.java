package net.centertain.ceac.prop.framework.server;

import net.centertain.ceac.item.custom.PropPlacerItem;
import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.PropDefinition;
import net.centertain.ceac.prop.framework.common.PropRaycast;
import net.centertain.ceac.prop.framework.common.PropUseContext;
import net.centertain.ceac.prop.framework.network.PropUsePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class PropInteractionManager {
    private PropInteractionManager() {}


    public static void handle(ServerPlayer player, PropUsePacket packet) {
        if (!player.isAlive() || player.isSpectator())
            return;

        double reach = player.getAbilities().instabuild ? 5.0 : 4.5;
        PropRaycast.Target target = PropRaycast.findTarget(player, reach);
        if (target == null || !target.prop().getId().equals(packet.getPropId()))
            return;

        Prop prop = target.prop();
        PropDefinition definition = target.definition();

        InteractionResult propResult = definition.use(new PropUseContext(
                player.level(),
                player,
                prop,
                packet.getHand(),
                target.location(),
                target.face()
        ));
        if (propResult != InteractionResult.PASS)
            return;

        ItemStack stack = player.getItemInHand(packet.getHand());
        if (stack.getItem() instanceof PropPlacerItem placer) {
            placer.placeAt(
                    player.level(),
                    player,
                    packet.getHand(),
                    target.location(),
                    target.face()
            );
            return;
        }

        Vec3 insideSurface = target.location().subtract(
                target.face().getStepX() * 1.0e-4,
                target.face().getStepY() * 1.0e-4,
                target.face().getStepZ() * 1.0e-4
        );
        BlockPos placementPos = BlockPos.containing(insideSurface).relative(target.face());
        BlockHitResult placementHit = new BlockHitResult(target.location(), target.face(), placementPos, false);
        InteractionResult itemResult = stack.useOn(new UseOnContext(player, packet.getHand(), placementHit));
        if (itemResult == InteractionResult.PASS)
            player.gameMode.useItem(player, player.level(), stack, packet.getHand());
    }
}
