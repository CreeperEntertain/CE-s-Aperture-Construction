package net.centertain.ceac.prop.framework.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.centertain.ceac.prop.framework.PropGeometry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.overlayng.OverlayNG;
import org.locationtech.jts.operation.overlayng.OverlayNGRobust;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.locationtech.jts.triangulate.polygon.PolygonTriangulator;

import java.util.*;

final class PropMeshSectionClipper {
    private static final double MIN_VOLUME = 1.0e-12;
    private static final double PLANE_EPSILON = 1.0e-6;
    private static final double INTERSECTION_EPSILON = 1.0e-10;
    private static final double INTERSECTION_EPSILON_SQUARED = INTERSECTION_EPSILON * INTERSECTION_EPSILON;
    private static final double PROBE_DISTANCE = 1.0e-4;
    private static final double MIN_SEGMENT_LENGTH = 1.0e-10;
    private static final double PROJECTION_GRID = 1.0e-5;
    private static final double RAY_EPSILON = 1.0e-9;
    private static final int BVH_LEAF_SIZE = 8;

    private static final Vec3[] INSIDE_RAY_DIRECTIONS = {
            new Vec3(1.0, 0.3713906763541037, 0.6947465906068658).normalize(),
            new Vec3(0.2137, 1.0, 0.4289).normalize(),
            new Vec3(0.5923, 0.1871, 1.0).normalize()
    };

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private static final Map<BakedModel, ModelCache> MODEL_CACHE = new WeakHashMap<>();

    private PropMeshSectionClipper() {}


    static Mesh getMesh(BakedModel model, long seed) {
        synchronized (MODEL_CACHE) {
            ModelCache cache = MODEL_CACHE.computeIfAbsent(model, ignored -> new ModelCache());
            Mesh cached = cache.bySeed.get(seed);
            if (cached != null)
                return cached;

            List<PropGeometry.Triangle> triangles = readModelTriangles(model, seed);
            long fingerprint = fingerprint(triangles);
            for (Mesh candidate : cache.variants.getOrDefault(fingerprint, List.of()))
                if (sameGeometry(triangles, candidate.triangles)) {
                    cache.bySeed.put(seed, candidate);
                    return candidate;
                }

            Mesh mesh = new Mesh(triangles);
            cache.variants.computeIfAbsent(fingerprint, ignored -> new ArrayList<>()).add(mesh);
            cache.bySeed.put(seed, mesh);

            return mesh;
        }
    }

