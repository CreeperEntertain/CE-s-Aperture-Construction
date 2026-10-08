package net.centertain.ceac.block.custom.material_shapes.basic_shapes;

import net.centertain.ceac.block.custom.FillableBlock;
import net.centertain.ceac.block.custom.MaterialShapeRotatable24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.models.basic_shapes.BitGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Bit extends MaterialShapeRotatable24Way implements FillableBlock {
    private static final IntegerProperty FILL = IntegerProperty.create("fill", 0, 255);

    private static final Map<Direction, VoxelShape[]> SHAPES =
            MaterialShapeVoxelHelper24Way.makeShapes(() -> BitGeometry.COLLISION_SHAPE, false);

    public Bit(Properties properties) {
        super(properties, CategoryConstants.Sub.Shapes.SLOPES_FULL);
        registerDefaultState(defaultBlockState().setValue(FILL, 1));
    }

    @Override
    public IntegerProperty getFillProperty() {
        return FILL;
    }

    @Override
    public @NotNull BakedModel getFillModel(BlockState state) {
        return getBakedModel();
    }

    @Override
    public VoxelShape getFillPieceShape(
            @NotNull BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(ROTATION)];
    }

    @SuppressWarnings("deprecation") // Literally what the docs told me to use. Why would you deprecate something that's
    @Override                        // the only real way to do the thing? No, really. Enlighten me.
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getFillPieceShape(state, level, pos, context);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(
            @NotNull BlockPlaceContext context
    ) {
        BlockPos pos = context.getClickedPos();
        BlockState clicked = context.getLevel().getBlockState(pos);
        if (clicked.getBlock() == this) {
            int index = getFillIndex(
                    clicked,
                    context.getLevel(),
                    pos,
                    CollisionContext.empty(),
                    context.getClickLocation(),
                    context.getClickedFace()
            );
            if (index >= 0 && (getFillMask(clicked) & (1 << index)) == 0)
                return setFillMask(clicked, getFillMask(clicked) | (1 << index));
        }

        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;

        return setFillMask(state, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(
            @NotNull BlockState state,
            @NotNull BlockPlaceContext context
    ) {
        if (!context.getItemInHand().is(asItem()))
            return super.canBeReplaced(state, context);
        if (!context.replacingClickedOnBlock())
            return false;

        int index = getFillIndex(
                state,
                context.getLevel(),
                context.getClickedPos(),
                CollisionContext.empty(),
                context.getClickLocation(),
                context.getClickedFace()
        );

        return index >= 0 && (getFillMask(state) & (1 << index)) == 0;
    }

    @Override
    protected void createBlockStateDefinition(
            @NotNull StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(FILL);
    }
}
