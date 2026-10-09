package net.centertain.ceac.block.custom.types.fillable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.centertain.ceac.material.shapes.MaterialShapeBlockEntity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public interface FillableBlock {
    VoxelShape getCanonicalFillShape();

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

    default BitSet getFillMask(
            @NotNull BlockGetter level,
            @NotNull BlockPos pos
    ) {
        if (level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity)
            return blockEntity.getFillMask();
        BitSet mask = new BitSet();
        mask.set(0);
        return mask;
    }

    default int getFillCount(
            @NotNull BlockGetter level,
            @NotNull BlockPos pos
    ) {
        return getFillMask(level, pos).cardinality();
    }

    default void setFillMask(
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull BitSet mask
    ) {
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            throw new IllegalStateException("Fillable block has no MaterialShapeBlockEntity at " + pos);
        blockEntity.setFillMask(mask);
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

    default int getFillIndex(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context,
            @NotNull Vec3 point,
            @NotNull Direction face
    ) {
        FillDefinition definition = getFillDefinition(
                state,
                level,
                pos,
                context
        );

        Vec3 localPoint = transformPointToLocal(
                state,
                point.subtract(pos.getX(), pos.getY(), pos.getZ())
        );

        Vec3 localDirection = transformDirectionToLocal(state, new Vec3(
                face.getStepX(),
                face.getStepY(),
                face.getStepZ()
        ));

        Direction localFace = Direction.getNearest(
                localDirection.x,
                localDirection.y,
                localDirection.z
        );

        return definition.surfaceIndex(localPoint, localFace);
    }

    default @NotNull VoxelShape getFilledShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        FillDefinition definition = getFillDefinition(
                state,
                level,
                pos,
                context
        );

        BitSet mask = getFillMask(level, pos);
        VoxelShape result = Shapes.empty();

        for (int index = 0; index < definition.size(); index++) {
            if (!mask.get(index))
                continue;
            result = Shapes.or(result, getFillCellShape(state, definition, index));
        }

        return result;
    }

    @SuppressWarnings("ExtractMethodRecommender")
    default @NotNull VoxelShape getFillCellShape(
            @NotNull BlockState state,
            @NotNull FillDefinition definition,
            int index
    ) {
        int x = index % definition.x();
        int yz = index / definition.x();
        int y = yz % definition.y();
        int z = yz / definition.y();

        double minX = x / (double) definition.x();
        double minY = y / (double) definition.y();
        double minZ = z / (double) definition.z();

        double maxX = (x + 1) / (double) definition.x();
        double maxY = (y + 1) / (double) definition.y();
        double maxZ = (z + 1) / (double) definition.z();

        Vec3[] corners = {
                new Vec3(minX, minY, minZ),
                new Vec3(maxX, minY, minZ),
                new Vec3(minX, maxY, minZ),
                new Vec3(maxX, maxY, minZ),
                new Vec3(minX, minY, maxZ),
                new Vec3(maxX, minY, maxZ),
                new Vec3(minX, maxY, maxZ),
                new Vec3(maxX, maxY, maxZ)
        };

        double worldMinX = Double.POSITIVE_INFINITY;
        double worldMinY = Double.POSITIVE_INFINITY;
        double worldMinZ = Double.POSITIVE_INFINITY;
        double worldMaxX = Double.NEGATIVE_INFINITY;
        double worldMaxY = Double.NEGATIVE_INFINITY;
        double worldMaxZ = Double.NEGATIVE_INFINITY;

        for (Vec3 corner : corners) {
            Vec3 world = transformPointToWorld(state, corner);

            worldMinX = Math.min(worldMinX, world.x);
            worldMinY = Math.min(worldMinY, world.y);
            worldMinZ = Math.min(worldMinZ, world.z);
            worldMaxX = Math.max(worldMaxX, world.x);
            worldMaxY = Math.max(worldMaxY, world.y);
            worldMaxZ = Math.max(worldMaxZ, world.z);
        }

        return Shapes.box(worldMinX, worldMinY, worldMinZ, worldMaxX, worldMaxY, worldMaxZ);
    }

    @NotNull BakedModel getFillModel(BlockState state);

    default boolean tryDestroyFilledPiece(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            boolean willHarvest,
            @NotNull FluidState fluid
    ) {
        BitSet mask = getFillMask(level, pos);
        if (mask.cardinality() <= 1)
            return false;

        HitResult hitResult = player.pick(player.getBlockReach(), 1.0f, false);
        if (!(hitResult instanceof BlockHitResult hit))
            return false;
        if (!hit.getBlockPos().equals(pos))
            return false;

        int pieceIndex = getBreakFillIndex(
                state,
                level,
                pos,
                CollisionContext.empty(),
                hit.getLocation(),
                hit.getDirection()
        );
        if (pieceIndex < 0)
            return false;
        if (!mask.get(pieceIndex))
            return false;
        mask.clear(pieceIndex);
        if (mask.isEmpty())
            return false;

        Block block = (Block) this;
        block.playerWillDestroy(level, pos, state, player);
        setFillMask(level, pos, mask);

        if (!player.isCreative() && willHarvest)
            Block.popResource(level, pos, new ItemStack(block));

        return true;
    }

    default int getBreakFillIndex(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context,
            @NotNull Vec3 point,
            @NotNull Direction face
    ) {
        return getFillIndex(
                state,
                level,
                pos,
                context,
                point,
                face.getOpposite()
        );
    }

    default int getMaxFillCount() {
        return getMaxFillCount(getCanonicalFillShape());
    }
    default int getMaxFillCount(@NotNull VoxelShape shape) {
        if (shape.isEmpty())
            throw new IllegalStateException("Fillable shape is empty");
        if (shape.toAabbs().size() != 1)
            throw new IllegalStateException("Fillable shape must have a single cuboid piece shape");

        AABB bounds = shape.bounds();

        int x = FillDefinition.subdivisions(bounds.maxX - bounds.minX);
        int y = FillDefinition.subdivisions(bounds.maxY - bounds.minY);
        int z = FillDefinition.subdivisions(bounds.maxZ - bounds.minZ);

        return x * y * z;
    }

    record FillDefinition(
            int x,
            int y,
            int z,
            double startX,
            double startY,
            double startZ
    ) {
        private static final double EPSILON = 1.0e-6;

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

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Vec3 offset(int index) {
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

        public int surfaceIndex(
                @NotNull Vec3 point,
                @NotNull Direction face
        ) {
            int[] coordinates = {
                    coordinate(point.x, x),
                    coordinate(point.y, y),
                    coordinate(point.z, z)
            };

            int axis = switch (face.getAxis()) {
                case X -> 0;
                case Y -> 1;
                case Z -> 2;
            };

            int[] subdivisions = {x, y, z};

            if (subdivisions[axis] > 1) {
                double surfaceCoordinate = switch (axis) {
                    case 0 -> point.x;
                    case 1 -> point.y;
                    default -> point.z;
                };

                coordinates[axis] = face.getAxisDirection() == Direction.AxisDirection.POSITIVE
                        ? positiveCoordinate(surfaceCoordinate, subdivisions[axis])
                        : negativeCoordinate(surfaceCoordinate, subdivisions[axis]);
            }

            if (!contains(
                    coordinates[0],
                    coordinates[1],
                    coordinates[2]
            ))
                return -1;

            return index(
                    coordinates[0],
                    coordinates[1],
                    coordinates[2]
            );
        }

        private static int coordinate(
                double coordinate,
                int subdivisions
        ) {
            int index = (int) Math.floor(coordinate * subdivisions + EPSILON);

            if (Math.abs(coordinate - 1.0) <= EPSILON)
                return subdivisions - 1;

            return index;
        }

        private static int positiveCoordinate(
                double coordinate,
                int subdivisions
        ) {
            int index = (int) Math.floor(coordinate * subdivisions + EPSILON);

            if (Math.abs(coordinate - 1.0) <= EPSILON)
                return subdivisions - 1;

            return index;
        }

        private static int negativeCoordinate(
                double coordinate,
                int subdivisions
        ) {
            if (Math.abs(coordinate) <= EPSILON)
                return 0;
            return (int) Math.ceil(coordinate * subdivisions - EPSILON) - 1;
        }

        @Contract("_, _, _ -> new")
        private static @NotNull FillDefinition fromShape(
                FillableBlock fillable,
                BlockState state,
                VoxelShape shape
        ) {
            if (shape.isEmpty())
                throw new IllegalStateException("Fillable block has an empty piece shape");
            if (shape.toAabbs().size() != 1)
                throw new IllegalStateException("Fillable block must have a single cuboid piece shape");

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
                    x, y, z,
                    minX, minY, minZ
            );
        }

        public static FillDefinition fromModel(
                BakedModel model,
                BlockState state
        ) {
            RandomSource random = RandomSource.create();

            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;

            for (BakedQuad quad : model.getQuads(
                    state,
                    null,
                    random,
                    ModelData.EMPTY,
                    null
            )) {
                int[] vertices = quad.getVertices();
                VertexFormat format = DefaultVertexFormat.BLOCK;

                int stride = format.getIntegerSize();
                int positionOffset = format.getOffset(0) / Integer.BYTES;

                for (int i = 0; i < 4; i++) {
                    int offset = i * stride + positionOffset;

                    double x = Float.intBitsToFloat(vertices[offset]);
                    double y = Float.intBitsToFloat(vertices[offset + 1]);
                    double z = Float.intBitsToFloat(vertices[offset + 2]);

                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    minZ = Math.min(minZ, z);

                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                    maxZ = Math.max(maxZ, z);
                }
            }

            for (Direction side : Direction.values())
                for (BakedQuad quad : model.getQuads(
                        state,
                        side,
                        random,
                        ModelData.EMPTY,
                        null
                )) {
                    int[] vertices = quad.getVertices();
                    VertexFormat format = DefaultVertexFormat.BLOCK;

                    int stride = format.getIntegerSize();
                    int positionOffset = format.getOffset(0) / Integer.BYTES;

                    for (int i = 0; i < 4; i++) {
                        int offset = i * stride + positionOffset;

                        double x = Float.intBitsToFloat(vertices[offset]);
                        double y = Float.intBitsToFloat(vertices[offset + 1]);
                        double z = Float.intBitsToFloat(vertices[offset + 2]);

                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        minZ = Math.min(minZ, z);

                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                        maxZ = Math.max(maxZ, z);
                    }
                }

            if (!Double.isFinite(minX))
                throw new IllegalStateException("Fillable model has no quads");

            int x = subdivisions(maxX - minX);
            int y = subdivisions(maxY - minY);
            int z = subdivisions(maxZ - minZ);

            return new FillDefinition(
                    x, y, z,
                    minX, minY, minZ
            );
        }

        private static int subdivisions(double size) {
            if (size <= EPSILON)
                throw new IllegalStateException("Fillable piece has zero-sized axis");

            double reciprocal = 1.0 / size;
            int subdivisions = (int) Math.round(reciprocal);

            if (subdivisions < 1 || Math.abs(size * subdivisions - 1.0) > 1.0e-5)
                throw new IllegalStateException("Fillable piece does not evenly divide a block: " + size);

            return subdivisions;
        }
    }
}