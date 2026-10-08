package net.centertain.ceac.block.custom.material_shapes.basic_shapes;

import net.centertain.ceac.block.custom.FillableBlock;
import net.centertain.ceac.block.custom.FillableBlockTemplate24Way;
import net.centertain.ceac.block.custom.MaterialShapeRotatable24Way;
import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.material.shapes.loaders.basic_shapes.BitLoader;
import net.centertain.ceac.material.shapes.models.basic_shapes.BitGeometry;
import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper24Way;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Bit extends FillableBlockTemplate24Way {
    private static final Map<Direction, VoxelShape[]> SHAPES =
            MaterialShapeVoxelHelper24Way.makeShapes(() -> BitGeometry.COLLISION_SHAPE, false);

    public Bit(Properties properties) {
        super(properties, CategoryConstants.Sub.Shapes.SLOPES_FULL);
    }

    @Override
    public VoxelShape getCanonicalFillShape() {
        return SHAPES.get(Direction.WEST)[0];
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
        return getFilledShape(state, level, pos, context);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(
            @NotNull BlockPlaceContext context
    ) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clicked = context.getLevel().getBlockState(clickedPos);
        if (clicked.getBlock() == this) {
            int index = getFillIndex(
                    clicked,
                    context.getLevel(),
                    clickedPos,
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

        int index = getFillIndex(
                state,
                level,
                clickedPos,
                CollisionContext.empty(),
                context.getClickLocation(),
                context.getClickedFace().getOpposite()
        );
        if (index >= 0)
            return setFillMask(state, 1 << index);

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
}
