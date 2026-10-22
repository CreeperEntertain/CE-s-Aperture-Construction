package net.centertain.ceac.mixin.client;

import net.centertain.ceac.prop.framework.client.PropClientStepSounds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerPropStepSoundClientMixin {
    @Inject(
            method = "playStepSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$playLocalPropStepSound(
            BlockPos pos,
            BlockState state,
            CallbackInfo ci
    ) {
        Player player = (Player) (Object) this;
        if (!(player instanceof LocalPlayer localPlayer))
            return;
        if (PropClientStepSounds.playPropStepSound(localPlayer))
            ci.cancel();
    }
}
