package net.centertain.ceac.prop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.UUID;

public final class PropMiningController {
    private static @Nullable UUID targetId;
    private static float progress;

    private PropMiningController() {}

    public static void reset() {
        targetId = null;
        progress = 0.0f;
    }

    @SuppressWarnings("deprecation")
    public static void tick(
            LocalPlayer player,
            PropHitResult hit,
            PropDefinition definition
    ) {
        if (!Minecraft.getInstance().options.keyAttack.isDown()
                || hit == null
                || definition == null
                || !definition.getMinable()
        ) {
            reset();
            return;
        }

        UUID id = hit.getProp().getId();
        if (!id.equals(targetId)) {
            targetId = id;
            progress = 0.0f;
        }

        float hardness = definition.getDestroyTime();
        if (hardness < 0.0f) {
            progress = 0.0f;
            return;
        }
        if (hardness == 0.0f) {
            finishMining(id);
            return;
        }

        float speed = player.getDestroySpeed(Blocks.STONE.defaultBlockState());
        if (progress >= 1.0f)
            finishMining(id);
    }

    private static void finishMining(UUID id) {
        // TODO: Server-bound prop-break package insert
        reset();
    }

    public static float getProgress() {
        return progress;
    }
}
