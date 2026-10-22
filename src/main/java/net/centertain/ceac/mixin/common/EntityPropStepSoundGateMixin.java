package net.centertain.ceac.mixin.common;

import net.centertain.ceac.prop.framework.server.PropStepSoundServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityPropStepSoundGateMixin {
    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z",
                    ordinal = 0
            )
    )
    private boolean ceac$allowPropFootstepBranch(BlockState state) {
        return ceac$isAirUnlessPropSupport(state);
    }

    @Redirect(
            method = "vibrationAndSoundEffectsFromBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"
            )
    )
    private boolean ceac$allowPropStepSound(BlockState state) {
        return ceac$isAirUnlessPropSupport(state);
    }


    @Unique
    private boolean ceac$isAirUnlessPropSupport(BlockState state) {
        if (!state.isAir())
            return false;

        Entity entity = (Entity) (Object) this;
        if (entity.noPhysics || entity.isSpectator() || !entity.onGround())
            return true;
        if (entity.level().isClientSide)
            return false;

        return !PropStepSoundServer.isSupportedByProp(entity);
    }
}
