package net.centertain.ceac.block.custom;

import net.centertain.ceac.screen.PurchasingTermialScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

public class PurchasingTerminal extends Block {
    public PurchasingTerminal(Properties properties) {
        super(properties);
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
                            new PurchasingTermialScreen(player)
                    )
            );

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
