package net.centertain.ceac.prop;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class PropHitResult extends BlockHitResult {
    private final Prop prop;
    private final BlockHitResult underlyingBlockHit;

    public PropHitResult(
            Vec3 location,
            Direction direction,
            BlockPos blockPos,
            boolean inside,
            Prop prop,
            BlockHitResult underlyingBlockHit
    ) {
        super(location, direction, blockPos, inside);
        this.prop = prop;
        this.underlyingBlockHit = underlyingBlockHit;
    }

    public Prop getProp() {
        return prop;
    }
    public BlockHitResult getUnderlyingBlockHit() {
        return underlyingBlockHit;
    }
}
