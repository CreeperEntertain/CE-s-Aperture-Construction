package net.centertain.ceac.material;

import net.centertain.ceac.block.custom.types.material_shapes.MaterialShape;
import net.centertain.ceac.material.shapes.MaterialShapeFace;
import net.centertain.ceac.material.shapes.MaterialShapeFaceInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
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
    public Vector2i getTilingSize() {
        return tilingSize;
    }

    public Vector2i getCoordinate(
            MaterialShapeFace face,
            BlockPos pos,
            BlockState state
    ) {
        return getCoordinate(face, pos, state, 0);
    }

    public Vector2i getCoordinate(
            MaterialShapeFace face,
            BlockPos pos,
            BlockState state,
            int rotation
    ) {
        MaterialShape shape = face.getOwner();
        List<Vec3> vertices = face.getVertices()
                .stream()
                .map(vertex -> shape.transformPointToWorld(state, vertex))
                .toList();
        return getCoordinate(vertices, pos, rotation);
    }

    public Vector2i getCoordinate(
            MaterialShapeFaceInstance instance,
            BlockPos pos,
            BlockState state
    ) {
        return getCoordinate(instance, pos, state, 0);
    }

    public Vector2i getCoordinate(
            MaterialShapeFaceInstance instance,
            BlockPos pos,
            BlockState state,
            int rotation
    ) {
        MaterialShape shape = instance.face().getOwner();
        List<Vec3> vertices = instance.getVertices()
                .stream()
                .map(vertex -> shape.transformPointToWorld(state, vertex))
                .toList();
        return getCoordinate(vertices, pos, rotation);
    }

    private Vector2i getCoordinate(
            List<Vec3> vertices,
            BlockPos pos,
            int rotation
    ) {
        Vec3 a = vertices.get(0);
        Vec3 b = vertices.get(1);
        Vec3 c = vertices.get(2);

        Vec3 normal = b.subtract(a)
                .cross(c.subtract(a))
                .normalize();

        Direction side = Direction.getNearest(
                normal.x,
                normal.y * (1.0 - DIRECTION_BIAS),
                normal.z
        );

        int width = tilingSize.x;
        int height = tilingSize.y;

        int gridX;
        int gridY;

        switch (side) {
            case UP -> {
                gridX = pos.getX();
                gridY = pos.getZ();
            } case DOWN -> {
                gridX = pos.getX();
                gridY = -pos.getZ() - 1;
            } case NORTH -> {
                gridX = -pos.getX() - 1;
                gridY = -pos.getY() - 1;
            } case SOUTH -> {
                gridX = pos.getX();
                gridY = -pos.getY() - 1;
            } case WEST -> {
                gridX = pos.getZ();
                gridY = -pos.getY() - 1;
            } case EAST -> {
                gridX = -pos.getZ() - 1;
                gridY = -pos.getY() - 1;
            } default -> throw new IllegalStateException("Unexpected direction: " + side);
        }

        return switch (Math.floorMod(rotation, 4)) {
            case 1 -> new Vector2i(
                    Math.floorMod(gridY, width),
                    Math.floorMod(-gridX - 1, height)
            );
            case 2 -> new Vector2i(
                    Math.floorMod(-gridX - 1, width),
                    Math.floorMod(-gridY - 1, height)
            );
            case 3 -> new Vector2i(
                    Math.floorMod(-gridY - 1, width),
                    Math.floorMod(gridX, height)
            );
            default -> new Vector2i(
                    Math.floorMod(gridX, width),
                    Math.floorMod(gridY, height)
            );
        };
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // ?????
    public boolean containsCoordinate(Vector2i coordinate) {
        return textures.containsKey(coordinate);
    }
}
