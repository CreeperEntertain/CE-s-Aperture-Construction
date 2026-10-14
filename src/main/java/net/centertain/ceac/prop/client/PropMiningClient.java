package net.centertain.ceac.prop.client;

import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.*;
import net.centertain.ceac.prop.network.PropMiningPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;

import java.util.UUID;

public final class PropMiningClient {
    private static UUID activePropId;
    private static float predictedProgress;
    private static int predictedStage = -1;
    private static boolean consumeAttackUntilRelease;

    private PropMiningClient() {}

    public static void start(Prop prop) {
        Minecraft minecraft = Minecraft.getInstance();
        if (
                minecraft.player == null ||
                minecraft.level == null ||
                minecraft.gameMode == null
        )
            return;
        abort();
        consumeAttackUntilRelease = true;

        minecraft.player.swing(InteractionHand.MAIN_HAND);

        minecraft.gameMode.stopDestroyBlock();
        activePropId = prop.getId();
        predictedProgress = 0.0f;
        predictedStage = -1;
        send(PropMiningPacket.Action.START, activePropId);
    }

    public static boolean handleContinue(boolean breaking) {
        Minecraft minecraft = Minecraft.getInstance();
        if (
                minecraft.player == null ||
                minecraft.level == null ||
                minecraft.gameMode == null
        ) {
            abort();
            return false;
        }

        if (consumeAttackUntilRelease) {
            if (!breaking) {
                consumeAttackUntilRelease = false;
                abort();
                return true;
            }
            if (!(minecraft.hitResult instanceof PropHitResult)) {
                abort();
                return true;
            }
        }

        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof PropHitResult propHit)) {
            if (activePropId != null) {
                abort();
                return true;
            }
            return false;
        }
        if (!breaking) {
            abort();
            return true;
        }

        Prop prop = propHit.getProp();
        if (activePropId == null || !activePropId.equals(prop.getId()))
            start(prop);
        else
            minecraft.player.swing(InteractionHand.MAIN_HAND);

        PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
        boolean creative = minecraft.player.getAbilities().instabuild;
        if (definition != null && (creative || definition.getMinable())) {
            predictedProgress += PropMiningMath.getDestroyProgress(
                    minecraft.player, definition, prop.getBlockPosition()
            );
            predictedProgress = Math.min(predictedProgress, 0.99f);
            predictedStage = Math.min(9, (int) (predictedProgress * 10.0f));
        } else
            predictedStage = -1;

        send(PropMiningPacket.Action.CONTINUE, prop.getId());

        return true;
    }

    public static void abort() {
        UUID oldId = activePropId;
        activePropId = null;
        predictedProgress = 0.0f;
        predictedStage = -1;
        if (oldId != null)
            send(PropMiningPacket.Action.ABORT, oldId);
    }

    public static int getPredictedStage(UUID propId) {
        if (activePropId == null || !activePropId.equals(propId))
            return -1;
        return predictedStage;
    }

    public static void reset() {
        activePropId = null;
        predictedProgress = 0.0f;
        predictedStage = -1;
        consumeAttackUntilRelease = false;
    }

    private static void send(
            PropMiningPacket.Action action,
            UUID propId
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null)
            return;
        ModNetworking.CHANNEL.sendToServer(new PropMiningPacket(action, propId));
    }
}
