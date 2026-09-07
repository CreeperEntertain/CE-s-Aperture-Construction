package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.AbstractDecal;
import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.DecalPreviewer;
import net.centertain.ceac.item.custom.DecalItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public final class DecalPlacement {
    static @Nullable Decal tempDecal;
    private static @Nullable AbstractDecal tempAbstractDecal;
    private static @Nullable DecalPreviewer decalPreview;
    private static boolean precisePlacement = false;

    private static double gridSize = 1.0 / 16.0;
    private static Vec3 gridRotation = new Vec3(0.0, 0.0, 0.0);
    private static Vec3 gridOffset = new Vec3(0.0, 0.0, 0.0);

    public static boolean decalItemPresent;
    public static boolean decalItemSeenThisTick;

    private static int movementDelay = 0;
    private static int stretchDelay = 0;

    private DecalPlacement() {}

    public static void markDecalItemPresent() {
        decalItemSeenThisTick = true;
    }

    public static void cleanup() {
        if (!precisePlacement) {
            setTempDecal(null);
            setDecalPreview(null);
        }
    }
    public static void forceCleanup() {
        setTempDecal(null);
        setDecalPreview(null);
        precisePlacement = false;
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

    public static double getGridSize() {
        return gridSize;
    }
    public static Vec3 getGridRotation() {
        return gridRotation;
    }
    public static Vec3 getGridOffset() {
        return gridOffset;
    }

    public static void setGridSize(double newSize) {
        gridSize = newSize;
    }
    public static void setGridRotation(Vec3 newRotation) {
        gridRotation = newRotation;
    }
    public static void setGridOffset(Vec3 newOffset) {
        gridOffset = newOffset;
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

    public static void suppressPrecisePlacementKeys(InputEvent.Key event) {
        if (!DecalPlacement.getPrecisePlacement())
            return;
        int key = event.getKey();
        boolean movementKeyPressed =
                key == GLFW.GLFW_KEY_W ||
                key == GLFW.GLFW_KEY_A ||
                key == GLFW.GLFW_KEY_S ||
                key == GLFW.GLFW_KEY_D ||
                key == GLFW.GLFW_KEY_Q ||
                key == GLFW.GLFW_KEY_E;
        boolean stretchKeyPressed =
                key == GLFW.GLFW_KEY_R ||
                key == GLFW.GLFW_KEY_F;
        if (event.getAction() == GLFW.GLFW_RELEASE) {
            if (movementKeyPressed)
                movementDelay = 0;
            if (stretchKeyPressed)
                stretchDelay = 0;
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            if (mapping.getKey().getValue() != key)
                continue;
            mapping.setDown(false);
            //noinspection StatementWithEmptyBody
            while (mapping.consumeClick()) {}
        }
        switch (key) {
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
        AbstractDecal abstraction = tempAbstractDecal;
        if (abstraction == null)
            return;
        if (movementDelay == 0 || movementDelay > 10) {
            // TODO: Abstraction movement
            tempAbstractDecal = abstraction;
        }
        movementDelay++;
    }
    private static void adjustAbstractionDepth(Stretch stretch) {
        AbstractDecal abstraction = tempAbstractDecal;
        if (abstraction == null)
            return;
        double blockDepth = abstraction.getBlockDepth();
        switch (stretch) {
            case STRETCH -> blockDepth += Math.pow(stretchDelay, 1.1) * 0.02 + 0.02;
            case SQUASH -> blockDepth -= Math.pow(stretchDelay, 1.1) * 0.02 + 0.02;
        }
        if (blockDepth <= 0.0)
            blockDepth = 0.02;
        if (blockDepth > 16.0)
            blockDepth = 16.0;
        abstraction.setBlockDepth(blockDepth);
        tempAbstractDecal = abstraction;
        stretchDelay++;
    }
}
