package net.centertain.ceac.block.custom;

import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PurchasingTerminal extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty BROKEN = BooleanProperty.create("broken");

    public PurchasingTerminal(Properties properties) {
        super(properties.noOcclusion());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        if (level.isClientSide)
            DistExecutor.unsafeRunWhenOn(
                    Dist.CLIENT,
                    () -> () -> Minecraft.getInstance().setScreen(
                            new PurchasingTermialScreen(player, pos)
                    )
            );

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BROKEN);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return defaultBlockState().setValue(
                FACING,
                context.getHorizontalDirection().getOpposite()
        ).setValue(BROKEN, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case NORTH, UP, DOWN -> Shapes.or(
                    box(1, 3, 1, 15, 14, 13),
                    box(5, 1, 5, 11, 3, 10),
                    box(3, 0, 3, 13, 1, 11)
            );
            case EAST -> Shapes.or(
                    box(3, 3, 1, 15, 14, 15),
                    box(6, 1, 5, 11, 3, 11),
                    box(5, 0, 3, 13, 1, 13)
            );
            case SOUTH -> Shapes.or(
                    box(1, 3, 3, 15, 14, 15),
                    box(5, 1, 6, 11, 3, 11),
                    box(3, 0, 5, 13, 1, 13)
            );
            case WEST -> Shapes.or(
                    box(1, 3, 1, 13, 14, 15),
                    box(5, 1, 5, 10, 3, 11),
                    box(3, 0, 3, 11, 1, 13)
            );
        };
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(
            @NotNull BlockState state,
            @NotNull HitResult target,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull Player player
    ) {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTagElement(BlockItem.BLOCK_STATE_TAG)
                .putBoolean("broken", state.getValue(BROKEN));
        return stack;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<ItemStack> getDrops(
            @NotNull BlockState state,
            LootParams.@NotNull Builder builder
    ) {
        List<ItemStack> drops = super.getDrops(state, builder);
        for (ItemStack stack : drops)
            if (stack.getItem() == this.asItem())
                stack.getOrCreateTagElement(BlockItem.BLOCK_STATE_TAG)
                        .putBoolean("broken", state.getValue(BROKEN));
        return drops;
    }
}
