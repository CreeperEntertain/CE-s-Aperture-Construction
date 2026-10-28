package net.centertain.ceac.material.preview;

import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.network.MaterialOffsetPacket;
import net.centertain.ceac.material.shapes.MaterialShapeFaceInstance;
import net.centertain.ceac.network.ModNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.InputEvent;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public final class MaterialPlacement {
    private static boolean adjustOffset = false;

    private static boolean matItemPresent;
    private static boolean matItemSeenThisTick;

    private MaterialPlacement() {}

    public static boolean getAdjustOffset() {
        return adjustOffset;
    }

    public static void setAdjustOffset(boolean state) {
        adjustOffset = state;

         if (!state) {
             MaterialPreviewer.destroy();
             MaterialPreviewer.setPreviewVisible(false);
         }
    }

    public static void markMatItemPresent() {
        matItemSeenThisTick = true;
    }

    public static void forceCleanup() {
        setAdjustOffset(false);
    }
    public static void previewClearing() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            matItemPresent = false;
            matItemSeenThisTick = false;
            forceCleanup();
            return;
        }
        if (matItemPresent && !matItemSeenThisTick)
            forceCleanup();
        matItemPresent = matItemSeenThisTick;
        matItemSeenThisTick = false;
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public static void suppressAdjustOffsetKeys(
            InputEvent.Key event
    ) {
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
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        switch (key) {
            case GLFW.GLFW_KEY_ENTER -> MaterialPreviewer.setHelpScreenShown(!MaterialPreviewer.getHelpScreenShown());
            case GLFW.GLFW_KEY_W -> shiftMaterialCoordinate(Direction.UP);
            case GLFW.GLFW_KEY_S -> shiftMaterialCoordinate(Direction.DOWN);
            case GLFW.GLFW_KEY_A -> shiftMaterialCoordinate(Direction.LEFT);
            case GLFW.GLFW_KEY_D -> shiftMaterialCoordinate(Direction.RIGHT);
            case GLFW.GLFW_KEY_R -> rotateMaterial();
        }
    }

    private static void rotateMaterial() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null)
            return;

        InteractionHand hand;
        ItemStack stack;
        MatItem matItem;

        if (player.getMainHandItem().getItem() instanceof MatItem mainHandItem) {
            hand = InteractionHand.MAIN_HAND;
            stack = player.getMainHandItem();
            matItem = mainHandItem;
        } else if (player.getOffhandItem().getItem() instanceof MatItem offHandItem) {
            hand = InteractionHand.OFF_HAND;
            stack = player.getOffhandItem();
            matItem = offHandItem;
        } else
            return;

        int oldRotation = matItem.getMaterialRotation(stack);
        int newRotation = (oldRotation + 1) % 4;

        Vector2i selectedCoordinate = MaterialPreviewer.getMaterialCoordinate();
        BlockPos position = MaterialPreviewer.getPosition();
        MaterialShapeFaceInstance face = MaterialPreviewer.getFace();

        if (
                selectedCoordinate != null &&
                position != null &&
                face != null &&
                minecraft.level != null &&
                MaterialPreviewer.getMaterial() == matItem.getMaterial()
        ) {
            BlockState state = minecraft.level.getBlockState(position);
            Vector2i newBaseCoordinate = matItem.getMaterial().getCoordinate(face, position, state, newRotation);
            int width = matItem.getMaterial().getTilingSize().x;
            int height = matItem.getMaterial().getTilingSize().y;
            Vector2i preservedOffset = new Vector2i(
                    Math.floorMod(selectedCoordinate.x - newBaseCoordinate.x, width),
                    Math.floorMod(selectedCoordinate.y - newBaseCoordinate.y, height)
            );

            matItem.setMaterialCoordinateOffset(stack, preservedOffset);
        }

        matItem.setMaterialRotation(stack, newRotation);
        MaterialPreviewer.setMaterialRotation(newRotation);

        ModNetworking.CHANNEL.sendToServer(new MaterialOffsetPacket(
                hand,
                matItem.getMaterialCoordinateOffset(stack),
                newRotation
        ));
    }

    private static void shiftMaterialCoordinate(
            Direction direction
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null)
            return;

        InteractionHand hand;
        ItemStack stack;

        if (player.getMainHandItem().getItem() instanceof MatItem) {
            hand = InteractionHand.MAIN_HAND;
            stack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof MatItem) {
            hand = InteractionHand.OFF_HAND;
            stack = player.getOffhandItem();
        } else
            return;

        MatItem matItem = (MatItem) stack.getItem();

        Vector2i offset = matItem.getMaterialCoordinateOffset(stack);

        int x = offset.x;
        int y = offset.y;

        switch (direction) {
            case UP -> y++;
            case DOWN -> y--;
            case LEFT -> x++;
            case RIGHT -> x--;
        }

        Vector2i newOffset = new Vector2i(x, y);

        matItem.setMaterialCoordinateOffset(stack, newOffset);

        ModNetworking.CHANNEL.sendToServer(new MaterialOffsetPacket(
                hand,
                newOffset,
                matItem.getMaterialRotation(stack)
        ));
    }
}
