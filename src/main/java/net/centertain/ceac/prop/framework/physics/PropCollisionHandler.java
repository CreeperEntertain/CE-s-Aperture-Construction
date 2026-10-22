package net.centertain.ceac.prop.framework.physics;

import cc.modlabs.box3d.*;
import net.centertain.ceac.prop.framework.enums.PropCollisionMode;
import net.centertain.ceac.prop.framework.enums.PropMotionMode;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static cc.modlabs.box3d.global.Box3D.*;

@SuppressWarnings("resource")
public final class PropCollisionHandler {
    private static final double MAX_PLAYER_PUSH_SPEED = 8.0;
    private static final double PLAYER_IMPACT_MASS = 5.0;

    private final PropPhysManager manager;
    private final PropBodyManager propBodyManager;
    private final TerrainCollisionManager terrainCollisionManager;
    private final EntityBodyManager entityBodyManager;
    private final PropSpatialIndex propSpatialIndex;

    private final Set<ImpactKey> handledEntityImpacts = new HashSet<>();

    PropCollisionHandler(
            PropPhysManager manager,
            PropBodyManager propBodyManager,
            TerrainCollisionManager terrainCollisionManager,
            EntityBodyManager entityBodyManager,
            PropSpatialIndex propSpatialIndex
    ) {
        this.manager = manager;
        this.propBodyManager = propBodyManager;
        this.terrainCollisionManager = terrainCollisionManager;
        this.entityBodyManager = entityBodyManager;
        this.propSpatialIndex = propSpatialIndex;
    }


    void processHitEvents() {
        handledEntityImpacts.clear();

        b3ContactEvents events = b3World_GetContactEvents(manager.world());
        if (events.hitCount() <= 0)
            return;

        b3ContactHitEvent hits = events.hitEvents();
        if (hits == null || hits.isNull())
            return;

        for (int i = 0; i < events.hitCount(); i++) {
            b3ContactHitEvent hit = hits.position(i);

            b3BodyId bodyA = b3Shape_GetBody(hit.shapeIdA());
            b3BodyId bodyB = b3Shape_GetBody(hit.shapeIdB());

            PropPhysManager.BodyKey keyA = PropPhysManager.BodyKey.of(bodyA);
            PropPhysManager.BodyKey keyB = PropPhysManager.BodyKey.of(bodyB);

            PropBodyManager.PropBody propA = propBodyManager.propsByBody().get(keyA);
            PropBodyManager.PropBody propB = propBodyManager.propsByBody().get(keyB);
            EntityBodyManager.EntityBody entityA = entityBodyManager.entitiesByBody().get(keyA);
            EntityBodyManager.EntityBody entityB = entityBodyManager.entitiesByBody().get(keyB);

            float nx = hit.normal().x();
            float ny = hit.normal().y();
            float nz = hit.normal().z();
            float speed = hit.approachSpeed();

            if (propA != null && propB != null)
                handlePropImpact(propA, propB, nx, ny, nz, speed);
            if (propA != null && entityB != null) {
                ImpactKey impact = new ImpactKey(propA.prop.getId(), entityB.entity.getUUID());
                if (handledEntityImpacts.add(impact))
                    handleEntityImpact(propA, entityB, true, nx, ny, nz, speed);
            }
            if (propB != null && entityA != null) {
                ImpactKey impact = new ImpactKey(propB.prop.getId(), entityA.entity.getUUID());
                if (handledEntityImpacts.add(impact))
                    handleEntityImpact(propB, entityA, false, nx, ny, nz, speed);
            }

            if (propA != null && terrainCollisionManager.terrainPatchBodies.contains(keyB))
                manager.playImpactSound(propA, speed);
            if (propB != null && terrainCollisionManager.terrainPatchBodies.contains(keyA))
                manager.playImpactSound(propB, speed);

            if (propA != null && propA.motionMode == PropMotionMode.MOVING && (
                    propB != null && propB.motionMode == PropMotionMode.STATIC ||
                    terrainCollisionManager.terrainPatchBodies.contains(keyB)
            ))
                clipMovingVelocity(propA, true, nx, ny, nz);
            if (propB != null && propB.motionMode == PropMotionMode.MOVING && (
                    propA != null && propA.motionMode == PropMotionMode.STATIC ||
                    terrainCollisionManager.terrainPatchBodies.contains(keyA)
            ))
                clipMovingVelocity(propB, false, nx, ny, nz);
        }
    }

