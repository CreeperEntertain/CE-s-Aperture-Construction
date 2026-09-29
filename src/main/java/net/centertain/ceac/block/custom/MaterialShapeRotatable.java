package net.centertain.ceac.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface MaterialShapeRotatable {
    @Nullable DirectionProperty getFacing();
    @Nullable Direction.Axis getAxis();
    @Nullable IntegerProperty getRotation();

    Vec3 transformPointToLocal(
            BlockState state,
            Vec3 point
    );

    Vec3 transformDirectionToLocal(
            BlockState state,
            Vec3 direction
    );

    Vec3 transformPointToWorld(
            BlockState state,
            Vec3 point
    );

    Vec3 transformDirectionToWorld(
            BlockState state,
            Vec3 point
    );

    boolean rotateFromViewDirection(
            Level level,
            BlockPos pos,
            Vec3 viewDirection,
            boolean counterclockwise
    );
}
