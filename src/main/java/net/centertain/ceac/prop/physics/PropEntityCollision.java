package net.centertain.ceac.prop.physics;

import net.centertain.ceac.prop.PropGeometry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class PropEntityCollision {
    private static final double COLLISION_SKIN = 1.0e-3;

    public record PropContact(UUID propId, Vec3 normal, double hitTime) {}
    public record MovementResult(Vec3 movement, List<PropContact> contacts) {}

    private PropEntityCollision() {}

    public static List<PropGeometry.Triangle> transformTriangles(PropGeometry geometry, Matrix4f transform) {
        List<PropGeometry.Triangle> transformed = new ArrayList<>(geometry.getTriangles().size());

        for (PropGeometry.Triangle triangle : geometry.getTriangles())
            transformed.add(new PropGeometry.Triangle(
                    transformPoint(triangle.a(), transform),
                    transformPoint(triangle.b(), transform),
                    transformPoint(triangle.c(), transform),
                    triangle.smoothEdgeMask()
            ));

        return List.copyOf(transformed);
    }

    public static AABB boundsOf(Collection<PropGeometry.Triangle> triangles) {
        if (triangles.isEmpty())
            return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;

        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (PropGeometry.Triangle triangle : triangles) {
            Vec3 a = triangle.a();
            Vec3 b = triangle.b();
            Vec3 c = triangle.c();

            minX = Math.min(minX, Math.min(a.x, Math.min(b.x, c.x)));
            minY = Math.min(minY, Math.min(a.y, Math.min(b.y, c.y)));
            minZ = Math.min(minZ, Math.min(a.z, Math.min(b.z, c.z)));

            maxX = Math.max(maxX, Math.max(a.x, Math.max(b.x, c.x)));
            maxY = Math.max(maxY, Math.max(a.y, Math.max(b.y, c.y)));
            maxZ = Math.max(maxZ, Math.max(a.z, Math.max(b.z, c.z)));
        }


        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static Vec3 clipMovementAgainstTriangles(
            AABB entityBox,
            Vec3 movement,
            Collection<PropGeometry.Triangle> triangles
    ) {
        if (triangles.isEmpty() || movement.lengthSqr() < 1.0e-12)
            return movement;

        double dy = clipAxisMovement(entityBox, movement.y, 1, triangles);
        AABB afterY = entityBox.move(0.0, dy, 0.0);
        Vec3 horizontal = new Vec3(movement.x, 0.0, movement.z);
        Vec3 clippedHorizontal = clipHorizontalMovementAgainstTriangles(afterY, horizontal, triangles);

        return new Vec3(clippedHorizontal.x, dy, clippedHorizontal.z);
    }

    private static Vec3 clipHorizontalMovementAgainstTriangles(
            AABB entityBox,
            Vec3 movement,
            Collection<PropGeometry.Triangle> triangles
    ) {
        AABB box = entityBox;
        Vec3 remaining = movement;
        Vec3 applied = Vec3.ZERO;

        for (int iteration = 0;
             iteration < 5 && remaining.lengthSqr() >= 1.0e-12;
             iteration++) {

            AABB swept = box.expandTowards(remaining).inflate(1.0e-6);
            Hit nearest = null;
            double tieEpsilon = 1.0e-7;

            for (PropGeometry.Triangle triangle : triangles) {
                if (!triangleBounds(triangle).inflate(1.0e-6).intersects(swept))
                    continue;

                Hit hit = sweepTriangle(box, remaining, triangle);
                if (hit == null)
                    continue;

                if (!hit.faceAxis) {
                    Vec3 faceNormal = triangle.b().subtract(triangle.a()).cross(triangle.c().subtract(triangle.a()));
                    double normalLengthSqr = faceNormal.lengthSqr();
                    if (normalLengthSqr < 1.0e-12)
                        continue;

                    faceNormal = faceNormal.scale(1.0 / Math.sqrt(normalLengthSqr));
                    if (Math.abs(remaining.dot(faceNormal)) < 1.0e-7)
                        continue;
                }

                if (
                        nearest == null ||
                        hit.time < nearest.time - tieEpsilon ||
                        (Math.abs(hit.time - nearest.time) <= tieEpsilon && hit.faceAxis && !nearest.faceAxis)
                )
                    nearest = hit;
            }

            if (nearest == null) {
                applied = applied.add(remaining);
                break;
            }

            double time = Math.max(0.0, Math.min(1.0, nearest.time));
            double intoSurfaceSpeed = -remaining.dot(nearest.normal);
            if (intoSurfaceSpeed > 1.0e-12)
                time = Math.max(0.0, time - COLLISION_SKIN / intoSurfaceSpeed);

            Vec3 step = remaining.scale(time);
            applied = applied.add(step);
            box = box.move(step);
            Vec3 rest = remaining.scale(1.0 - time);
            double intoSurface = rest.dot(nearest.normal);
            if (intoSurface < 0.0)
                rest = rest.subtract(nearest.normal.scale(intoSurface));
            if (rest.lengthSqr() < 1.0e-12)
                break;
            if (time <= 1.0e-7 && rest.distanceToSqr(remaining) < 1.0e-12)
                break;

            remaining = rest;
        }

        return applied;
    }

    @SuppressWarnings("SameParameterValue")
    private static double clipAxisMovement(
            AABB box,
            double amount,
            int axis,
            Collection<PropGeometry.Triangle> triangles
    ) {
        if (Math.abs(amount) < 1.0e-12)
            return amount;

        Vec3 movement = switch (axis) {
            case 0 -> new Vec3(amount, 0.0, 0.0);
            case 1 -> new Vec3(0.0, amount, 0.0);
            default -> new Vec3(0.0, 0.0, amount);
        };
        AABB swept = box.expandTowards(movement).inflate(1.0e-6);
        double allowedTime = 1.0;

        for (PropGeometry.Triangle triangle : triangles) {
            if (!triangleBounds(triangle).inflate(1.0e-6).intersects(swept))
                continue;

            Hit hit = sweepTriangle(box, movement, triangle);
            if (hit == null)
                continue;

            double hitTime = Math.max(0.0, Math.min(1.0, hit.time));
            double skinTime = COLLISION_SKIN / Math.abs(amount);
            allowedTime = Math.min(allowedTime, Math.max(0.0, hitTime - skinTime));
            if (allowedTime <= 0.0)
                break;
        }

        return amount * allowedTime;
    }

    private static Hit sweepTriangle(
            AABB box,
            Vec3 movement,
            PropGeometry.Triangle triangle
    ) {
        Vec3 a = triangle.a();
        Vec3 b = triangle.b();
        Vec3 c = triangle.c();

        Vec3 edgeAB = b.subtract(a);
        Vec3 edgeBC = c.subtract(b);
        Vec3 edgeCA = a.subtract(c);

        Vec3 faceNormal = edgeAB.cross(c.subtract(a));
        if (faceNormal.lengthSqr() < 1.0e-12)
            return null;

        faceNormal = faceNormal.normalize();
        Vec3[] baseAxes = {
                new Vec3(1.0, 0.0, 0.0),
                new Vec3(0.0, 1.0, 0.0),
                new Vec3(0.0, 0.0, 1.0)
        };
        List<Vec3> axes = new ArrayList<>(13);
        axes.add(baseAxes[0]);
        axes.add(baseAxes[1]);
        axes.add(baseAxes[2]);
        axes.add(faceNormal);

        Vec3[] triangleEdges = {edgeAB, edgeBC, edgeCA};
        for (int edgeIndex = 0; edgeIndex < triangleEdges.length; edgeIndex++) {
            if (triangle.isSmoothEdge(edgeIndex))
                continue;
            Vec3 edge = triangleEdges[edgeIndex];
            for (Vec3 baseAxis : baseAxes) {
                Vec3 axis = edge.cross(baseAxis);
                if (axis.lengthSqr() >= 1.0e-12)
                    axes.add(axis.normalize());
            }
        }

        Vec3 center = new Vec3(
                (box.minX + box.maxX) * 0.5,
                (box.minY + box.maxY) * 0.5,
                (box.minZ + box.maxZ) * 0.5
        );
        Vec3 extents = new Vec3(
                (box.maxX - box.minX) * 0.5,
                (box.maxY - box.minY) * 0.5,
                (box.maxZ - box.minZ) * 0.5
        );

        double entryTime = Double.NEGATIVE_INFINITY;
        double exitTime = Double.POSITIVE_INFINITY;

        Vec3 entryNormal = null;
        boolean faceAxis = false;

        for (int i = 0; i < axes.size(); i++) {
            Vec3 axis = axes.get(i);
            double centerProjection = center.dot(axis);
            double radius = extents.x * Math.abs(axis.x) + extents.y * Math.abs(axis.y) + extents.z * Math.abs(axis.z);

            double boxMin = centerProjection - radius;
            double boxMax = centerProjection + radius;

            double triangleA = a.dot(axis);
            double triangleB = b.dot(axis);
            double triangleC = c.dot(axis);

            double triangleMin = Math.min(triangleA, Math.min(triangleB, triangleC));
            double triangleMax = Math.max(triangleA, Math.max(triangleB, triangleC));

            double velocity = movement.dot(axis);
            if (Math.abs(velocity) < 1.0e-12) {
                double epsilon = 1.0e-7;
                if (boxMax <= triangleMin + epsilon || boxMin >= triangleMax - epsilon)
                    return null;
                continue;
            }

            double first = (triangleMin - boxMax) / velocity;
            double second = (triangleMax - boxMin) / velocity;

            double axisEntry = Math.min(first, second);
            double axisExit = Math.max(first, second);
            if (
                    axisEntry > entryTime + 1.0e-7 ||
                    (Math.abs(axisEntry - entryTime) <= 1.0e-7 && i == 3 && !faceAxis)
            ) {
                entryTime = axisEntry;
                entryNormal = axis.scale(velocity > 0.0 ? -1.0 : 1.0);
                faceAxis = i == 3;
            }

            exitTime = Math.min(exitTime, axisExit);
            if (entryTime > exitTime + 1.0e-7)
                return null;
        }
        if (exitTime < -1.0e-7 || entryTime > 1.0 + 1.0e-7)
            return null;
        if (entryTime < -1.0e-7) {
            if (movement.dot(faceNormal) >= -1.0e-7)
                return null;
            return new Hit(0.0, faceNormal, true);
        }
        if (movement.dot(entryNormal) >= -1.0e-7) {
            Vec3 normal = movement.dot(faceNormal) > 0.0
                    ? faceNormal.scale(-1.0)
                    : faceNormal;
            if (movement.dot(normal) >= -1.0e-7)
                return null;
            entryNormal = normal;
            faceAxis = true;
        }
        return new Hit(
                Math.max(0.0, entryTime),
                entryNormal,
                faceAxis
        );
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

    public static Vec3 clipAgainstBlocks(Entity entity, Vec3 movement) {
        if (movement.lengthSqr() < 1.0e-12)
            return movement;

        AABB box = entity.getBoundingBox();
        List<VoxelShape> blockCollisions = new ArrayList<>();
        entity.level()
                .getBlockCollisions(entity, box.expandTowards(movement).inflate(1.0e-7))
                .forEach(blockCollisions::add);

        return Entity.collideBoundingBox(
                entity,
                movement,
                box,
                entity.level(),
                blockCollisions
        );
    }

    private static Vec3 transformPoint(Vec3 point, Matrix4f transform) {
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

    public static List<PropContact> findHorizontalContacts(
            UUID propId,
            AABB entityBox,
            Vec3 movement,
            Collection<PropGeometry.Triangle> triangles
    ) {
        Vec3 remaining = new Vec3(movement.x, 0.0, movement.z);
        if (triangles.isEmpty() || remaining.lengthSqr() < 1.0e-12)
            return List.of();

        double dy = clipAxisMovement(entityBox, movement.y, 1, triangles);
        AABB box = entityBox.move(0.0, dy, 0.0);
        List<PropContact> contacts = new ArrayList<>(2);

        for (int iteration = 0; iteration < 5 && remaining.lengthSqr() >= 1.0e-12; iteration++) {
            AABB swept = box.expandTowards(remaining).inflate(1.0e-6);
            Hit nearest = null;

            for (PropGeometry.Triangle triangle : triangles) {
                if (!triangleBounds(triangle).inflate(1.0e-6).intersects(swept))
                    continue;

                Hit hit = sweepTriangle(box, remaining, triangle);
                if (hit == null || remaining.dot(hit.normal) >= -1.0e-7)
                    continue;
                if (!hit.faceAxis) {
                    Vec3 faceNormal = triangle.b().subtract(triangle.a()).cross(triangle.c().subtract(triangle.a()));
                    double normalLengthSqr = faceNormal.lengthSqr();
                    if (normalLengthSqr < 1.0e-12)
                        continue;

                    faceNormal = faceNormal.scale(1.0 / Math.sqrt(normalLengthSqr));
                    if (Math.abs(remaining.dot(faceNormal)) < 1.0e-7)
                        continue;
                }
                if (
                        nearest == null ||
                        hit.time < nearest.time - 1.0e-7 ||
                        (Math.abs(hit.time - nearest.time) <= 1.0e-7 && hit.faceAxis && !nearest.faceAxis)
                )
                    nearest = hit;
            }
            if (nearest == null)
                break;

            double time = Math.max(0.0, Math.min(1.0, nearest.time));
            double intoSurfaceSpeed = -remaining.dot(nearest.normal);
            if (intoSurfaceSpeed <= 1.0e-7)
                break;

            contacts.add(new PropContact(propId, nearest.normal, time));
            time = Math.max(0.0, time - COLLISION_SKIN / intoSurfaceSpeed);
            Vec3 step = remaining.scale(time);
            box = box.move(step);
            Vec3 rest = remaining.scale(1.0 - time);
            double intoSurface = rest.dot(nearest.normal);
            if (intoSurface < 0.0)
                rest = rest.subtract(nearest.normal.scale(intoSurface));
            if (rest.lengthSqr() < 1.0e-12)
                break;
            if (time <= 1.0e-7 && rest.distanceToSqr(remaining) < 1.0e-12)
                break;

            remaining = rest;
        }

        return contacts;
    }

    private record Hit(
            double time,
            Vec3 normal,
            boolean faceAxis
    ) {}
}
