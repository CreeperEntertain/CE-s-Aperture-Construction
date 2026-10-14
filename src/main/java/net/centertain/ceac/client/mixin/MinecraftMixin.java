package net.centertain.ceac.client.mixin;

import net.centertain.ceac.prop.PropHitResult;
import net.centertain.ceac.prop.client.PropMiningClient;
import net.centertain.ceac.prop.client.PropTargeting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(
            method = "startAttack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$startPropMining(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (!(minecraft.hitResult instanceof PropHitResult hit))
            return;
        PropMiningClient.start(hit.getProp());
        cir.setReturnValue(true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Inject(
            method = "continueAttack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$continuePropMining(
            boolean breaking,
            CallbackInfo ci
    ) {
        Minecraft minecraft = (Minecraft) (Object) this;
        ceac$refreshTarget(minecraft);
        if (PropMiningClient.handleContinue(breaking))
            ci.cancel();
    }

    @SuppressWarnings("UnusedReturnValue")
    @Unique
    private PropHitResult ceac$refreshTarget(Minecraft minecraft) {
        if (
                minecraft.player == null ||
                minecraft.level == null ||
                minecraft.gameMode == null
        )
            return null;

        float partialTick = minecraft.getFrameTime();
        Vec3 origin = minecraft.player.getEyePosition(partialTick);
        Vec3 direction = minecraft.player.getViewVector(partialTick).normalize();
        double reach = minecraft.gameMode.getPickRange();
        BlockHitResult blockHit = minecraft.level.clip(new ClipContext(
                origin,
                origin.add(direction.scale(reach)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                minecraft.player
        ));
        PropHitResult propHit = PropTargeting.pick(minecraft.player, partialTick, blockHit);
        if (propHit != null) {
            minecraft.hitResult = propHit;
            return propHit;
        }

        minecraft.hitResult = blockHit;
        return null;
    }
}