    static AABB worldBounds(Mesh mesh, Matrix4f transform) {
        AABB local = mesh.localBounds;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (int bits = 0; bits < 8; bits++) {
            Vec3 corner = new Vec3(
                    (bits & 1) == 0 ? local.minX : local.maxX,
                    (bits & 2) == 0 ? local.minY : local.maxY,
                    (bits & 4) == 0 ? local.minZ : local.maxZ
            );
            Vec3 p = transformPoint(corner, transform);

            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            minZ = Math.min(minZ, p.z);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
            maxZ = Math.max(maxZ, p.z);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(PLANE_EPSILON);
    }

    static void appendInside(
            PropGeometry.Triangle previewTriangle,
            Mesh mesh,
            Matrix4f propTransform,
            List<PropGeometry.Triangle> output
    ) {
        if (!mesh.hasVolume || mesh.root == null)
            return;
        try {
            appendInsideInterval(previewTriangle, mesh, propTransform, output);
        } catch (TopologyException ignored) {
            // Simply contributes nothing
        }
    }

    private static void appendInsideInterval(
            PropGeometry.Triangle preview,
            Mesh mesh,
            Matrix4f propTransform,
            List<PropGeometry.Triangle> output
    ) {
        Vec3 a = preview.a();
        Vec3 b = preview.b();
        Vec3 c = preview.c();

        Vec3 ab = b.subtract(a);
        Vec3 ac = c.subtract(a);
        Vec3 cross = ab.cross(ac);
        if (cross.lengthSqr() <= 1.0e-20)
            return;

        Vec3 normal = cross.normalize();
        Vec3 u = ab.normalize();
        Vec3 v = normal.cross(u).normalize();

        Coordinate ca = project(a, a, u, v);
        Coordinate cb = project(b, a, u, v);
        Coordinate cc = project(c, a, u, v);

        Polygon trianglePolygon = GEOMETRY_FACTORY.createPolygon(new Coordinate[] { ca, cb, cc, new Coordinate(ca) });
        if (trianglePolygon.getArea() <= 1.0e-14)
            return;

        List<Geometry> linework = new ArrayList<>();
        linework.add(trianglePolygon.getExteriorRing());
        AABB queryBounds = triangleBounds(preview).inflate(PLANE_EPSILON * 4.0);
        List<Entry> candidates = new ArrayList<>();
        query(mesh.root, queryBounds, candidates);

        for (Entry entry : candidates) {
            PlaneSection section = intersectTriangleWithPlane(entry.triangle, a, normal);
            if (section.coplanar) {
                PropGeometry.Triangle t = entry.triangle;
                appendClippedSegment(t.a(), t.b(), a, u, v, trianglePolygon, linework);
                appendClippedSegment(t.b(), t.c(), a, u, v, trianglePolygon, linework);
                appendClippedSegment(t.c(), t.a(), a, u, v, trianglePolygon, linework);
                continue;
            }

            List<Vec3> points = section.points;
            if (points.size() < 2)
                continue;

            Vec3 first = points.get(0);
            Vec3 second = points.get(1);
            double longest = first.distanceToSqr(second);

            for (int i = 0; i < points.size(); i++)
                for (Vec3 point : points) {
                    double distance = points.get(i).distanceToSqr(point);
                    if (distance > longest) {
                        longest = distance;
                        first = points.get(i);
                        second = point;
                    }
                }
            if (longest <= INTERSECTION_EPSILON_SQUARED)
                continue;

            appendClippedSegment(first, second, a, u, v, trianglePolygon, linework);
        }

        Geometry noded = UnaryUnionOp.union(linework);

        Polygonizer polygonizer = new Polygonizer(true);
        polygonizer.add(noded);

        Collection<?> faces = polygonizer.getPolygons();
        for (Object candidate : faces) {
            if (!(candidate instanceof Polygon face))
                continue;

            Coordinate interior = face.getInteriorPoint().getCoordinate();
            Vec3 sample = lift(a, u, v, interior.x, interior.y);
            if (!containsNearPlane(mesh, sample, normal))
                continue;

            Geometry triangulated = PolygonTriangulator.triangulate(face);
            for (int i = 0; i < triangulated.getNumGeometries(); i++) {
                Geometry part = triangulated.getGeometryN(i);
                if (!(part instanceof  Polygon trianglePolygonPart))
                    continue;

                Coordinate[] coordinates = trianglePolygonPart.getExteriorRing().getCoordinates();
                if (coordinates.length < 4)
                    continue;

                Vec3 localA = lift(a, u, v, coordinates[0].x, coordinates[0].y);
                Vec3 localB = lift(a, u, v, coordinates[1].x, coordinates[1].y);
                Vec3 localC = lift(a, u, v, coordinates[2].x, coordinates[2].y);

                Vec3 worldA = transformPoint(localA, propTransform);
                Vec3 worldB = transformPoint(localB, propTransform);
                Vec3 worldC = transformPoint(localC, propTransform);
                if (worldB.subtract(worldA).cross(worldC.subtract(worldA)).lengthSqr() <= 1.0e-12)
                    continue;

                output.add(new PropGeometry.Triangle(worldA, worldB, worldC));
            }
        }
    }

    private static void appendClippedSegment(
            Vec3 a, Vec3 b,
            Vec3 origin,
            Vec3 u, Vec3 v,
            Polygon clipPolygon,
            List<Geometry> linework
    ) {
        Coordinate ca = project(a, origin, u, v);
        Coordinate cb = project(b, origin, u, v);
        if (ca.equals2D(cb))
            return;

        LineString segment = GEOMETRY_FACTORY.createLineString(new Coordinate[] { ca, cb });
        Geometry clipped = OverlayNGRobust.overlay(segment, clipPolygon, OverlayNG.INTERSECTION);
        collectLineStrings(clipped, linework);
    }

    private static void collectLineStrings(Geometry geometry, List<Geometry> output) {
        if (geometry instanceof LineString line) {
            if (line.getNumPoints() >= 2 && line.getLength() > MIN_SEGMENT_LENGTH)
                output.add(line);
            return;
        }
        if (geometry instanceof GeometryCollection collection)
            for (int i = 0; i < collection.getNumGeometries(); i++)
                collectLineStrings(collection.getGeometryN(i), output);
    }

    private static PlaneSection intersectTriangleWithPlane(PropGeometry.Triangle triangle, Vec3 planePoint, Vec3 normal) {
        Vec3[] vertices = {triangle.a(), triangle.b(), triangle.c()};
        double[] distances = new double[3];
        for (int i = 0; i < 3; i++)
            distances[i] = vertices[i].subtract(planePoint).dot(normal);
        if (
                Math.abs(distances[0]) <= PLANE_EPSILON &&
                Math.abs(distances[1]) <= PLANE_EPSILON &&
                Math.abs(distances[2]) <= PLANE_EPSILON
        )
            return new PlaneSection(List.of(), true);

        List<Vec3> intersections = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            int j = (i + 1) % 3;
            double di = distances[i];
            double dj = distances[j];
            if (Math.abs(di) <= PLANE_EPSILON)
                addUnique(intersections, projectOntoPlane(vertices[i], planePoint, normal));
            if (
                    (di < -PLANE_EPSILON && dj > PLANE_EPSILON) ||
                    (di > PLANE_EPSILON && dj < -PLANE_EPSILON)
            ) {
                double amount = di / (di - dj);
                Vec3 intersection = vertices[i].lerp(vertices[j], amount);
                addUnique(intersections, projectOntoPlane(intersection, planePoint, normal));
            }
        }

        return new PlaneSection(List.copyOf(intersections), false);
    }

