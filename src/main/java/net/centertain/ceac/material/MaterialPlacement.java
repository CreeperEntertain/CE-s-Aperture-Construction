package net.centertain.ceac.material;

import net.centertain.ceac.item.custom.MatItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

public final class MaterialPlacement {
    private static boolean adjustOffset = false;

    private MaterialPlacement() {}

    public static boolean getAdjustOffset() {
        return adjustOffset;
    }

    public static void setAdjustOffset(boolean state) {
        adjustOffset = state;
    }

    private enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }


    public static void swapAdjustOffset(InputEvent.MouseButton.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null)
            return;
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
            return;
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        Player player = minecraft.player;
        if (player == null)
            return;
        boolean mainHand = player.getMainHandItem().getItem() instanceof MatItem;
        boolean offHand = player.getOffhandItem().getItem() instanceof MatItem;
        if (!mainHand && !offHand)
            return;
        event.setCanceled(true);

        adjustOffset = !adjustOffset;
    }

    public static void suppressAdjustOffsetKeys(InputEvent.Key event) {
        if (!adjustOffset)
            return;
        int key = event.getKey();
        Minecraft minecraft = Minecraft.getInstance();
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            if (mapping.getKey().getValue() != key)
                continue;
            mapping.setDown(false);
            //noinspection StatementWithEmptyBody
            while (mapping.consumeClick()) {}
        }
        switch (key) {
            case GLFW.GLFW_KEY_W -> shiftMaterialCoordinate(Direction.UP);
            case GLFW.GLFW_KEY_S -> shiftMaterialCoordinate(Direction.DOWN);
            case GLFW.GLFW_KEY_A -> shiftMaterialCoordinate(Direction.LEFT);
            case GLFW.GLFW_KEY_D -> shiftMaterialCoordinate(Direction.RIGHT);
        }
    }

    private static void shiftMaterialCoordinate(Direction direction) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        boolean mainHand = player.getMainHandItem().getItem() instanceof MatItem;
        Item item = mainHand
                ? player.getMainHandItem().getItem()
                : player.getOffhandItem().getItem();
        if (!(item instanceof MatItem matItem))
            return;
        ItemStack stack = mainHand
                ? player.getMainHandItem()
                : player.getOffhandItem();
        switch (direction) {
            case UP -> matItem.setMaterialCoordinateOffset(matItem.getMaterialCoordinateOffset().add(0, 1));
            case DOWN -> matItem.setMaterialCoordinateOffset(matItem.getMaterialCoordinateOffset().add(0, -1));
            case LEFT -> matItem.setMaterialCoordinateOffset(matItem.getMaterialCoordinateOffset().add(-1, 0));
            case RIGHT -> matItem.setMaterialCoordinateOffset(matItem.getMaterialCoordinateOffset().add(1, 0));
        }
    }
}
