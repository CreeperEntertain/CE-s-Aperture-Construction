package net.centertain.ceac.mixin.common;

import net.centertain.ceac.prop.framework.physics.PropEntityCollision;
import net.centertain.ceac.prop.framework.physics.PropPhysManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityPropCollisionMixin {
    @Inject(
            method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void ceac$collideWithProps(
            Vec3 movement,
            CallbackInfoReturnable<Vec3> cir
    ) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide || entity.noPhysics || entity.isSpectator())
            return;

        Vec3 vanillaMovement = cir.getReturnValue();
        Vec3 propMovement = PropPhysManager.clipEntityMovement(entity, vanillaMovement, movement);
        Vec3 safeMovement = PropEntityCollision.clipAgainstBlocks(entity, propMovement);

        cir.setReturnValue(safeMovement);
    }
}
