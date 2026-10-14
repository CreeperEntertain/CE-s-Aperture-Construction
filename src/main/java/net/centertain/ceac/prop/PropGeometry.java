package net.centertain.ceac.prop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class PropGeometry {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropGeometry.class);
    private static final double RAY_EPSILON = 1.0e-7;
    private static final Map<ResourceLocation, Optional<PropGeometry>> CACHE = new HashMap<>();

    private final Vec3 minBounds;
    private final Vec3 maxBounds;
    private final double boundsVolume;
    private final double volume;
    private final List<Triangle> triangles;

    private PropGeometry(List<Triangle> triangles) {
        this.triangles = List.copyOf(triangles);

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        double signedVolume = 0.0;

        for (Triangle triangle : triangles) {
            Vec3 a = triangle.a;
            Vec3 b = triangle.b;
            Vec3 c = triangle.c;
            signedVolume += a.dot(b.cross(c)) / 6.0;

            for (Vec3 vertex : new Vec3[]{a, b, c}) {
                minX = Math.min(minX, vertex.x);
                minY = Math.min(minY, vertex.y);
                minZ = Math.min(minZ, vertex.z);
                maxX = Math.max(maxX, vertex.x);
                maxY = Math.max(maxY, vertex.y);
                maxZ = Math.max(maxZ, vertex.z);
            }
        }

        this.minBounds = new Vec3(minX, minY, minZ);
        this.maxBounds = new Vec3(maxX, maxY, maxZ);
        this.boundsVolume = (maxX - minX) * (maxY - minY) * (maxZ * minZ);
        this.volume = Math.abs(signedVolume);
    }

    public record Triangle(
            Vec3 a,
            Vec3 b,
            Vec3 c
    ) {}

    public record RayHit(
            Vec3 location,
            Vec3 normal,
            double distance
    ) {}

    public static synchronized @Nullable PropGeometry get(ResourceManager resources, ResourceLocation modelId) {
        Optional<PropGeometry> cached = CACHE.get(modelId);
        if (cached != null)
            return cached.orElse(null);

        try {
            PropGeometry geometry = load(resources, modelId);
            CACHE.put(modelId, Optional.of(geometry));
            return geometry;
        } catch (IOException | RuntimeException exception) {
            CACHE.put(modelId, Optional.empty());
            LOGGER.error("Could nor load prop geometry for model {}", modelId, exception);
            return null;
        }
    }

    public static synchronized void clearCache() {
        CACHE.clear();
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public double getVolume() {
        return volume;
    }

    public Vec3 getBoundsCenter() {
        return minBounds.add(maxBounds).scale(0.5);
    }

    private static PropGeometry load(ResourceManager resources, ResourceLocation modelId) throws IOException {
        ResourceLocation jsonId = ResourceLocation.fromNamespaceAndPath(
                modelId.getNamespace(),
                "models/" + modelId.getPath() + ".json"
        );

        ResourceLocation objId;

        try (Reader reader = openReader(resources, jsonId)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (!json.has("loader") || !"forge:obj".equals(json.get("loader").getAsString()))
                throw new IOException("Only forge:obj models currently provide prop geometry: " + modelId);
            if (!json.has("model"))
                throw new IOException("Model JSON has no 'model' field");

            String modelReference = json.get("model").getAsString();
            String qualifiedReference = modelReference.contains(":")
                    ? modelReference
                    : modelId.getNamespace() + ":" + modelReference;
            objId = ResourceLocation.tryParse(qualifiedReference);
            if (objId == null || !objId.getPath().endsWith(".obj"))
                throw new IOException("Invalid OBJ model reference: " + modelReference);
        }

        List<Triangle> triangles;
        try (Reader reader = openReader(resources, objId)) {
            triangles = parseObj(reader);
        }
        if (triangles.isEmpty())
            throw new IOException("OBJ contains no faces: " + objId);

        return new PropGeometry(triangles);
    }

    private static Reader openReader(ResourceManager resources, ResourceLocation id) throws IOException {
        Optional<Resource> resource = resources.getResource(id);
        if (resource.isPresent())
            return new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8);

        String classpathPath = "/assets/" + id.getNamespace() + "/" + id.getPath();
        InputStream stream = PropGeometry.class.getResourceAsStream(classpathPath);
        if (stream == null)
            throw new FileNotFoundException("Resource not found: " + id);

        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    private static List<Triangle> parseObj(Reader source) throws IOException {
        List<Vec3> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(source)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                int commentIndex = line.indexOf('#');
                if (commentIndex >= 0)
                    line = line.substring(0, commentIndex);

                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] fields = line.split("\\s+");

                switch (fields[0]) {
                    case "v" -> {
                        if (fields.length < 4)
                            throw new IOException("Invalid vertex at OBJ line " + lineNumber);

                        double x = Double.parseDouble(fields[1]);
                        double y = Double.parseDouble(fields[2]);
                        double z = Double.parseDouble(fields[3]);
                        if (
                                !Double.isFinite(x) ||
                                !Double.isFinite(y) ||
                                !Double.isFinite(z)
                        )
                            throw new IOException("Non-finite vertex at OBJ line " + lineNumber);

                        vertices.add(new Vec3(x, y, z));
                    } case "f" -> {
                        if (fields.length < 4)
                            throw new IOException("Invalid face at OBJ line " + lineNumber);

                        List<Vec3> faceVertices = new ArrayList<>();

                        for (int i = 1; i < fields.length; i++) {
                            String vertexField = fields[i];
                            String indexField = vertexField.split("/", -1)[0];
                            int objIndex = Integer.parseInt(indexField);
                            if (objIndex == 0)
                                throw new IOException("OBJ vertex indices cannot be zero");

                            int index = objIndex > 0
                                    ? objIndex - 1
                                    : vertices.size() + objIndex;
                            if (index < 0 || index > vertices.size())
                                throw new IOException("Invalid vertex index at OBJ line " + lineNumber);

                            faceVertices.add(vertices.get(index));
                        }

                        Vec3 first = faceVertices.get(0);

                        for (int i = 1; i < faceVertices.size() - 1; i++)
                            triangles.add(new Triangle(first, faceVertices.get(i), faceVertices.get(i + 1)));
                    } default -> {} // Skip vt, vn, o, g, s, usemtl, mtllib, etc.
                }
            }
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid numeric data in OBJ", exception);
        }

        return triangles;
    }

    public @Nullable Vec3 samplePointInside(RandomSource random) {
        if (volume <= 1.0e-12 || boundsVolume <= 1.0e-12)
            return null;

        int maxAttempts = (int) Math.max(128.0, Math.min(100_000.0, Math.ceil(boundsVolume / volume * 12.0)));
        Vec3 rayDirection = new Vec3( // Random axis non-aligned values from hell
                1.0,
                0.3713906763541037,
                0.6947465906068658
        ).normalize();
        double width = maxBounds.x - minBounds.x;
        double height = maxBounds.y - minBounds.y;
        double depth = maxBounds.z - minBounds.z;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Vec3 point = new Vec3(
                    minBounds.x + random.nextDouble() * width,
                    minBounds.y + random.nextDouble() * height,
                    minBounds.z + random.nextDouble() * depth
            );
            if (containsPoint(point, rayDirection))
                return point;
        }

        return null;
    }

    private boolean containsPoint(Vec3 point, Vec3 rayDirection) {
        int intersections = 0;
        for (Triangle triangle : triangles)
            if (intersectTriangle(
                    point,
                    rayDirection,
                    triangle.a,
                    triangle.b,
                    triangle.c,
                    Double.POSITIVE_INFINITY
            ) != null)
                intersections++;
        return (intersections & 1) != 0;
    }

    public @Nullable RayHit raycast(
            Vec3 origin,
            Vec3 direction,
            Matrix4f transform,
            double maxDistance
    ) {
        if (maxDistance <= 0.0 || direction.lengthSqr() < RAY_EPSILON)
            return null;

        Vec3 rayDirection = direction.normalize();
        double closestDistance = maxDistance;
        RayHit closestHit = null;

        for (Triangle triangle : triangles) {
            Vec3 a = transformPoint(triangle.a(), transform);
            Vec3 b = transformPoint(triangle.b(), transform);
            Vec3 c = transformPoint(triangle.c(), transform);

            Intersection intersection = intersectTriangle(
                    origin,
                    rayDirection,
                    a, b, c,
                    closestDistance
            );
            if (intersection == null)
                continue;

            closestDistance = intersection.distance;
            closestHit = new RayHit(
                    origin.add(rayDirection.scale(closestDistance)),
                    intersection.normal,
                    closestDistance
            );
        }

        return closestHit;
    }

    @Contract("_, _ -> new")
    private static @NotNull Vec3 transformPoint(@NotNull Vec3 point, @NotNull Matrix4f transform) {
        Vector3f transformed = new Vector3f(
                (float) point.x,
                (float) point.y,
                (float) point.z
        );
        transformed.mulPosition(transform);
        return new Vec3(
                transformed.x,
                transformed.y,
                transformed.z
        );
    }

    private static @Nullable Intersection intersectTriangle(
            @NotNull Vec3 origin,
            @NotNull Vec3 direction,
            @NotNull Vec3 a,
            @NotNull Vec3 b,
            @NotNull Vec3 c,
            double maxDistance
    ) {
        Vec3 edge1 = b.subtract(a);
        Vec3 edge2 = c.subtract(a);

        Vec3 p = direction.cross(edge2);
        double determinant = edge1.dot(p);
        if (Math.abs(determinant) < RAY_EPSILON)
            return null;

        double inverseDeterminant = 1.0 / determinant;
        Vec3 fromA = origin.subtract(a);
        double u = fromA.dot(p) * inverseDeterminant;
        if (u < 0.0 || u > 1.0)
            return null;

        Vec3 q = fromA.cross(edge1);
        double v = direction.dot(q) * inverseDeterminant;
        if (v < 0.0 || u + v > 1.0)
            return null;

        double distance = edge2.dot(q) * inverseDeterminant;
        if (distance < 0.0 || distance >= maxDistance)
            return null;

        Vec3 normal = edge1.cross(edge2);
        if (normal.lengthSqr() < RAY_EPSILON)
            return null;

        return new Intersection(distance, normal);
    }

    private record Intersection(
            double distance,
            Vec3 normal
    ) {}
}
