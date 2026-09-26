package net.centertain.ceac.material.shapes.utility;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public final class MaterialShapeVoxelHelper {
    private static final int SIZE = 16;
    private static final double EPSILON = 1.0e-9;

    private MaterialShapeVoxelHelper() {}

    public static Map<Direction, VoxelShape[]> makeShapes(
            Supplier<BakedModel> source
    ) {
        return new LazyShapes(source);
    }

    private static Map<Direction, VoxelShape[]> makeShapes(
            BakedModel source
    ) {
        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);

        for (Direction facing : Direction.values()) {
            VoxelShape[] rotations = new VoxelShape[4];
            for (int rotation = 0; rotation < 4; rotation++)
                rotations[rotation] = compact(rasterize(getTriangles(
                        source,
                        facing,
                        rotation
                )));

            shapes.put(facing, rotations);
        }
        return Map.copyOf(shapes);
    }

    private static List<Triangle> getTriangles(
            BakedModel source,
            Direction facing,
            int rotation
    ) {
        List<Triangle> triangles = new ArrayList<>();
        RandomSource random = RandomSource.create();

        addTriangles(
                triangles,
                source.getQuads(null, null, random, ModelData.EMPTY, null),
                facing,
                rotation
        );

        for (Direction side : Direction.values())
            addTriangles(
                    triangles,
                    source.getQuads(null, side, random, ModelData.EMPTY, null),
                    facing,
                    rotation
            );

        return triangles;
    }

    private static void addTriangles(
            List<Triangle> triangles,
            List<BakedQuad> quads,
            Direction facing,
            int rotation
    ) {
        VertexFormat format = DefaultVertexFormat.BLOCK;
        int stride = format.getIntegerSize();
        int positionOffset = format.getOffset(0) / Integer.BYTES;

        for (BakedQuad quad : quads) {
            int[] vertices = quad.getVertices();
            Vec3[] points = new Vec3[4];

            for (int i = 0; i < 4; i++) {
                int offset = i * stride + positionOffset;
                points[i] = transform(
                        new Vec3(
                                Float.intBitsToFloat(vertices[offset]),
                                Float.intBitsToFloat(vertices[offset + 1]),
                                Float.intBitsToFloat(vertices[offset + 2])
                        ),
                        facing,
                        rotation
                );
            }

            triangles.add(new Triangle(points[0], points[1], points[2]));
            if (!points[2].equals(points[3]))
                triangles.add(new Triangle(points[0], points[2], points[3]));
        }
    }

    private static Vec3 transform(
            Vec3 point,
            Direction facing,
            int rotation
    ) {
        Vec3 forward = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        Vec3 x = forward.scale(-1.0);
        Vec3 y = switch (facing) {
            case UP -> new Vec3(0.0, 0.0, -1.0);
            case DOWN -> new Vec3(0.0, 0.0, 1.0);
            default -> new Vec3(0.0, 1.0, 0.0);
        };

        for (int i = 0; i < rotation; i++)
            y = y.cross(forward).add(forward.scale(y.dot(forward)));

        Vec3 z = y.cross(forward).normalize();
        Vec3 local = point.subtract(0.5, 0.5, 0.5);

        return new Vec3(0.5, 0.5, 0.5)
                .add(x.scale(local.x))
                .add(y.scale(local.y))
                .add(z.scale(local.z));
    }

    private static boolean[][][] rasterize(List<Triangle> triangles) {
        boolean[][][] voxels = new boolean[SIZE][SIZE][SIZE];
        double radius = Math.sqrt(3.0) / (2.0 * SIZE) - EPSILON;
        double radiusSqr = radius * radius;

        for (Triangle triangle : triangles) {
            double minX = Math.min(triangle.a.x(), Math.min(triangle.b.x(), triangle.c.x()));
            double minY = Math.min(triangle.a.y(), Math.min(triangle.b.y(), triangle.c.y()));
            double minZ = Math.min(triangle.a.z(), Math.min(triangle.b.z(), triangle.c.z()));
            double maxX = Math.max(triangle.a.x(), Math.max(triangle.b.x(), triangle.c.x()));
            double maxY = Math.max(triangle.a.y(), Math.max(triangle.b.y(), triangle.c.y()));
            double maxZ = Math.max(triangle.a.z(), Math.max(triangle.b.z(), triangle.c.z()));

            int startX = Math.max(0, (int) Math.floor(minX * SIZE) - 1);
            int endX = Math.min(SIZE - 1, (int) Math.ceil(maxX * SIZE));
            int startY = Math.max(0, (int) Math.floor(minY * SIZE) - 1);
            int endY = Math.min(SIZE - 1, (int) Math.ceil(maxY * SIZE));
            int startZ = Math.max(0, (int) Math.floor(minZ * SIZE) - 1);
            int endZ = Math.min(SIZE - 1, (int) Math.ceil(maxZ * SIZE));

            for (int x = startX; x <= endX; x++)
                for (int y = startY; y <= endY; y++)
                    for (int z = startZ; z <= endZ; z++) {
                        Vec3 center = new Vec3(
                                (x + 0.5) / SIZE,
                                (y + 0.5) / SIZE,
                                (z + 0.5) / SIZE
                        );

                        if (distanceSquared(center, triangle) < radiusSqr)
                            voxels[x][y][z] = true;
                    }
        }

        fillInterior(voxels);

        return voxels;
    }

    private static double distanceSquared(
            Vec3 point,
            Triangle triangle
    ) {
        Vec3 ab = triangle.b.subtract(triangle.a);
        Vec3 ac = triangle.c.subtract(triangle.a);
        Vec3 ap = point.subtract(triangle.a);

        double d1 = ab.dot(ap);
        double d2 = ac.dot(ap);
        if (d1 <= 0.0 && d2 <= 0.0)
            return point.distanceToSqr(triangle.a);

        Vec3 bp = point.subtract(triangle.b);
        double d3 = ab.dot(bp);
        double d4 = ac.dot(bp);
        if (d3 >= 0.0 && d4 <= d3)
            return point.distanceToSqr(triangle.b);

        double vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0 && d1 >= 0.0 && d3 <= 0.0) {
            double v = d1 / (d1 - d3);
            Vec3 projection = triangle.a.add(ab.scale(v));
            return point.distanceToSqr(projection);
        }

        Vec3 cp = point.subtract(triangle.c);
        double d5 = ab.dot(cp);
        double d6 = ac.dot(cp);
        if (d6 >= 0.0 && d5 <= d6)
            return point.distanceToSqr(triangle.c);

        double vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0 && d2 >= 0.0 && d6 <= 0.0) {
            double w = d2 / (d2 - d6);
            Vec3 projection = triangle.a.add(ac.scale(w));
            return point.distanceToSqr(projection);
        }

        double va = d3 * d6 - d5 * d4;
        if (va <= 0.0 && (d4 - d3) >= 0.0 && (d5 - d6) >= 0.0) {
            double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
            Vec3 projection = triangle.b.add(triangle.c.subtract(triangle.b).scale(w));
            return point.distanceToSqr(projection);
        }

        Vec3 normal = ab.cross(ac).normalize();
        double distance = point.subtract(triangle.a).dot(normal);

        return distance * distance;
    }

    private static void fillInterior(boolean[][][] voxels) {
        boolean[][][] outside = new boolean[SIZE + 2][SIZE + 2][ SIZE + 2];
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        outside[0][0][0] = true;
        queue.add(new BlockPos(0, 0, 0));

        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();

            for (Direction direction : Direction.values()) {
                int x = pos.getX() + direction.getStepX();
                int y = pos.getY() + direction.getStepY();
                int z = pos.getZ() + direction.getStepZ();

                if (
                        x < 0 || x > SIZE + 1 ||
                        y < 0 || y > SIZE + 1 ||
                        z < 0 || z > SIZE + 1
                )
                    continue;
                if (outside[x][y][z])
                    continue;
                if (
                        x > 0 && x <= SIZE &&
                        y > 0 && y <= SIZE &&
                        z > 0 && z <= SIZE &&
                        voxels[x - 1][y - 1][z - 1]
                )
                    continue;

                outside[x][y][z] = true;
                queue.add(new BlockPos(x, y, z));
            }
        }

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                for (int z = 0; z < SIZE; z++)
                    if (!outside[x + 1][y + 1][z + 1])
                        voxels[x][y][z] = true;
    }

    private static VoxelShape compact(boolean[][][] voxels) {
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
                } else
                    next.add(new ActiveCuboid(
                            axis,
                            layer,
                            rectangle.u,
                            rectangle.v,
                            rectangle.width,
                            rectangle.height
                    ));
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

    private record Triangle(
            Vec3 a,
            Vec3 b,
            Vec3 c
    ) {}

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

    private static final class LazyShapes extends AbstractMap<Direction, VoxelShape[]> {
        private final Supplier<BakedModel> source;
        private volatile Map<Direction, VoxelShape[]> shapes;

        private LazyShapes(Supplier<BakedModel> source) {
            this.source = source;
        }

        private Map<Direction, VoxelShape[]> getShapes() {
            Map<Direction, VoxelShape[]> result = shapes;
            if (result != null)
                return result;

            synchronized (this) {
                result = shapes;
                if (result == null) {
                    BakedModel model = source.get();
                    if (model == null)
                        throw new IllegalStateException("Collision model has not baked yet");
                    result = makeShapes(model);
                    shapes = result;
                }
            }

            return result;
        }

        @Override
        public VoxelShape[] get(Object key) {
            return getShapes().get(key);
        }

        @Override
        public @NotNull Set<Entry<Direction, VoxelShape[]>> entrySet() {
            return getShapes().entrySet();
        }
    }
}
