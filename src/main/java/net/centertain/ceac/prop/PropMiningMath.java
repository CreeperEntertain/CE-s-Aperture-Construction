package net.centertain.ceac.prop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public final class PropMiningMath {
    private PropMiningMath() {}

    @SuppressWarnings("deprecation")
    public static boolean hasRequiredTool(
            Player player,
            PropDefinition definition
    ) {
        int requiredLevel = definition.getRequiredToolLevel();
        if (requiredLevel < 0)
            return true;

        ItemStack stack = player.getMainHandItem();
        ToolAction action = definition.getEffectiveToolAction();
        if (action == null || !stack.canPerformAction(action))
            return false;
        if (!(stack.getItem() instanceof TieredItem tieredItem))
            return false;

        return tieredItem.getTier().getLevel() >= requiredLevel;
    }

    @SuppressWarnings("deprecation")
    public static float getDestroyProgress(
            Player player,
            PropDefinition definition,
            BlockPos position
    ) {
        if (player.getAbilities().instabuild)
            return 1.0f;

        float hardness = definition.getDestroyTime();
        if (hardness < 0.0f)
            return 0.0f;
        if (hardness == 0.0f)
            return 1.0f;

        ItemStack stack = player.getMainHandItem();
        ToolAction action = definition.getEffectiveToolAction();
        boolean effectiveTool = action == null || stack.canPerformAction(action);

        BlockState referenceState;
        if (!effectiveTool || action == null)
            referenceState = Blocks.AIR.defaultBlockState();
        else
            referenceState = getReferenceState(action);

        float speed = player.getDestroySpeed(referenceState);
        if (speed <= 0.0f)
            return 0.0f;

        boolean correctTool = hasRequiredTool(player, definition);
        float divisor = correctTool ? 30.0f : 100.0f;

        return speed / hardness / divisor;
    }

    private static BlockState getReferenceState(ToolAction action) {
        if (ToolActions.PICKAXE_DIG.equals(action))
            return Blocks.STONE.defaultBlockState();
        if (ToolActions.AXE_DIG.equals(action))
            return Blocks.OAK_LOG.defaultBlockState();
        if (ToolActions.SHOVEL_DIG.equals(action))
            return Blocks.DIRT.defaultBlockState();
        if (ToolActions.HOE_DIG.equals(action))
            return Blocks.HAY_BLOCK.defaultBlockState();
        if (ToolActions.SWORD_DIG.equals(action))
            return Blocks.COBWEB.defaultBlockState();
        return Blocks.AIR.defaultBlockState();
    }
}
