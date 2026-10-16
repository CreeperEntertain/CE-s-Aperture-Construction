package net.centertain.ceac.prop.physics;

import net.centertain.ceac.prop.PropGeometry;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PropEntityCollision {
    private PropEntityCollision() {}

    public static AABB transformBounds(AABB local, Matrix4f transform) {
        double cx = (local.minX + local.maxX) * 0.5;
        double cy = (local.minY + local.maxY) * 0.5;
        double cz = (local.minZ + local.maxZ) * 0.5;

        double ex = (local.maxX - local.minX) * 0.5;
        double ey = (local.maxY - local.minY) * 0.5;
        double ez = (local.maxZ - local.minZ) * 0.5;

        double wx = transform.m00() * cx + transform.m10() * cy + transform.m20() * cz + transform.m30();
        double wy = transform.m01() * cx + transform.m11() * cy + transform.m21() * cz + transform.m31();
        double wz = transform.m02() * cx + transform.m12() * cy + transform.m22() * cz + transform.m32();

        double rx = Math.abs(transform.m00()) * ex + Math.abs(transform.m10()) * ey + Math.abs(transform.m20()) * ez;
        double ry = Math.abs(transform.m01()) * ex + Math.abs(transform.m11()) * ey + Math.abs(transform.m21()) * ez;
        double rz = Math.abs(transform.m02()) * ex + Math.abs(transform.m12()) * ey + Math.abs(transform.m22()) * ez;

        return new AABB(
                wx - rx, wy - ry, wz - rz,
                wx + rx, wy + ry, wz + rz
        );
    }

    public static Vec3 clipMovement(
            AABB entityBox,
            Vec3 movement,
            Collection<AABB> obstacles
    ) {
        if (obstacles.isEmpty() || movement.lengthSqr() < 1.0e-12)
            return movement;

        double dy = movement.y;
        for (AABB obstacle : obstacles)
            dy = clipY(entityBox, obstacle, dy);

        AABB afterY = entityBox.move(0.0, dy, 0.0);

        double dx = movement.x;
        double dz = movement.z;

        if (Math.abs(dx) < Math.abs(dz)) {
            for (AABB obstacle : obstacles)
                dz = clipZ(afterY, obstacle, dz);
            AABB afterZ = afterY.move(0.0, 0.0, dz);
            for (AABB obstacle : obstacles)
                dx = clipX(afterZ, obstacle, dx);
        } else {
            for (AABB obstacle : obstacles)
                dx = clipX(afterY, obstacle, dx);
            AABB afterX = afterY.move(dx, 0.0, 0.0);
            for (AABB obstacle : obstacles)
                dz = clipZ(afterX, obstacle, dz);
        }

        return new Vec3(dx, dy, dz);
    }

    private static double clipX(AABB box, AABB obstacle, double movement) {
        if (
                obstacle.maxY <= box.minY ||
                obstacle.minY >= box.maxY ||
                obstacle.maxZ <= box.minZ ||
                obstacle.minZ >= box.maxZ)
            return movement;

        if (movement > 0.0 && box.maxX <= obstacle.minX) {
            double distance = obstacle.minX - box.maxX;
            if (distance < movement)
                return Math.max(0.0, distance);
        } else if (movement < 0.0 && box.minX >= obstacle.maxX) {
            double distance = obstacle.maxX - box.minX;
            if (distance > movement)
                return Math.min(0.0, distance);
        }

        return movement;
    }

    private static double clipY(AABB box, AABB obstacle, double movement) {
        if (
                obstacle.maxX <= box.minX ||
                obstacle.minX >= box.maxX ||
                obstacle.maxZ <= box.minZ ||
                obstacle.minZ >= box.maxZ
        )
            return movement;

        if (movement > 0.0 && box.maxY <= obstacle.minY) {
            double distance = obstacle.minY - box.maxY;
            if (distance < movement)
                return Math.max(0.0, distance);
        } else if (movement < 0.0 && box.minY >= obstacle.maxY) {
            double distance = obstacle.maxY - box.minY;
            if (distance > movement)
                return Math.min(0.0, distance);
        }

        return movement;
    }

    private static double clipZ(AABB box, AABB obstacle, double movement) {
        if (
                obstacle.maxX <= box.minX ||
                obstacle.minX >= box.maxX ||
                obstacle.maxY <= box.minY ||
                obstacle.minY >= box.maxY
        )
            return movement;

        if (movement > 0.0 && box.maxZ <= obstacle.minZ) {
            double distance = obstacle.minZ - box.maxZ;
            if (distance < movement)
                return Math.max(0.0, distance);
        } else if (movement < 0.0 && box.minZ >= obstacle.maxZ) {
            double distance = obstacle.maxZ - box.minZ;
            if (distance > movement)
                return Math.min(0.0, distance);
        }

        return movement;
    }

    public static List<PropGeometry.Triangle> transformTriangles(PropGeometry geometry, Matrix4f transform) {
        List<PropGeometry.Triangle> transformed = new ArrayList<>(geometry.getTriangles().size());

        for (PropGeometry.Triangle triangle : geometry.getTriangles())
            transformed.add(new PropGeometry.Triangle(
                    transformPoint(triangle.a(), transform),
                    transformPoint(triangle.b(), transform),
                    transformPoint(triangle.c(), transform)
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

        for (PropGeometry.Triangle triangle : triangles)
            for (Vec3 vertex : List.of(
                    triangle.a(),
                    triangle.b(),
                    triangle.c()
            )) {
                minX = Math.min(minX, vertex.x);
                minY = Math.min(minY, vertex.y);
                minZ = Math.min(minZ, vertex.z);

                maxX = Math.max(maxX, vertex.x);
                maxY = Math.max(maxY, vertex.y);
                maxZ = Math.max(maxZ, vertex.z);
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

        AABB box = entityBox;
        Vec3 remaining = movement;
        Vec3 applied = Vec3.ZERO;
        for (int iteration = 0; iteration < 5 && remaining.lengthSqr() >= 1.0e-12; iteration++) {
            AABB swept = box.expandTowards(remaining).inflate(1.0e-6);
            Hit nearest = null;

            for (PropGeometry.Triangle triangle : triangles) {
                if (!triangleBounds(triangle).inflate(1.0e-6).intersects(swept))
                    continue;
                Hit hit = sweepTriangle(box, remaining, triangle);
                if (hit == null)
                    continue;
                if (
                        nearest == null ||
                        hit.time < nearest.time - 1.0e-7 ||
                        (Math.abs(hit.time - nearest.time) <= 1.0e-7 && hit.faceAxis && !nearest.faceAxis)
                )
                    nearest = hit;
            }
            if (nearest == null) {
                applied = applied.add(remaining);
                break;
            }

            double time = Math.max(0.0, Math.min(1.0, nearest.time));
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
        for (Vec3 edge : List.of(edgeAB, edgeBC, edgeCA))
            for (Vec3 baseAxis : baseAxes) {
                Vec3 axis = edge.cross(baseAxis);
                if (axis.lengthSqr() >= 1.0e-12)
                    axes.add(axis.normalize());
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
                if (boxMax < triangleMin - 1.0e-7 || boxMin > triangleMax + 1.0e-7)
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
            Vec3 normal = faceNormal;
            if (center.subtract(a).dot(normal) < 0.0)
                normal = normal.scale(-1.0);
            if (movement.dot(normal) >= -1.0e-7)
                return null;
            return new Hit(0.0, normal, true);
        }
        if (entryNormal == null)
            return null;
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

    private record Hit(
            double time,
            Vec3 normal,
            boolean faceAxis
    ) {}
}
