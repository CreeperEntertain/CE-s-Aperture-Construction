package net.centertain.ceac.mixin.client;

import net.centertain.ceac.prop.framework.client.PropClientStepSounds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public class EntityPropGroundClientMixin {
    @ModifyArg(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setOnGroundWithKnownMovement(ZLnet/minecraft/world/phys/Vec3;)V"
            ),
            index = 0
    )
    private boolean ceac$includeLocalPropSupport(boolean vanillaGround) {
        if (vanillaGround)
            return true;

        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LocalPlayer player))
            return false;
        if (!entity.level().isClientSide || entity.noPhysics || entity.isSpectator())
            return false;

        Vec3 velocity = player.getDeltaMovement();
        if (velocity.y > 0.0)
            return false;

        return PropClientStepSounds.isSupportedByProp(player);
    }
}
