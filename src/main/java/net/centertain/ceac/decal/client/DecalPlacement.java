package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.AbstractDecal;
import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.DecalPreviewer;
import net.centertain.ceac.item.custom.DecalItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public final class DecalPlacement {
    static @Nullable Decal tempDecal;
    private static @Nullable AbstractDecal tempAbstractDecal;
    private static @Nullable DecalPreviewer decalPreview;
    private static boolean precisePlacement = false;

    public static boolean decalItemPresent;
    public static boolean decalItemSeenThisTick;

    private DecalPlacement() {}

    public static void markDecalItemPresent() {
        decalItemSeenThisTick = true;
    }

    public static void cleanup() {
        setTempDecal(null);
        setDecalPreview(null);
    }

    public static void setTempDecal(@Nullable Decal decal) {
        tempDecal = decal;
    }
    public static void setTempAbstractDecal(@Nullable AbstractDecal abstraction) {
        tempAbstractDecal = abstraction;
    }
    public static void setDecalPreview(@Nullable DecalPreviewer preview) {
        decalPreview = preview;
    }
    public static void setPrecisePlacement(boolean state) {
         precisePlacement = state;
    }

    public static @Nullable Decal getTempDecal() {
        return tempDecal;
    }
    public static @Nullable AbstractDecal getTempAbstractDecal() {
        return tempAbstractDecal;
    }
    public static @Nullable DecalPreviewer getDecalPreview() {
        return decalPreview;
    }
    public static boolean getPrecisePlacement() {
        return precisePlacement;
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        IN,
        OUT
    }
    private enum Stretch {
        STRETCH,
        SQUASH
    }

    public static void swapPrecisePlacement(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
            return;
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null)
            return;
        boolean mainHand = player.getMainHandItem().getItem() instanceof DecalItem;
        boolean offHand = player.getOffhandItem().getItem() instanceof DecalItem;
        if (!mainHand && !offHand)
            return;
        event.setCanceled(true);

        precisePlacement = !precisePlacement;
    }
    public static void rotateAbstraction(InputEvent.MouseScrollingEvent event) {
        if (!DecalPlacement.getPrecisePlacement())
            return;
        event.setCanceled(true);
        double scroll = event.getScrollDelta();

        AbstractDecal abstraction = tempAbstractDecal;
        if (abstraction == null)
            return;
        byte rotation = abstraction.getRotation();
        int rotationDelta = scroll > 0 ? 1 : -1;
        rotation = (byte) Math.floorMod(rotation + rotationDelta, 16);
        abstraction.setRotation(rotation);
        tempAbstractDecal = abstraction;
    }

    public static void suppressPrecisePlacementKeys() {
        Minecraft minecraft = Minecraft.getInstance();
        int pressedKey = -1;
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            pressedKey = mapping.getKey().getValue();
            if (
                    pressedKey != GLFW.GLFW_KEY_W &&
                    pressedKey != GLFW.GLFW_KEY_A &&
                    pressedKey != GLFW.GLFW_KEY_S &&
                    pressedKey != GLFW.GLFW_KEY_D &&
                    pressedKey != GLFW.GLFW_KEY_Q &&
                    pressedKey != GLFW.GLFW_KEY_E &&
                    pressedKey != GLFW.GLFW_KEY_R &&
                    pressedKey != GLFW.GLFW_KEY_F
            )
                continue;
            mapping.setDown(false);
            mapping.consumeClick();
        }
        switch (pressedKey) {
            case GLFW.GLFW_KEY_W -> moveAbstraction(Direction.UP);
            case GLFW.GLFW_KEY_A -> moveAbstraction(Direction.LEFT);
            case GLFW.GLFW_KEY_S -> moveAbstraction(Direction.DOWN);
            case GLFW.GLFW_KEY_D -> moveAbstraction(Direction.RIGHT);
            case GLFW.GLFW_KEY_Q -> moveAbstraction(Direction.IN);
            case GLFW.GLFW_KEY_E -> moveAbstraction(Direction.OUT);
            case GLFW.GLFW_KEY_R -> adjustAbstractionDepth(Stretch.STRETCH);
            case GLFW.GLFW_KEY_F -> adjustAbstractionDepth(Stretch.SQUASH);
        }
    }
    private static void moveAbstraction(Direction direction) {
        AbstractDecal abstraction = getTempAbstractDecal();
        if (abstraction == null)
            return;
    }
    private static void adjustAbstractionDepth(Stretch stretch) {
        AbstractDecal abstraction = getTempAbstractDecal();
        if (abstraction == null)
            return;
    }
}