    private void handlePropImpact(
            PropBodyManager.PropBody a,
            PropBodyManager.PropBody b,
            float nx,
            float ny,
            float nz,
            float approachSpeed
    ) {
        if (
                a.prop.getCollisionMode() != PropCollisionMode.SOLID ||
                b.prop.getCollisionMode() != PropCollisionMode.SOLID
        )
            return;
        manager.playImpactSound(a, approachSpeed);
        manager.playImpactSound(b, approachSpeed);
        if (
                a.motionMode == PropMotionMode.DYNAMIC &&
                b.motionMode == PropMotionMode.MOVING
        )
            addVelocity(b, nx, ny, nz, approachSpeed * 0.5f);
        else if (
                b.motionMode == PropMotionMode.DYNAMIC &&
                a.motionMode == PropMotionMode.MOVING
        )
            addVelocity(a, -nx, -ny, -nz, approachSpeed * 0.5f);
    }

    private void handleEntityImpact(
            PropBodyManager.PropBody prop,
            EntityBodyManager.EntityBody entityBody,
            boolean propIsA,
            float nx,
            float ny,
            float nz,
            float approachSpeed
    ) {
        if (prop.prop.getCollisionMode() != PropCollisionMode.SOLID)
            return;
        if (approachSpeed <= 0.05f)
            return;

        Entity entity = entityBody.entity;
        double direction = propIsA ? 1.0 : -1.0;
        Vec3 normal = new Vec3(
                nx * direction,
                ny * direction,
                nz * direction
        );
        b3Pos propPosition = b3Body_GetPosition(prop.body);
        AABB entityBox = entity.getBoundingBox();

        Vec3 toEntity = new Vec3(
                (entityBox.minX + entityBox.maxX) * 0.5 - propPosition.x(),
                (entityBox.minY + entityBox.maxY) * 0.5 - propPosition.y(),
                (entityBox.minZ + entityBox.maxZ) * 0.5 - propPosition.z()
        );
        double normalLengthSqr = normal.lengthSqr();
        if (normalLengthSqr < 1.0e-10) {
            normal = toEntity;
            normalLengthSqr = normal.lengthSqr();
        }
        if (normalLengthSqr < 1.0e-10)
            return;

        normal = normal.scale(1.0 / Math.sqrt(normalLengthSqr));
        if (normal.dot(toEntity) < 0.0)
            normal = normal.scale(-1.0);

        Vec3 incomingPropVelocity = prop.velocityBeforeStep;
        if (prop.motionMode == PropMotionMode.DYNAMIC)
            incomingPropVelocity = incomingPropVelocity.add(
                    0.0,
                    -9.81 * PropPhysManager.FIXED_STEP * prop.gravityScale,
                    0.0
            );

        double propImpactSpeed = incomingPropVelocity.dot(normal);
        double entityImpactSpeed = entity.getDeltaMovement().scale(1.0 / PropPhysManager.FIXED_STEP).dot(normal);
        double relativeImpactSpeed = propImpactSpeed - entityImpactSpeed;
        if (relativeImpactSpeed <= 0.01)
            return;

        manager.playImpactSound(prop, approachSpeed);

        double entityMass = entity instanceof Player ? PLAYER_IMPACT_MASS : effectiveEntityMass(entity);
        double inversePropMass = prop.motionMode == PropMotionMode.DYNAMIC ? 1.0 / Math.max(1.0e-3, prop.mass) : 0.0;
        double inverseEntityMass = 1.0 / entityMass;
        double impulse = (1.0 + prop.restitution) * relativeImpactSpeed / (inversePropMass + inverseEntityMass);
        Vec3 entityVelocityChange = normal.scale(impulse * inverseEntityMass * PropPhysManager.FIXED_STEP);
        entity.push(
                entityVelocityChange.x,
                entityVelocityChange.y,
                entityVelocityChange.z
        );
        if (entity instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
        if (prop.motionMode != PropMotionMode.DYNAMIC)
            return;

        double targetPropNormalSpeed = propImpactSpeed - impulse / prop.mass;
        b3Vec3 currentPropVelocity = b3Body_GetLinearVelocity(prop.body);
        double currentPropNormalSpeed =
                currentPropVelocity.x() * normal.x +
                        currentPropVelocity.y() * normal.y +
                        currentPropVelocity.z() * normal.z;
        double correction = targetPropNormalSpeed - currentPropNormalSpeed;

        b3Body_SetLinearVelocity(prop.body, new b3Vec3()
                .x((float) (currentPropVelocity.x() + normal.x * correction))
                .y((float) (currentPropVelocity.y() + normal.y * correction))
                .z((float) (currentPropVelocity.z() + normal.z * correction))
        );
    }

    private static double effectiveEntityMass(Entity entity) {
        if (entity instanceof Player)
            return 80.0;

        double width = entity.getBbWidth();
        double height = entity.getBbHeight();

        return Math.max(5.0, width * width * height * 40.0);
    }

    private void addVelocity(
            PropBodyManager.PropBody body,
            float x,
            float y,
            float z,
            float strength
    ) {
        b3Vec3 velocity = b3Body_GetLinearVelocity(body.body);
        b3Body_SetLinearVelocity(body.body, new b3Vec3()
                .x(velocity.x() + x * strength)
                .y(velocity.y() + y * strength)
                .z(velocity.z() + z * strength)
        );
    }

    private void clipMovingVelocity(
            PropBodyManager.PropBody body,
            boolean bodyIsA,
            float nx,
            float ny,
            float nz
    ) {
        float direction = bodyIsA ? 1.0f : -1.0f;
        float ox = nx * direction;
        float oy = ny * direction;
        float oz = nz * direction;

        b3Vec3 velocity = b3Body_GetLinearVelocity(body.body);

        float intoObstacle = velocity.x() * ox + velocity.y() * oy + velocity.z() * oz;
        if (intoObstacle <= 0.0f)
            return;

        b3Body_SetLinearVelocity(body.body, new b3Vec3()
                .x(velocity.x() - ox * intoObstacle)
                .y(velocity.y() - oy * intoObstacle)
                .z(velocity.z() - oz * intoObstacle)
        );
    }

    void applyPlayerPushImpulse(
            Player player,
            UUID propId,
            Vec3 incomingVelocity
    ) {
        if (player.level() != manager.level() || player.isRemoved() || player.isSpectator() || player.noPhysics)
            return;
        if (!Double.isFinite(incomingVelocity.x) || !Double.isFinite(incomingVelocity.z))
            return;

        PropBodyManager.PropBody propBody = propBodyManager.propBodies().get(propId);
        if (
                propBody == null ||
                propBody.motionMode != PropMotionMode.DYNAMIC ||
                propBody.prop.getCollisionMode() != PropCollisionMode.SOLID
        )
            return;

        PropSpatialIndex.PropSpatialEntry entry = propSpatialIndex.entry(propId);
        if (
                entry == null ||
                !entry.worldBounds.inflate(0.25)
                        .intersects(player.getBoundingBox().inflate(0.25))
        )
            return;

        double speedSquared = incomingVelocity.x * incomingVelocity.x + incomingVelocity.z * incomingVelocity.z;
        if (speedSquared < 1.0e-6)
            return;

        double speed = Math.sqrt(speedSquared);
        if (speed > MAX_PLAYER_PUSH_SPEED)
            incomingVelocity = incomingVelocity.scale(MAX_PLAYER_PUSH_SPEED / speed);

        List<PropEntityCollision.PropContact> contacts = PropEntityCollision.findHorizontalContacts(
                propId,
                player.getBoundingBox(),
                incomingVelocity.scale(PropPhysManager.FIXED_STEP),
                entry.triangles
        );
        if (contacts.isEmpty())
            return;

        b3Vec3 boxVelocity = b3Body_GetLinearVelocity(propBody.body);
        Vec3 propVelocity = new Vec3(
                boxVelocity.x(),
                boxVelocity.y(),
                boxVelocity.z()
        );

        Vec3 relativeVelocity = incomingVelocity.subtract(propVelocity);
        double playerMass = effectiveEntityMass(player);
        double inversePlayerMass = 1.0 / playerMass;
        double inversePropMass = 1.0 / Math.max(1.0e-3, propBody.mass);

        AABB playerBounds = player.getBoundingBox();

        Vec3 playerCenter = new Vec3(
                (playerBounds.minX + playerBounds.maxX) * 0.5,
                (playerBounds.minY + playerBounds.maxY) * 0.5,
                (playerBounds.minZ + playerBounds.maxZ) * 0.5
        );

        for (PropEntityCollision.PropContact contact : contacts) {
            Vec3 normal = contact.normal().normalize();
            if (normal.x * normal.x + normal.z * normal.z < 0.09)
                continue;

            double approachSpeed = -incomingVelocity.dot(normal);
            if (approachSpeed <= 0.05)
                continue;

            double relativeClosingSpeed = -relativeVelocity.dot(normal);
            if (relativeClosingSpeed <= 0.05)
                continue;

            double impulseMagnitude =
                    (1.0 + propBody.restitution) *
                    relativeClosingSpeed /
                    (inversePropMass + inversePlayerMass);
            if (!Double.isFinite(impulseMagnitude) || impulseMagnitude <= 0.0)
                continue;

            Vec3 impulse = normal.scale(-impulseMagnitude);
            double supportRadius =
                    Math.abs(normal.x) * player.getBbWidth() * 0.5 +
                    Math.abs(normal.y) * player.getBbHeight() * 0.5 +
                    Math.abs(normal.z) * player.getBbWidth() * 0.5;
            Vec3 contactPoint = playerCenter.subtract(normal.scale(supportRadius));

            b3Body_ApplyLinearImpulse(
                    propBody.body,
                    PropPhysGeometry.toBoxVec(impulse),
                    PropPhysGeometry.toBoxPos(contactPoint),
                    true
            );

            return;
        }
    }

    void clear() {
        handledEntityImpacts.clear();
    }


    private record ImpactKey(UUID propId, UUID entityId) {}
}
