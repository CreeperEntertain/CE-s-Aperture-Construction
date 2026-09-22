package net.centertain.ceac.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Material {
    private static final double DIRECTION_BIAS = 1.0e-6;

    private final String name;
    private final SoundType soundType;
    private final Map<Vector2i, ResourceLocation> textures;
    private final Vector2i tilingSize;

    protected Material(
            String name,
            SoundType soundType,
            Map<Vector2i, ResourceLocation> textures
    ) {
        this.name = name;
        this.soundType = soundType;
        this.textures = Map.copyOf(textures);
        this.tilingSize = inspectTiling(textures.keySet());
    }

    private Vector2i inspectTiling(Set<Vector2i> coordinates) {
        List<Integer> xCoordinates = new ArrayList<>();
        List<Integer> yCoordinates = new ArrayList<>();

        for (Vector2i coordinate : coordinates) {
            if (!xCoordinates.contains(coordinate.x))
                xCoordinates.add(coordinate.x);
            if (!yCoordinates.contains(coordinate.y))
                yCoordinates.add(coordinate.y);
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (int x : xCoordinates) {
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
        }

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int y : yCoordinates) {
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        if (minX < 0 || minY < 0)
            throw new IllegalStateException("Material \"" + name + "\" contains at least one coordinate less than zero.");
        if (
                maxX > xCoordinates.size() - 1 ||
                maxY > yCoordinates.size() - 1
        )
            throw new IllegalStateException("Coordinates for material \"" + name + "\" contain at least one hole");

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        if (coordinates.size() != width * height)
            throw new IllegalStateException("Coordinates for material \"" + name + "\" do not form a rectangle.");
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                if (!coordinates.contains(new Vector2i(x, y)))
                    throw new IllegalStateException("Coordinates for material \"" + name + "\" do not form a rectangle.");

        return new Vector2i(width, height);
    }

    public String getName() {
        return name;
    }
    public SoundType getSoundType() {
        return soundType;
    }
    public Map<Vector2i, ResourceLocation> getTextures() {
        return textures;
    }
    public @Nullable ResourceLocation getTexture(Vector2i coordinate) {
        return textures.get(coordinate);
    }
    public Vector2i getCoordinate(
            MaterialShapeFace face,
            BlockPos pos
    ) {
        List<Vec3> vertices = face.getVertices();

        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();

        Direction side = Direction.getNearest(
                normal.x,
                normal.y * (1.0 - DIRECTION_BIAS),
                normal.z
        );

        int width = tilingSize.x;
        int height = tilingSize.y;

        return switch (side) {
            case UP -> new Vector2i(
                    Math.floorMod(pos.getX(), width),
                    Math.floorMod(pos.getZ(), height)
            );
            case DOWN -> new Vector2i(
                    Math.floorMod(-pos.getX() - 1, width),
                    Math.floorMod(pos.getZ(), height)
            );
            case NORTH -> new Vector2i(
                    Math.floorMod(-pos.getX() - 1, width),
                    Math.floorMod(pos.getY(), height)
            );
            case SOUTH -> new Vector2i(
                    Math.floorMod(pos.getX(), width),
                    Math.floorMod(pos.getY(), height)
            );
            case WEST -> new Vector2i(
                    Math.floorMod(pos.getZ(), width),
                    Math.floorMod(pos.getY(), height)
            );
            case EAST -> new Vector2i(
                    Math.floorMod(-pos.getZ() - 1, width),
                    Math.floorMod(pos.getY(), height)
            );
        };
    }
    public Vector2i getTilingSize() {
        return tilingSize;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // ?????
    public boolean containsCoordinate(Vector2i coordinate) {
        return textures.containsKey(coordinate);
    }
}