    private static void addUnique(List<Vec3> points, Vec3 point) {
        for (Vec3 existing : points)
            if (existing.distanceToSqr(point) <= INTERSECTION_EPSILON_SQUARED)
                return;
        points.add(point);
    }

    private static Vec3 projectOntoPlane(Vec3 point, Vec3 origin, Vec3 normal) {
        double distance = point.subtract(origin).dot(normal);
        return point.subtract(normal.scale(distance));
    }

    private static Coordinate project(Vec3 point, Vec3 origin, Vec3 u, Vec3 v) {
        Vec3 relative = point.subtract(origin);
        return new Coordinate(snap(relative.dot(u)), snap(relative.dot(v)));
    }

    private static double snap(double value) {
        return Math.rint(value / PROJECTION_GRID) * PROJECTION_GRID;
    }

    private static Vec3 lift(
            Vec3 origin,
            Vec3 u, Vec3 v,
            double x, double y
    ) {
        return origin.add(u.scale(x)).add(v.scale(y));
    }

    private static boolean containsNearPlane(
            Mesh mesh,
            Vec3 point,
            Vec3 normal
    ) {
        if (containsAtDistance(mesh, point, normal, PROBE_DISTANCE))
            return true;
        if (containsAtDistance(mesh, point, normal, PLANE_EPSILON * 16.0))
            return true;
        return containsAtDistance(mesh, point, normal, PLANE_EPSILON * 4.0);
    }

    private static boolean containsAtDistance(
            Mesh mesh,
            Vec3 point,
            Vec3 normal,
            double distance
    ) {
        Vec3 offset = normal.scale(distance);
        return containsPoint(mesh, point.add(offset)) || containsPoint(mesh, point.subtract(offset));
    }

    private static boolean containsPoint(Mesh mesh, Vec3 point) {
        if (mesh.root == null)
            return false;

        double solidAngle = 0.0;

        for (PropGeometry.Triangle triangle : mesh.triangles) {
            Vec3 a = triangle.a().subtract(point);
            Vec3 b = triangle.b().subtract(point);
            Vec3 c = triangle.c().subtract(point);

            double lengthA = a.length();
            double lengthB = b.length();
            double lengthC = c.length();

            if (lengthA < 1.0e-12 || lengthB < 1.0e-12 || lengthC < 1.0e-12)
                return true;

            double numerator = a.dot(b.cross(c));
            double denominator = lengthA * lengthB * lengthC + a.dot(b) * lengthC + b.dot(c) * lengthA + c.dot(a) * lengthB;

            solidAngle += 2.0 * Math.atan2(numerator, denominator);
        }

        return Math.abs(solidAngle) > 2.0 * Math.PI;
    }

