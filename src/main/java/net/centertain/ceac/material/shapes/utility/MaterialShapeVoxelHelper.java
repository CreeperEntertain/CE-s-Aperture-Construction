package net.centertain.ceac.material.shapes.utility;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MaterialShapeVoxelHelper {
    private static final int SIZE = 16;
    private static final double EPSILON = 1.0e-9;

    private MaterialShapeVoxelHelper() {}

    @FunctionalInterface
    public interface ShapeFactory {
        VoxelShape create(Direction facing, int rotation);
    }

    public static Map<Direction, VoxelShape[]> makeShapes(ShapeFactory factory) {
        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);

        for (Direction facing : Direction.values()) {
            VoxelShape[] rotations = new VoxelShape[4];
            for (int rotation = 0; rotation < 4; rotation++)
                rotations[rotation] = compact(factory.create(facing, rotation));
            shapes.put(facing, rotations);
        }
        return Map.copyOf(shapes);
    }

    private static VoxelShape compact(VoxelShape source) {
        boolean[][][] voxels = rasterize(source);
        List<Cuboid> best = null;

        for (Direction.Axis axis : Direction.Axis.values()) {
            List<Cuboid> cuboids = compactAlong(voxels, axis);
            if (best == null || cuboids.size() < best.size())
                best = cuboids;
        }

        VoxelShape result = Shapes.empty();
        if (best == null) // Because Java's definite-assignment checking is stupid
            return result;

        for (Cuboid cuboid : best)
            result = Shapes.or(result, Shapes.box(
                    cuboid.minX / (double) SIZE,
                    cuboid.minY / (double) SIZE,
                    cuboid.minZ / (double) SIZE,
                    cuboid.maxX / (double) SIZE,
                    cuboid.maxY / (double) SIZE,
                    cuboid.maxZ / (double) SIZE
            ));

        return result;
    }

    private static boolean[][][] rasterize(VoxelShape source) {
        boolean[][][] voxels = new boolean[SIZE][SIZE][SIZE];

        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            int startX = Math.max(0, (int) Math.floor(minX * SIZE));
            int endX = Math.min(SIZE - 1, (int) Math.ceil(maxX * SIZE) - 1);

            int startY = Math.max(0, (int) Math.floor(minY * SIZE));
            int endY = Math.min(SIZE - 1, (int) Math.ceil(maxY * SIZE) - 1);

            int startZ = Math.max(0, (int) Math.floor(minZ * SIZE));
            int endZ = Math.min(SIZE - 1, (int) Math.ceil(maxZ * SIZE) - 1);

            for (int x = startX; x <= endX; x++) {
                double voxelMinX = x / (double) SIZE;
                double voxelMaxX = (x + 1) / (double) SIZE;
                double overlapX = Math.min(maxX, voxelMaxX) - Math.max(minX, voxelMinX);
                if (overlapX <= EPSILON)
                    continue;

                for (int y = startY; y <= endY; y++) {
                    double voxelMinY = y / (double) SIZE;
                    double voxelMaxY = (y + 1) / (double) SIZE;
                    double overlapY = Math.min(maxY, voxelMaxY) - Math.max(minY, voxelMinY);
                    if (overlapY <= EPSILON)
                        continue;

                    for (int z = startZ; z <= endZ; z++) {
                        double voxelMinZ = z / (double) SIZE;
                        double voxelMaxZ = (z + 1) / (double) SIZE;
                        double overlapZ = Math.min(maxZ, voxelMaxZ) - Math.max(minZ, voxelMinZ);
                        if (overlapZ <= EPSILON)
                            continue;

                        voxels[x][y][z] = true;
                    }
                }
            }
        });

        return voxels;
    }

    private static List<Cuboid> compactAlong(
            boolean[][][] voxels,
            Direction.Axis axis
    ) {
        List<Cuboid> cuboids = new ArrayList<>();
        List<ActiveCuboid> active = new ArrayList<>();

        for (int layer = 0; layer < SIZE; layer++) {
            List<Rectangle> rectangles = decomposeLayer(voxels, axis, layer);
            List<ActiveCuboid> next = new ArrayList<>();

            for (Rectangle rectangle : rectangles) {
                ActiveCuboid match = null;

                for (ActiveCuboid cuboid : active) {
                    if (!cuboid.matches(rectangle))
                        continue;
                    match = cuboid;
                    break;
                }

                if (match != null) {
                    match.depth++;
                    active.remove(match);
                    next.add(match);
                } else {
                    next.add(new ActiveCuboid(
                            axis,
                            layer,
                            rectangle.u,
                            rectangle.v,
                            rectangle.width,
                            rectangle.height
                    ));
                }
            }

            for (ActiveCuboid cuboid: active)
                cuboids.add(cuboid.finish());

            active = next;
        }

        for (ActiveCuboid cuboid : active)
            cuboids.add(cuboid.finish());

        return cuboids;
    }

    private static List<Rectangle> decomposeLayer(
            boolean[][][] voxels,
            Direction.Axis axis,
            int layer
    ) {
        boolean[][] remaining = new boolean[SIZE][SIZE];
        for (int v = 0; v < SIZE; v++)
            for (int u = 0; u < SIZE; u++)
                remaining[v][u] = isFilled(voxels, axis, layer, u, v);

        List<Rectangle> rectangles = new ArrayList<>();

        while (true) {
            Rectangle rectangle = findLargestRectangle(remaining);
            if (rectangle == null)
                break;

            rectangles.add(rectangle);

            for (int v = rectangle.v; v < rectangle.v + rectangle.height; v++)
                for (int u = rectangle.u; u < rectangle.u + rectangle.width; u++)
                    remaining[v][u] = false;
        }

        return rectangles;
    }

    private static Rectangle findLargestRectangle(boolean[][] filled) {
        int[] heights = new int[SIZE];

        Rectangle best = null;
        int bestArea = 0;

        for (int v = 0; v < SIZE; v++) {
            for (int u = 0; u < SIZE; u++)
                heights[u] = filled[v][u] ? heights[u] + 1 : 0;

            int[] stack = new int[SIZE + 1];
            int stackSize = 0;

            for (int u = 0; u <= SIZE; u++) {
                int height = u == SIZE
                        ? 0
                        : heights[u];

                while(
                        stackSize > 0 &&
                        heights[stack[stackSize - 1]] > height
                ) {
                    int rectangleHeight = heights[stack[--stackSize]];
                    int left = stackSize == 0
                            ? 0
                            : stack[stackSize - 1] + 1;
                    int width = u - left;
                    int area = width * rectangleHeight;
                    if (area <= bestArea)
                        continue;

                    bestArea = area;

                    best = new Rectangle(
                            left,
                            v - rectangleHeight + 1,
                            width,
                            rectangleHeight
                    );
                }

                if (u < SIZE)
                    stack[stackSize++] = u;
            }
        }

        return best;
    }

    private static boolean isFilled(
            boolean[][][] voxels,
            Direction.Axis axis,
            int layer,
            int u,
            int v
    ) {
        return switch (axis) {
            case X -> voxels[layer][u][v];
            case Y -> voxels[u][layer][v];
            case Z -> voxels[u][v][layer];
        };
    }

    private record Rectangle(
            int u,
            int v,
            int width,
            int height
    ) {}

    private record Cuboid(
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ
    ) {}

    private static final class ActiveCuboid {
        private final Direction.Axis axis;
        private final int layerStart;
        private final int u;
        private final int v;
        private final int width;
        private final int height;
        private int depth = 1;

        private ActiveCuboid(
                Direction.Axis axis,
                int layerStart,
                int u,
                int v,
                int width,
                int height
        ) {
            this.axis = axis;
            this.layerStart = layerStart;
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }

        private boolean matches(Rectangle rectangle) {
            return
                    rectangle.u == u &&
                    rectangle.v == v &&
                    rectangle.width == width &&
                    rectangle.height == height;
        }

        private Cuboid finish() {
            return switch (axis) {
                case X -> new Cuboid(
                        layerStart,
                        u,
                        v,
                        layerStart + depth,
                        u + width,
                        v + height
                );
                case Y -> new Cuboid(
                        u,
                        layerStart,
                        v,
                        u + width,
                        layerStart + depth,
                        v + height
                );
                case Z -> new Cuboid(
                        u,
                        v,
                        layerStart,
                        u + width,
                        v + height,
                        layerStart + depth
                );
            };
        }
    }
}
