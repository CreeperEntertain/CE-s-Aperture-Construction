package net.centertain.ceac.block.custom.types.combinable;

import net.centertain.ceac.block.custom.types.material_shapes.MaterialShapeRotatable6Way;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class CombinableMaterialShapeTemplate6Way extends MaterialShapeRotatable6Way implements CombinableBlock {
    protected final Map<Direction, VoxelShape> SHAPES;

    protected CombinableMaterialShapeTemplate6Way(
            Properties properties,
            String subcategory,
            Map<Direction, VoxelShape> shapes
    ) {
        super(properties, subcategory);
        this.SHAPES = shapes;
        setCombinableDefaultState();
    }

    protected CombinableMaterialShapeTemplate6Way(
            Properties properties,
            @Nullable String category,
            String subcategory,
            @Nullable Double price,
            Map<Direction, VoxelShape> shapes
    ) {
        super(properties, category, subcategory, price);
        this.SHAPES = shapes;
        setCombinableDefaultState();
    }


    private void setCombinableDefaultState() {
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.WEST)
                .setValue(X_FILLED, false)
                .setValue(Y_FILLED, false)
                .setValue(Z_FILLED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(X_FILLED, Y_FILLED, Z_FILLED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;
        Direction.Axis axis = getInsertionAxis(state, context.getClickedFace());
        return state.setValue(CombinableBlock.propertyForAxis(axis), true);
    }

    @Override
    public Direction.Axis getInsertionAxis(
            BlockState state,
            Direction clickedFace
    ) {
        Vec3 localDirection = transformDirectionToLocal(state, new Vec3(
                clickedFace.getStepX(),
                clickedFace.getStepY(),
                clickedFace.getStepZ()
        ));
        return Direction.getNearest(
                localDirection.x,
                localDirection.y,
                localDirection.z
        ).getAxis();
    }

    @Override
    public VoxelShape getAxisShape(
            BlockState state,
            Direction.Axis axis
    ) {
        Basis basis = getBasis(state);
        Vec3 direction = switch (axis) {
            case X -> basis.x();
            case Y -> basis.y();
            case Z -> basis.z();
        };
        Direction facing = Direction.getNearest(direction.x, direction.y, direction.z);
        return SHAPES.get(facing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getCombinableShape(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getCombinableShape(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getOcclusionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos
    ) {
        return getCombinableShape(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<ItemStack> getDrops(
            @NotNull BlockState state,
            @NotNull LootParams.Builder builder) {
        int count = getPieceCount(state);
        return count == 0
                ? List.of()
                : List.of(new ItemStack(asItem(), count));
    }
}