    private static void collectRayHits(
            Node node,
            Vec3 origin,
            Vec3 direction,
            List<Double> hits
    ) {
        if (!rayIntersectsBounds(origin, direction, node.bounds))
            return;
        if (node.entries != null) {
            for (Entry entry : node.entries) {
                PropGeometry.Triangle t = entry.triangle;
                double distance = rayTriangleDistance(origin, direction, t.a(), t.b(), t.c());
                if (Double.isFinite(distance))
                    hits.add(distance);
            }
            return;
        }
        if (node.left != null)
            collectRayHits(node.left, origin, direction, hits);
        if (node.right != null)
            collectRayHits(node.right, origin, direction, hits);
    }

    private static double rayTriangleDistance(
            Vec3 origin, Vec3 direction,
            Vec3 a, Vec3 b, Vec3 c
    ) {
        Vec3 edge1 = b.subtract(a);
        Vec3 edge2 = c.subtract(a);
        Vec3 p = direction.cross(edge2);

        double determinant = edge1.dot(p);
        if (Math.abs(determinant) < RAY_EPSILON)
            return Double.NaN;

        double inverseDeterminant = 1.0 / determinant;
        Vec3 fromA = origin.subtract(a);

        double u = fromA.dot(p) * inverseDeterminant;
        if (u < -RAY_EPSILON || u > 1.0 + RAY_EPSILON)
            return Double.NaN;

        Vec3 q = fromA.cross(edge1);
        double v = direction.dot(q) * inverseDeterminant;
        if (v < -RAY_EPSILON || u + v > 1.0 + RAY_EPSILON)
            return Double.NaN;

        double distance = edge2.dot(q) * inverseDeterminant;
        return distance > RAY_EPSILON ? distance : Double.NaN;
    }

    private static boolean rayIntersectsBounds(Vec3 origin, Vec3 direction, AABB bounds) {
        double tMin = 0.0;
        double tMax = Double.POSITIVE_INFINITY;

        for (int axis = 0; axis < 3; axis++) {
            double o = component(origin, axis);
            double d = component(direction, axis);
            double min = minComponent(bounds, axis);
            double max = maxComponent(bounds, axis);
            if (Math.abs(d) < 1.0e-12) {
                if (o < min || o > max)
                    return false;
                continue;
            }

            double first = (min - o) / d;
            double second = (max - o) / d;
            if (first > second) {
                double swap = first;
                first = second;
                second = swap;
            }

            tMin = Math.max(tMin, first);
            tMax = Math.min(tMax, second);
            if (tMax < tMin)
                return false;
        }

        return tMax > RAY_EPSILON;
    }

    private static List<PropGeometry.Triangle> readModelTriangles(BakedModel model, long seed) {
        List<PropGeometry.Triangle> triangles = new ArrayList<>();

        BlockState state = Blocks.AIR.defaultBlockState();
        RandomSource random = RandomSource.create();
        for (Direction face : Direction.values()) {
            random.setSeed(seed);
            appendQuads(model.getQuads(state, face, random, ModelData.EMPTY, RenderType.solid()), triangles);
        }

        random.setSeed(seed);
        appendQuads(model.getQuads(state, null, random, ModelData.EMPTY, RenderType.solid()), triangles);

        return List.copyOf(triangles);
    }

    private static void appendQuads(
            List<BakedQuad> quads,
            List<PropGeometry.Triangle> output
    ) {
        int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
        int positionOffset = DefaultVertexFormat.BLOCK.getOffset(0) / Integer.BYTES;

        for (BakedQuad quad : quads) {
            int[] data = quad.getVertices();
            Vec3[] vertices = new Vec3[4];
            for (int i = 0; i < 4; i++) {
                int index = i * stride + positionOffset;
                vertices[i] = new Vec3(
                        Float.intBitsToFloat(data[index]),
                        Float.intBitsToFloat(data[index + 1]),
                        Float.intBitsToFloat(data[index + 2])
                );
            }

            output.add(new PropGeometry.Triangle(vertices[0], vertices[1], vertices[2]));
            output.add(new PropGeometry.Triangle(vertices[0], vertices[2], vertices[3]));
        }
    }

    private static long fingerprint(List<PropGeometry.Triangle> triangles) {
        long hash = 0xcbf29ce484222325L;

        for (PropGeometry.Triangle triangle : triangles)
            for (Vec3 point : List.of(triangle.a(), triangle.b(), triangle.c())) {
                hash = mix(hash, point.x);
                hash = mix(hash, point.y);
                hash = mix(hash, point.z);
            }

        return hash;
    }

