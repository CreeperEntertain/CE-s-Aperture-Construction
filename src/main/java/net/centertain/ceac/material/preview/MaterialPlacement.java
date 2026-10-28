package net.centertain.ceac.material.preview;

import net.centertain.ceac.block.custom.types.material_shapes.MaterialShape;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

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
            case GLFW.GLFW_KEY_E -> toggleTextureLock();
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
                newRotation,
                matItem.isTextureLocked(stack),
                matItem.getLockedMaterialCoordinate(stack)
        ));
    }

    private static void toggleTextureLock() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null)
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

        Vector2i selectedCoordinate = MaterialPreviewer.getMaterialCoordinate();
        BlockPos position = MaterialPreviewer.getPosition();
        MaterialShapeFaceInstance face = MaterialPreviewer.getFace();
        if (
                selectedCoordinate == null ||
                position == null ||
                face == null ||
                MaterialPreviewer.getMaterial() != matItem.getMaterial()
        )
            return;

        if (!matItem.isTextureLocked(stack)) {
            matItem.setLockedMaterialCoordinate(stack, selectedCoordinate);
            matItem.setTextureLocked(stack, true);
        } else {
            Vector2i lockedCoordinate = matItem.getLockedMaterialCoordinate(stack);
            BlockState state = minecraft.level.getBlockState(position);
            Vector2i baseCoordinate = matItem.getMaterial().getCoordinate(
                    face, position, state, matItem.getMaterialRotation(stack)
            );

            matItem.setMaterialCoordinateOffset(stack, new Vector2i(
                    lockedCoordinate.x - baseCoordinate.x,
                    lockedCoordinate.y - baseCoordinate.y
            ));
            matItem.setTextureLocked(stack, false);
        }

        ModNetworking.CHANNEL.sendToServer(new MaterialOffsetPacket(
                hand,
                matItem.getMaterialCoordinateOffset(stack),
                matItem.getMaterialRotation(stack),
                matItem.isTextureLocked(stack),
                matItem.getLockedMaterialCoordinate(stack)
        ));
    }

    @SuppressWarnings("SuspiciousNameCombination")
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
        Vector2i gridDelta = getSurfaceGridDelta(direction, player);
        if (gridDelta == null)
            return;

        int textureDeltaX;
        int textureDeltaY;
        switch (matItem.getMaterialRotation(stack)) {
            case 1 -> {
                textureDeltaX = gridDelta.y;
                textureDeltaY = -gridDelta.x;
            } case 2 -> {
                textureDeltaX = -gridDelta.x;
                textureDeltaY = -gridDelta.y;
            } case 3 -> {
                textureDeltaX = -gridDelta.y;
                textureDeltaY = gridDelta.x;
            } default -> {
                textureDeltaX = gridDelta.x;
                textureDeltaY = gridDelta.y;
            }
        }

        if (matItem.isTextureLocked(stack)) {
            Vector2i lockedCoordinate = matItem.getLockedMaterialCoordinate(stack);
            matItem.setLockedMaterialCoordinate(stack, new Vector2i(
                    lockedCoordinate.x - textureDeltaX,
                    lockedCoordinate.y - textureDeltaY
            ));
        } else {
            Vector2i offset = matItem.getMaterialCoordinateOffset(stack);
            matItem.setMaterialCoordinateOffset(stack, new Vector2i(
                    offset.x - textureDeltaX,
                    offset.y - textureDeltaY
            ));
        }

        ModNetworking.CHANNEL.sendToServer(new MaterialOffsetPacket(
                hand,
                matItem.getMaterialCoordinateOffset(stack),
                matItem.getMaterialRotation(stack),
                matItem.isTextureLocked(stack),
                matItem.getLockedMaterialCoordinate(stack)
        ));
    }

    private static @Nullable Vector2i getSurfaceGridDelta(Direction direction, Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        MaterialShapeFaceInstance face = MaterialPreviewer.getFace();
        BlockPos position = MaterialPreviewer.getPosition();
        if (minecraft.level == null || face == null || position == null)
            return null;

        BlockState state = minecraft.level.getBlockState(position);
        if (!(state.getBlock() instanceof MaterialShape shape))
            return null;

        List<Vec3> vertices = face.getVertices()
                .stream()
                .map(vertex -> shape.transformPointToWorld(state, vertex))
                .toList();
        if (vertices.size() < 3)
            return null;

        Vec3 normal = vertices.get(1).subtract(vertices.get(0)).cross(vertices.get(2).subtract(vertices.get(0))).normalize();
        net.minecraft.core.Direction side = net.minecraft.core.Direction.getNearest(
                normal.x,
                normal.y * (1.0 - 1.0e-6),
                normal.z
        );
        if (
                side != net.minecraft.core.Direction.UP &&
                side != net.minecraft.core.Direction.DOWN
        )
            return switch (direction) {
                case UP -> new Vector2i(0, -1);
                case DOWN -> new Vector2i(0, 1);
                case LEFT -> new Vector2i(-1, 0);
                case RIGHT -> new Vector2i(1, 0);
            };

        int facing = Math.floorMod(Math.round(player.getYRot() / 90.0f), 4);

        int forwardX;
        int forwardZ;
        switch (facing) {
            case 1 -> {
                forwardX = -1;
                forwardZ = 0;
            } case 2 -> {
                forwardX = 0;
                forwardZ = -1;
            } case 3 -> {
                forwardX = 1;
                forwardZ = 0;
            } default -> {
                forwardX = 0;
                forwardZ = 1;
            }
        }

        int moveX;
        int moveZ;
        switch (direction) {
            case UP -> {
                moveX = forwardX;
                moveZ = forwardZ;
            } case DOWN -> {
                moveX = -forwardX;
                moveZ = -forwardZ;
            } case LEFT -> {
                moveX = forwardZ;
                moveZ = -forwardX;
            } case RIGHT -> {
                moveX = -forwardZ;
                moveZ = forwardX;
            } default -> throw new IllegalStateException("Unexpected direction: " + direction);
        }

        return new Vector2i(moveX, side == net.minecraft.core.Direction.UP ? moveZ : -moveZ);
    }
}
