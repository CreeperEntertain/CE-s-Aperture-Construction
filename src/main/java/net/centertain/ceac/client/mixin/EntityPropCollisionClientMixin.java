package net.centertain.ceac.client.mixin;

import net.centertain.ceac.prop.client.ClientProps;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityPropCollisionClientMixin {
    @Inject(
            method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void ceac$collideWithClientProps(
            Vec3 movement,
            CallbackInfoReturnable<Vec3> cir
    ) {
        Entity entity = (Entity) (Object) this;
        if (
                !(entity instanceof LocalPlayer) ||
                !entity.level().isClientSide ||
                entity.noPhysics ||
                entity.isSpectator()
        )
            return;
        cir.setReturnValue(ClientProps.clipLocalPlayerMovement(entity, cir.getReturnValue()));
    }
}
