package net.centertain.ceac.mixin.common;

import net.centertain.ceac.prop.framework.server.PropStepSoundServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityPropStepSoundMixin {
    @Inject(
            method = "playStepSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ceac$playPropStepSound(
            BlockPos pos,
            BlockState state,
            CallbackInfo ci
    ) {
        Entity entity = (Entity) (Object) this;
        if (!entity.level().isClientSide && PropStepSoundServer.playIfOnProp(entity))
            ci.cancel();
    }
}
