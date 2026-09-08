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

    private static final double[] GRID_SIZES = {
            0.0,        // 0: FREEFORM PLACEMENT!
            1.0 / 16.0, // 1
            1.0 / 8.0,  // 2
            1.0 / 4.0,  // 3: DEFAULT!
            1.0 / 2.0,  // 4
            1.0,        // 5
            2.0,        // 6
            4.0         // 7
    };
    private static int gridSizeIndex = 3;
    private static double gridSize = GRID_SIZES[gridSizeIndex];

    public static boolean decalItemPresent;
    public static boolean decalItemSeenThisTick;

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
    public static int getGridSizeIndex() {
        return gridSizeIndex;
    }

    public static void setGridSize(double newSize) {
        gridSize = newSize;
    }
    public static void setGridSizeIndex(int newIndex) {
        gridSizeIndex = newIndex;
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
    private enum Tilt {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private enum GridResize {
        BIGGER,
        SMALLER
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
        boolean stretchKeyPressed =
                key == GLFW.GLFW_KEY_R ||
                key == GLFW.GLFW_KEY_F;
        if (event.getAction() == GLFW.GLFW_RELEASE) {
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
            case GLFW.GLFW_KEY_Q -> moveAbstraction(Direction.OUT);
            case GLFW.GLFW_KEY_E -> moveAbstraction(Direction.IN);

            case GLFW.GLFW_KEY_R -> adjustAbstractionDepth(Stretch.STRETCH);
            case GLFW.GLFW_KEY_F -> adjustAbstractionDepth(Stretch.SQUASH);

            case GLFW.GLFW_KEY_UP -> tiltAbstraction(Tilt.UP);
            case GLFW.GLFW_KEY_DOWN -> tiltAbstraction(Tilt.DOWN);
            case GLFW.GLFW_KEY_LEFT -> tiltAbstraction(Tilt.LEFT);
            case GLFW.GLFW_KEY_RIGHT -> tiltAbstraction(Tilt.RIGHT);

            case GLFW.GLFW_KEY_PAGE_UP -> resizeGrid(GridResize.BIGGER);
            case GLFW.GLFW_KEY_PAGE_DOWN -> resizeGrid(GridResize.SMALLER);
        }
    }
    private static void moveAbstraction(Direction direction) {
        AbstractDecal abstraction = tempAbstractDecal;
        if (abstraction == null)
            return;
        double step = gridSizeIndex == 0 ? 0.02 : gridSize;
        Vec3 normal = abstraction.getNormal().normalize();
        Vec3 reference = Math.abs(normal.y) < 0.999
                ? new Vec3(0, 1, 0)
                : new Vec3(1, 0, 0);
        Vec3 right = reference.cross(normal).normalize();
        Vec3 up = normal.cross(right).normalize();
        double rotation = (Math.PI * 2.0 / 16.0) * abstraction.getRotation();
        right = right
                .scale(Math.cos(rotation))
                .add(up.scale(Math.sin(rotation)))
                .normalize();
        up = normal.cross(right).normalize();
        Vec3 movement = switch (direction) {
            case UP -> up.scale(step);
            case DOWN -> up.scale(-step);
            case LEFT -> right.scale(-step);
            case RIGHT -> right.scale(step);
            case IN -> normal.scale(-step);
            case OUT -> normal.scale(step);
        };
        abstraction.setOrigin(abstraction.getOrigin().add(movement));
        tempAbstractDecal = abstraction;
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
    private static void tiltAbstraction(Tilt tilt) {
        AbstractDecal abstraction = tempAbstractDecal;
        if (abstraction == null)
            return;
        Vec3 normal = abstraction.getNormal();
        Vec3 reference = Math.abs(normal.y) < 0.999
                ? new Vec3(0, 1, 0)
                : new Vec3(1, 0, 0);
        Vec3 right = reference.cross(normal).normalize();
        Vec3 up = normal.cross(right).normalize();
        double rotation = (Math.PI * 2.0 / 16.0) * abstraction.getRotation();
        right = right
                .scale(Math.cos(rotation))
                .add(up.scale(Math.sin(rotation)))
                .normalize();
        up = normal.cross(right).normalize();

        double tiltAngle = Math.toRadians(5.0);
        Vec3 tiltedNormal = switch (tilt) {
            case UP -> rotateVector(normal, right, -tiltAngle);
            case DOWN -> rotateVector(normal, right, tiltAngle);
            case LEFT -> rotateVector(normal, up, -tiltAngle);
            case RIGHT -> rotateVector(normal, up, tiltAngle);
        };

        if (Math.abs(tiltedNormal.y) >= 0.999)
            return;

        abstraction.setNormal(tiltedNormal);
        tempAbstractDecal = abstraction;
    }
    private static void resizeGrid(GridResize gridResize) {
        int min = 0;
        int max = GRID_SIZES.length - 1;
        switch (gridResize) {
            case BIGGER -> gridSizeIndex = Math.min(max, gridSizeIndex + 1);
            case SMALLER -> gridSizeIndex = Math.max(min, gridSizeIndex - 1);
        }
        gridSize = GRID_SIZES[gridSizeIndex];
    }

    // Rodrigues' rotation formula
    // No, IntelliJ, this is not a typo!
    private static Vec3 rotateVector(Vec3 vector, Vec3 axis, double angle) {
        return vector
                .scale(Math.cos(angle))
                .add(axis.cross(vector).scale(Math.sin(angle)))
                .add(axis.scale(axis.dot(vector) * (1.0 - Math.cos(angle))));
    }
}
