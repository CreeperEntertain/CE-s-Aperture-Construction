package net.centertain.ceac.client.mixin;

import net.centertain.ceac.prop.framework.common.PropHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockPlaceContext.class)
public abstract class BlockPlaceContextMixin {
    @Shadow @Final @Mutable
    private BlockPos relativePos;

    @Shadow
    protected boolean replaceClicked;

    @Inject(
            method = "<init>(Lnet/minecraft/world/item/context/UseOnContext;)V",
            at = @At("TAIL")
    )
    private void ceac$placeOutsideProp(
            UseOnContext context,
            CallbackInfo ci
    ) {
        BlockHitResult hit = ((UseOnContextAccessor) (Object) this).ceac$getHitResult();
        if (!(hit instanceof PropHitResult))
            return;

        Direction face = hit.getDirection();
        Vec3 insideSurface = hit.getLocation().subtract(
                face.getStepX() * 1.0e-4,
                face.getStepY() * 1.0e-4,
                face.getStepZ() * 1.0e-4
        );

        this.relativePos = BlockPos.containing(insideSurface).relative(face);
        this.replaceClicked = false;
    }
}
