package net.centertain.ceac.material.shapes.utility;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public final class MaterialShapeVoxelHelper24Way {
    private static final int SIZE = 16;
    private static final double EPSILON = 1.0e-9;

    private MaterialShapeVoxelHelper24Way() {}

    public static Map<Direction, VoxelShape[]> makeShapes(
            Supplier<BakedModel> source
    ) {
        return new LazyShapes(source);
    }

    private static Map<Direction, VoxelShape[]> makeShapes(
            BakedModel source
    ) {
        Map<Direction, VoxelShape[]> shapes = new EnumMap<>(Direction.class);
        boolean[][][] canonical = rasterize(getTriangles(source));

        for (Direction facing : Direction.values()) {
            VoxelShape[] rotations = new VoxelShape[4];
            for (int rotation = 0; rotation < 4; rotation++)
                rotations[rotation] = compact(transformVoxels(
                        canonical,
                        facing,
                        rotation
                ));

            shapes.put(facing, rotations);
        }
        return Map.copyOf(shapes);
    }

    private static List<Triangle> getTriangles(
            BakedModel source
    ) {
        List<Triangle> triangles = new ArrayList<>();
        RandomSource random = RandomSource.create();

        addTriangles(
                triangles,
                source.getQuads(null, null, random, ModelData.EMPTY, null)
        );

        for (Direction side : Direction.values())
            addTriangles(
                    triangles,
                    source.getQuads(null, side, random, ModelData.EMPTY, null)
            );

        return triangles;
    }

    private static void addTriangles(
            List<Triangle> triangles,
            List<BakedQuad> quads
    ) {
        VertexFormat format = DefaultVertexFormat.BLOCK;
        int stride = format.getIntegerSize();
        int positionOffset = format.getOffset(0) / Integer.BYTES;

        for (BakedQuad quad : quads) {
            int[] vertices = quad.getVertices();
            Vec3[] points = new Vec3[4];

            for (int i = 0; i < 4; i++) {
                int offset = i * stride + positionOffset;
                points[i] = new Vec3(
                        Float.intBitsToFloat(vertices[offset]),
                        Float.intBitsToFloat(vertices[offset + 1]),
                        Float.intBitsToFloat(vertices[offset + 2])
                );
            }

            triangles.add(new Triangle(points[0], points[1], points[2]));
            if (!points[2].equals(points[3]))
                triangles.add(new Triangle(points[0], points[2], points[3]));
        }
    }

    private static boolean[][][] transformVoxels(
            boolean[][][] voxels,
            Direction facing,
            int rotation
    ) {
        boolean[][][] transformed = new boolean[SIZE][SIZE][SIZE];

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

        int xx = (int) Math.round(x.x);
        int xy = (int) Math.round(x.y);
        int xz = (int) Math.round(x.z);
        int yx = (int) Math.round(y.x);
        int yy = (int) Math.round(y.y);
        int yz = (int) Math.round(y.z);
        int zx = (int) Math.round(z.x);
        int zy = (int) Math.round(z.y);
        int zz = (int) Math.round(z.z);

        for (int localX = 0; localX < SIZE; localX++)
            for (int localY = 0; localY < SIZE; localY++)
                for (int localZ = 0; localZ < SIZE; localZ++) {
                    if (!voxels[localX][localY][localZ])
                        continue;

                    int centerX = localX * 2 - (SIZE - 1);
                    int centerY = localY * 2 - (SIZE - 1);
                    int centerZ = localZ * 2 - (SIZE - 1);

                    int worldX = xx * centerX + yx * centerY + zx * centerZ;
                    int worldY = xy * centerX + yy * centerY + zy * centerZ;
                    int worldZ = xz * centerX + yz * centerY + zz * centerZ;

                    int targetX = (worldX + SIZE - 1) / 2;
                    int targetY = (worldY + SIZE - 1) / 2;
                    int targetZ = (worldZ + SIZE - 1) / 2;

                    transformed[targetX][targetY][targetZ] = true;
                }

        return transformed;
    }

    private static boolean[][][] rasterize(List<Triangle> triangles) {
        boolean[][][] voxels = new boolean[SIZE][SIZE][SIZE];

        for (Triangle triangle : triangles) {
            double minX = Math.min(triangle.a.x(), Math.min(triangle.b.x(), triangle.c.x()));
            double minY = Math.min(triangle.a.y(), Math.min(triangle.b.y(), triangle.c.y()));
            double minZ = Math.min(triangle.a.z(), Math.min(triangle.b.z(), triangle.c.z()));
            double maxX = Math.max(triangle.a.x(), Math.max(triangle.b.x(), triangle.c.x()));
            double maxY = Math.max(triangle.a.y(), Math.max(triangle.b.y(), triangle.c.y()));
            double maxZ = Math.max(triangle.a.z(), Math.max(triangle.b.z(), triangle.c.z()));

            int startX = Math.max(0, (int) Math.floor(minX * SIZE));
            int endX = Math.min(SIZE - 1, (int) Math.ceil(maxX * SIZE));
            int startY = Math.max(0, (int) Math.floor(minY * SIZE));
            int endY = Math.min(SIZE - 1, (int) Math.ceil(maxY * SIZE));
            int startZ = Math.max(0, (int) Math.floor(minZ * SIZE));
            int endZ = Math.min(SIZE - 1, (int) Math.ceil(maxZ * SIZE));

            for (int x = startX; x <= endX; x++)
                for (int y = startY; y <= endY; y++)
                    for (int z = startZ; z <= endZ; z++)
                        if (triangleIntersectsVoxel(triangle, x, y, z))
                            voxels[x][y][z] = true;
        }

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                for (int z = 0; z < SIZE; z++) {
                    if (voxels[x][y][z])
                        continue;

                    Vec3 center = new Vec3(
                            (x + 0.5) / SIZE,
                            (y + 0.5) / SIZE,
                            (z + 0.5) / SIZE
                    );

                    if (isInsideMesh(center, triangles))
                        voxels[x][y][z] = true;
                }

        return voxels;
    }

    private static boolean isInsideMesh(
            Vec3 point,
            List<Triangle> triangles
    ) {
        double solidAngle = 0.0;

        for (Triangle triangle : triangles) {
            Vec3 a = triangle.a().subtract(point);
            Vec3 b = triangle.b().subtract(point);
            Vec3 c = triangle.c().subtract(point);

            double lengthA = a.length();
            double lengthB = b.length();
            double lengthC = c.length();

            if (
                    lengthA < EPSILON ||
                            lengthB < EPSILON ||
                            lengthC < EPSILON
            )
                return true;

            double numerator = a.dot(b.cross(c));
            double denominator =
                    lengthA * lengthB * lengthC +
                            lengthA * b.dot(c) +
                            lengthB * c.dot(a) +
                            lengthC * a.dot(b);

            solidAngle += 2.0 * Math.atan2(numerator, denominator);
        }

        return Math.abs(solidAngle) > 2.0 * Math.PI;
    }

    private static boolean triangleIntersectsVoxel(
            Triangle triangle,
            int x,
            int y,
            int z
    ) {
        double minX = x / (double) SIZE;
        double minY = y / (double) SIZE;
        double minZ = z / (double) SIZE;
        double maxX = (x + 1) / (double) SIZE;
        double maxY = (y + 1) / (double) SIZE;
        double maxZ = (z + 1) / (double) SIZE;

        List<Vec3> polygon = List.of(triangle.a, triangle.b, triangle.c);

        polygon = clipPolygon(polygon, 0, minX, true);
        polygon = clipPolygon(polygon, 0, maxX, false);
        polygon = clipPolygon(polygon, 1, minY, true);
        polygon = clipPolygon(polygon, 1, maxY, false);
        polygon = clipPolygon(polygon, 2, minZ, true);
        polygon = clipPolygon(polygon, 2, maxZ, false);

        if (polygon.size() < 3)
            return false;

        Vec3 origin = polygon.get(0);
        double area = 0.0;

        for (int i = 1; i < polygon.size() - 1; i++)
            area += polygon.get(i)
                    .subtract(origin)
                    .cross(polygon.get(i + 1).subtract(origin))
                    .length() * 0.5;

        return area > EPSILON;
    }

    private static List<Vec3> clipPolygon(
            List<Vec3> polygon,
            int axis,
            double boundary,
            boolean keepGreater
    ) {
        if (polygon.isEmpty())
            return polygon;

        List<Vec3> clipped = new ArrayList<>();

        Vec3 previous = polygon.get(polygon.size() - 1);
        double previousValue = getAxis(previous, axis);
        boolean previousInside = keepGreater
                ? previousValue >= boundary - EPSILON
                : previousValue <= boundary + EPSILON;

        for (Vec3 current : polygon) {
            double currentValue = getAxis(current, axis);
            boolean currentInside = keepGreater
                    ? currentValue >= boundary - EPSILON
                    : currentValue <= boundary + EPSILON;

            if (currentInside != previousInside) {
                double denominator = currentValue - previousValue;
                if (Math.abs(denominator) > EPSILON) {
                    double t = (boundary - previousValue) / denominator;
                    clipped.add(previous.add(current.subtract(previous).scale(t)));
                }
            }

            if (currentInside)
                clipped.add(current);

            previous = current;
            previousValue = currentValue;
            previousInside = currentInside;
        }

        return clipped;
    }

    private static double getAxis(
            Vec3 point,
            int axis
    ) {
        return switch (axis) {
            case 0 -> point.x;
            case 1 -> point.y;
            case 2 -> point.z;
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
        };
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
