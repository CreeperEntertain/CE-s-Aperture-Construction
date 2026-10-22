package net.centertain.ceac.mixin.common;

import net.centertain.ceac.prop.framework.common.PropHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockPlaceContext.class)
public abstract class BlockPlaceContextMixin {
    @Unique
    private static final double PLACEMENT_GRACE = 1.0 / 16.0;


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
        Vec3 location = hit.getLocation();

        double x = location.x;
        double y = location.y;
        double z = location.z;

        switch (face.getAxis()) {
            case X -> x = ceac$snapToBlockBoundary(x);
            case Y -> y = ceac$snapToBlockBoundary(y);
            case Z -> z = ceac$snapToBlockBoundary(z);
        }

        Vec3 snappedLocation = new Vec3(x, y, z);
        Vec3 insideSurface = snappedLocation.subtract(
                face.getStepX() * 1.0e-4,
                face.getStepY() * 1.0e-4,
                face.getStepZ() * 1.0e-4
        );

        this.relativePos = BlockPos.containing(insideSurface).relative(face);
        this.replaceClicked = false;
    }


    @Unique
    private static double ceac$snapToBlockBoundary(double coordinate) {
        double boundary = Math.rint(coordinate);
        return Math.abs(coordinate - boundary) <= PLACEMENT_GRACE ? boundary : coordinate;
    }
}
