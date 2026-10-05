package net.centertain.ceac.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public interface FillableBlock {
    IntegerProperty getFillProperty();

    VoxelShape getFillPieceShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    );

    default Vec3 transformPointToLocal(
            BlockState state,
            Vec3 point
    ) {
        return point;
    }

    default Vec3 transformDirectionToLocal(
            BlockState state,
            Vec3 direction
    ) {
        return direction;
    }

    default Vec3 transformPointToWorld(
            BlockState state,
            Vec3 point
    ) {
        return point;
    }

    default Vec3 transformDirectionToWorld(
            BlockState state,
            Vec3 direction
    ) {
        return direction;
    }

    default int getFillMask(BlockState state) {
        return state.getValue(getFillProperty());
    }

    default int getFillCount(BlockState state) {
        return Integer.bitCount(getFillMask(state));
    }

    default BlockState setFillMask(
            BlockState state,
            int mask
    ) {
        return state.setValue(getFillProperty(), mask);
    }

    default @NotNull FillDefinition getFillDefinition(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return FillDefinition.fromShape(
                this,
                state,
                getFillPieceShape(state, level, pos, context)
        );
    }

    record FillDefinition(
            int x,
            int y,
            int z,
            double startX,
            double startY,
            double startZ
    ) {
        public int size() {
            return x * y * z;
        }

        public int index(
                int x,
                int y,
                int z
        ) {
            return x + this.x * (y + this.y * z);
        }

        public Vec3 offset(int index) {
            int x = index % this.x;
            int yz = index / this.x;
            int y = yz % this.y;
            int z = yz / this.y;

            return new Vec3(
                    x / (double) this.x - startX,
                    y / (double) this.y - startY,
                    z / (double) this.z - startZ
            );
        }

        public boolean contains(
                int x,
                int y,
                int z
        ) {
            return
                    x >= 0 && x < this.x &&
                    y >= 0 && y < this.y &&
                    z >= 0 && z < this.z;
        }

        private static FillDefinition fromShape(
                FillableBlock fillable,
                BlockState state,
                VoxelShape shape
        ) {
            if (shape.isEmpty())
                throw new IllegalStateException("Fillable block has an empty piece shape");
            if (shape.toAabbs().size() != 1)
                throw new IllegalStateException("Fillable block must have a single cuboid piece");

            var bounds = shape.bounds();

            Vec3[] corners = {
                    new Vec3(bounds.minX, bounds.minY, bounds.minZ),
                    new Vec3(bounds.maxX, bounds.minY, bounds.minZ),
                    new Vec3(bounds.minX, bounds.maxY, bounds.minZ),
                    new Vec3(bounds.maxX, bounds.maxY, bounds.minZ),
                    new Vec3(bounds.minX, bounds.minY, bounds.maxZ),
                    new Vec3(bounds.maxX, bounds.minY, bounds.maxZ),
                    new Vec3(bounds.minX, bounds.maxY, bounds.maxZ),
                    new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)
            };

            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;

            for (Vec3 corner : corners) {
                Vec3 local = fillable.transformPointToLocal(state, corner);

                minX = Math.min(minX, local.x);
                minY = Math.min(minY, local.y);
                minZ = Math.min(minZ, local.z);
                maxX = Math.max(maxX, local.x);
                maxY = Math.max(maxY, local.y);
                maxZ = Math.max(maxZ, local.z);
            }

            int x = subdivisions(maxX - minX);
            int y = subdivisions(maxY - minY);
            int z = subdivisions(maxZ - minZ);

            return new FillDefinition(
                    x,
                    y,
                    z,
                    minX,
                    minY,
                    minZ
            );
        }

        private static int subdivisions(double size) {
            if (size <= 1.0e-6)
                throw new IllegalStateException("Fillable piece has zero-sized axis");

            double reciprocal = 1.0 / size;
            int subdivisions = (int) Math.round(reciprocal);

            if (subdivisions < 1 || Math.abs(size * subdivisions - 1.0) > 1.0e-5)
                throw new IllegalStateException("Fillable piece does not evenly divide a block: " + size);

            return subdivisions;
        }
    }
}
