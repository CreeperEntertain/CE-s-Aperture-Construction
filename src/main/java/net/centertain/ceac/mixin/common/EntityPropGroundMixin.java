package net.centertain.ceac.mixin.common;

import net.centertain.ceac.prop.framework.server.PropStepSoundServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class EntityPropGroundMixin {
    @ModifyArg(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setOnGroundWithKnownMovement(ZLnet/minecraft/world/phys/Vec3;)V"
            ),
            index = 0
    )
    private boolean ceac$includePropSupport(boolean vanillaGrounded) {
        if (vanillaGrounded)
            return true;

        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide || entity.noPhysics || entity.isSpectator())
            return false;

        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.y > 1.0e-6)
            return false;

        return PropStepSoundServer.isSupportedByProp(entity);
    }
}