    private static long mix(long hash, double value) {
        hash ^= Double.doubleToLongBits(value);
        return hash * 0x100000001b3L;
    }

    private static boolean sameGeometry(List<PropGeometry.Triangle> a, List<PropGeometry.Triangle> b) {
        if (a.size() != b.size())
            return false;

        for (int i = 0; i < a.size(); i++) {
            PropGeometry.Triangle ta = a.get(i);
            PropGeometry.Triangle tb = b.get(i);
            if (
                    !samePoint(ta.a(), tb.a()) ||
                    !samePoint(ta.b(), tb.b()) ||
                    !samePoint(ta.c(), tb.c())
            )
                return false;
        }

        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean samePoint(Vec3 a, Vec3 b) {
        return
                Double.doubleToLongBits(a.x) == Double.doubleToLongBits(b.x) &&
                Double.doubleToLongBits(a.y) == Double.doubleToLongBits(b.y) &&
                Double.doubleToLongBits(a.z) == Double.doubleToLongBits(b.z);
    }

    private static AABB triangleBounds(PropGeometry.Triangle triangle) {
        Vec3 a = triangle.a();
        Vec3 b = triangle.b();
        Vec3 c = triangle.c();

        return new AABB(
                Math.min(a.x, Math.min(b.x, c.x)),
                Math.min(a.y, Math.min(b.y, c.y)),
                Math.min(a.z, Math.min(b.z, c.z)),
                Math.max(a.x, Math.max(b.x, c.x)),
                Math.max(a.y, Math.max(b.y, c.y)),
                Math.max(a.z, Math.max(b.z, c.z))
        );
    }

    private static AABB boundsOf(List<PropGeometry.Triangle> triangles) {
        if (triangles.isEmpty())
            return new AABB(0, 0, 0, 0, 0, 0);

        AABB bounds = null;
        for (PropGeometry.Triangle triangle : triangles) {
            AABB triangleBounds = triangleBounds(triangle);
            bounds = bounds == null
                    ? triangleBounds
                    : union(bounds, triangleBounds);
        }

        return bounds;
    }

    private static AABB union(AABB a, AABB b) {
        return new AABB(
                Math.min(a.minX, b.minX),
                Math.min(a.minY, b.minY),
                Math.min(a.minZ, b.minZ),
                Math.max(a.maxX, b.maxX),
                Math.max(a.maxY, b.maxY),
                Math.max(a.maxZ, b.maxZ)
        );
    }

    private static Node buildNode(List<Entry> entries) {
        AABB bounds = entries.get(0).bounds;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Entry entry : entries) {
            bounds = union(bounds, entry.bounds);

            minX = Math.min(minX, entry.centroid.x);
            minY = Math.min(minY, entry.centroid.y);
            minZ = Math.min(minZ, entry.centroid.z);
            maxX = Math.max(maxX, entry.centroid.x);
            maxY = Math.max(maxY, entry.centroid.y);
            maxZ = Math.max(maxZ, entry.centroid.z);
        }
        if (entries.size() <= BVH_LEAF_SIZE)
            return new Node(bounds, List.copyOf(entries), null, null);

        double extentX = maxX - minX;
        double extentY = maxY - minY;
        double extentZ = maxZ - minZ;

        final int axis = extentX >= extentY && extentX >= extentZ ? 0 : extentY >= extentZ ? 1 : 2;

        entries.sort(Comparator.comparingDouble((Entry entry) -> component(entry.centroid, axis)));

        int middle = entries.size() / 2;
        Node left = buildNode(new ArrayList<>(entries.subList(0, middle)));
        Node right = buildNode(new ArrayList<>(entries.subList(middle, entries.size())));

        return new Node(bounds, null, left, right);
    }

    private static void query(Node node, AABB bounds, List<Entry> output) {
        if (node == null || !node.bounds.intersects(bounds))
            return;
        if (node.entries != null) {
            for (Entry entry : node.entries)
                if (entry.bounds.intersects(bounds))
                    output.add(entry);
            return;
        }

        query(node.left, bounds, output);
        query(node.right, bounds, output);
    }

    private static double component(Vec3 v, int axis) {
        return switch (axis) {
            case 0 -> v.x;
            case 1 -> v.y;
            default -> v.z;
        };
    }

