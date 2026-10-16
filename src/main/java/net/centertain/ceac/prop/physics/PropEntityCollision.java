package net.centertain.ceac.prop.physics;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Collection;

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
}
