package net.centertain.ceac.prop.physics;

import net.centertain.ceac.prop.PropGeometry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PropStepManager {
    private static final double COLLISION_SKIN = 1.0e-3;
    private static final double STEP_EPSILON = 1.0e-6;

    private PropStepManager() {}


    public static AABB collisionQueryBounds(Entity entity, Vec3 movement) {
        return entity.getBoundingBox()
                .expandTowards(movement)
                .expandTowards(new Vec3(
                        0.0,
                        Math.max(0.0, entity.getStepHeight()) + COLLISION_SKIN,
                        0.0
                ))
                .inflate(1.0e-6);
    }

    public static Vec3 clipMovement(
            Entity entity,
            Vec3 movement,
            Collection<PropGeometry.Triangle> triangles
    ) {
        if (triangles.isEmpty() || movement.lengthSqr() < 1.0e-12)
            return movement;

        AABB startBox = entity.getBoundingBox();
        Vec3 ordinary = PropEntityCollision.clipMovementAgainstTriangles(startBox, movement, triangles);
        ordinary = clipAgainstBlocks(entity, startBox, ordinary);

        Vec3 horizontal = new Vec3(movement.x, 0.0, movement.z);
        double requestedHorizontalSqr = horizontal.lengthSqr();
        if (requestedHorizontalSqr < 1.0e-12)
            return ordinary;

        double stepHeight = Math.max(0.0, entity.getStepHeight());
        if (stepHeight <= STEP_EPSILON || movement.y > STEP_EPSILON)
            return ordinary;

        boolean grounded = entity.onGround() || (movement.y < -STEP_EPSILON && ordinary.y > movement.y + STEP_EPSILON);
        if (!grounded)
            return ordinary;

        double ordinaryProgress = horizontalProgress(ordinary, horizontal);
        if (ordinaryProgress >= requestedHorizontalSqr - STEP_EPSILON)
            return ordinary;

        List<PropGeometry.Triangle> upwardTriangles = getUpwardCollisionTriangles(triangles);
        Vec3 upwardRequest = new Vec3(0.0, stepHeight + COLLISION_SKIN, 0.0);
        Vec3 upward = PropEntityCollision.clipMovementAgainstTriangles(startBox, upwardRequest, upwardTriangles);
        upward = clipAgainstBlocks(entity, startBox, upward);
        if (upward.y <= STEP_EPSILON)
            return ordinary;

        AABB raisedBox = startBox.move(0.0, upward.y, 0.0);
        Vec3 steppedHorizontal = PropEntityCollision.clipMovementAgainstTriangles(raisedBox, horizontal, triangles);
        steppedHorizontal = clipAgainstBlocks(entity, raisedBox, steppedHorizontal);
        double steppedProgress = horizontalProgress(steppedHorizontal, horizontal);
        if (steppedProgress <= ordinaryProgress + STEP_EPSILON)
            return ordinary;

        AABB advancedBox = raisedBox.move(steppedHorizontal);
        double downDistance = upward.y + COLLISION_SKIN;
        Vec3 downwardRequest = new Vec3(0.0, -downDistance, 0.0);
        Vec3 downward = PropEntityCollision.clipMovementAgainstTriangles(advancedBox, downwardRequest, triangles);
        downward = clipAgainstBlocks(entity, advancedBox, downward);
        if (downward.y <= downwardRequest.y + STEP_EPSILON)
            return ordinary;

        double finalRise = upward.y + downward.y;
        if (finalRise < ordinary.y - COLLISION_SKIN || finalRise > stepHeight + COLLISION_SKIN + STEP_EPSILON)
            return ordinary;

        return new Vec3(
                steppedHorizontal.x,
                finalRise,
                steppedHorizontal.z
        );
    }

    private static List<PropGeometry.Triangle> getUpwardCollisionTriangles(Collection<PropGeometry.Triangle> triangles) {
        List<PropGeometry.Triangle> result = new ArrayList<>(triangles.size());

        for (PropGeometry.Triangle triangle : triangles) {
            Vec3 normal = triangle.b().subtract(triangle.a()).cross(triangle.c().subtract(triangle.a()));
            double lengthSqr = normal.lengthSqr();
            if (lengthSqr < 1.0e-12)
                continue;
            if (Math.abs(normal.y) / Math.sqrt(lengthSqr) > 1.0e-6)
                result.add(triangle);
        }

        return result;
    }

    private static double horizontalProgress(Vec3 actual, Vec3 requested) {
        return actual.x * requested.x + actual.z * requested.z;
    }

    private static Vec3 clipAgainstBlocks(Entity entity, AABB box, Vec3 movement) {
        if (movement.lengthSqr() < 1.0e-12)
            return movement;

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
}