    private static double minComponent(AABB b, int axis) {
        return switch (axis) {
            case 0 -> b.minX;
            case 1 -> b.minY;
            default -> b.minZ;
        };
    }
    private static double maxComponent(AABB b, int axis) {
        return switch (axis) {
            case 0 -> b.maxX;
            case 1 -> b.maxY;
            default -> b.maxZ;
        };
    }

    static PropGeometry.Triangle transformTriangleToLocal(
            PropGeometry.Triangle triangle,
            Matrix4f localToWorld
    ) {
        return new PropGeometry.Triangle(
                inverseTransformPoint(triangle.a(), localToWorld),
                inverseTransformPoint(triangle.b(), localToWorld),
                inverseTransformPoint(triangle.c(), localToWorld),
                triangle.smoothEdgeMask()
        );
    }

    private static Vec3 transformPoint(Vec3 point, Matrix4f transform) {
        return new Vec3(
                transform.m00() * point.x + transform.m10() * point.y + transform.m20() * point.z + transform.m30(),
                transform.m01() * point.x + transform.m11() * point.y + transform.m21() * point.z + transform.m31(),
                transform.m02() * point.x + transform.m12() * point.y + transform.m22() * point.z + transform.m32()
        );
    }

    private static Vec3 inverseTransformPoint(Vec3 point, Matrix4f transform) {
        double a00 = transform.m00();
        double a01 = transform.m10();
        double a02 = transform.m20();

        double a10 = transform.m01();
        double a11 = transform.m11();
        double a12 = transform.m21();

        double a20 = transform.m02();
        double a21 = transform.m12();
        double a22 = transform.m22();

        double x = point.x - transform.m30();
        double y = point.y - transform.m31();
        double z = point.z - transform.m32();

        double c00 = a11 * a22 - a12 * a21;
        double c01 = a02 * a21 - a01 * a22;
        double c02 = a01 * a12 - a02 * a11;

        double c10 = a12 * a20 - a10 * a22;
        double c11 = a00 * a22 - a02 * a20;
        double c12 = a02 * a10 - a00 * a12;

        double c20 = a10 * a21 - a11 * a20;
        double c21 = a01 * a20 - a00 * a21;
        double c22 = a00 * a11 - a01 * a10;

        double determinant = a00 * c00 + a01 * c10 + a02 * c20;

        if (Math.abs(determinant) < 1.0e-12)
            throw new IllegalArgumentException("Singular prop transform");

        double inverseDeterminant = 1.0 / determinant;

        return new Vec3(
                (c00 * x + c01 * y + c02 * z) * inverseDeterminant,
                (c10 * x + c11 * y + c12 * z) * inverseDeterminant,
                (c20 * x + c21 * y + c22 * z) * inverseDeterminant
        );
    }


    private record PlaneSection(List<Vec3> points, boolean coplanar) {}
    private record Entry(PropGeometry.Triangle triangle, AABB bounds, Vec3 centroid) {}
    private record Node(AABB bounds, List<Entry> entries, Node left, Node right) {}

    private static final class ModelCache {
        final Map<Long, Mesh> bySeed = new HashMap<>();
        final Map<Long, List<Mesh>> variants = new HashMap<>();
    }

    static final class Mesh {
        private final List<PropGeometry.Triangle> triangles;
        private final AABB localBounds;
        private final Node root;
        private final boolean hasVolume;

        private Mesh(List<PropGeometry.Triangle> triangles) {
            this.triangles = List.copyOf(triangles);
            this.localBounds = boundsOf(this.triangles);

            double signedVolume = 0.0;
            List<Entry> entries = new ArrayList<>(triangles.size());

            for (PropGeometry.Triangle triangle : this.triangles) {
                signedVolume += triangle.a().dot(triangle.b().cross(triangle.c()));

                AABB bounds = triangleBounds(triangle);
                Vec3 centroid = new Vec3(
                        (bounds.minX + bounds.maxX) * 0.5,
                        (bounds.minY + bounds.maxY) * 0.5,
                        (bounds.minZ + bounds.maxZ) * 0.5
                );

                entries.add(new Entry(triangle, bounds, centroid));
            }

            this.hasVolume = Math.abs(signedVolume) > MIN_VOLUME;
            this.root = entries.isEmpty() ? null : buildNode(entries);
        }

        AABB localBounds() {
            return localBounds;
        }
        boolean hasVolume() {
            return hasVolume;
        }
    }
}
