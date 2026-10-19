package net.centertain.ceac.prop.framework.physics;

import cc.modlabs.box3d.b3BodyDef;
import cc.modlabs.box3d.b3BodyId;
import cc.modlabs.box3d.b3HullData;
import cc.modlabs.box3d.b3ShapeDef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static cc.modlabs.box3d.global.Box3D.*;

public final class EntityBodyManager {
    private final PropPhysManager manager;

    private final Map<UUID, EntityBody> entityBodies = new HashMap<>();
    private final Map<PropPhysManager.BodyKey, EntityBody> entitiesByBody = new HashMap<>();

    EntityBodyManager(PropPhysManager manager) {
        this.manager = manager;
    }

    Map<PropPhysManager.BodyKey, EntityBody> entitiesByBody() {
        return entitiesByBody;
    }


    void synchronizeEntities(Collection<Entity> wantedEntities) {
        Set<UUID> wantedEntityIds = new HashSet<>();
        for (Entity entity : wantedEntities)
            wantedEntityIds.add(entity.getUUID());

        Iterator<Map.Entry<UUID, EntityBody>> iterator = entityBodies.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, EntityBody> entry = iterator.next();
            if (wantedEntityIds.contains(entry.getKey()))
                continue;

            EntityBody body = entry.getValue();

            entitiesByBody.remove(PropPhysManager.BodyKey.of(body.body));
            b3DestroyBody(body.body);
            iterator.remove();
        }
        for (Entity entity : wantedEntities)
            synchronizeEntityBody(entity);
    }

    private void synchronizeEntityBody(Entity entity) {
        UUID id = entity.getUUID();
        EntityBody existing = entityBodies.get(id);

        AABB worldBox = entity.getBoundingBox();
        AABB localBox = new AABB(
                worldBox.minX - entity.getX(),
                worldBox.minY - entity.getY(),
                worldBox.minZ - entity.getZ(),
                worldBox.maxX - entity.getX(),
                worldBox.maxY - entity.getY(),
                worldBox.maxZ - entity.getZ()
        );

        if (existing != null && !PropPhysGeometry.sameBox(existing.localBox, localBox)) {
            destroyEntityBody(id);
            existing = null;
        }

        Vec3 currentPosition = new Vec3(
                entity.getX(),
                entity.getY(),
                entity.getZ()
        );
        Vec3 tickDisplacement = new Vec3(
                entity.getX() - entity.xo,
                entity.getY() - entity.yo,
                entity.getZ() - entity.zo
        );
        boolean teleported = tickDisplacement.lengthSqr() > 4.0;
        if (teleported)
            tickDisplacement = Vec3.ZERO;

        Vec3 boxVelocity = tickDisplacement.scale(1.0 / PropPhysManager.FIXED_STEP);
        Vec3 boxStartPosition = currentPosition.subtract(tickDisplacement);
        if (existing == null) {
            AABB hullBox = localBox.inflate(PropPhysManager.ENTITY_CONTACT_SKIN);
            List<PropPhysGeometry.FloatPoint> points = PropPhysGeometry.boxPoints(
                    hullBox.minX, hullBox.minY, hullBox.minZ,
                    hullBox.maxX, hullBox.maxY, hullBox.maxZ
            );
            b3HullData hull = PropPhysGeometry.createHull(points, 8);
            if (hull == null || hull.isNull())
                return;

            try {
                b3BodyDef bodyDefinition = b3DefaultBodyDef();
                bodyDefinition.type(b3_kinematicBody);
                bodyDefinition.gravityScale(0.0f);
                bodyDefinition.enableSleep(false);
                bodyDefinition._position(PropPhysGeometry.toBoxPos(boxStartPosition));
                bodyDefinition.rotation(PropPhysGeometry.identityQuat());
                bodyDefinition.linearVelocity(PropPhysGeometry.toBoxVec(boxVelocity));

                b3BodyId body = b3CreateBody(manager.world, bodyDefinition);
                b3ShapeDef shapeDefinition = PropPhysGeometry.makeShapeDefinition(
                        PropPhysManager.CATEGORY_ENTITY,
                        PropPhysManager.MASK_ENTITY,
                        false,
                        false,
                        true,
                        0.0f
                );

                b3CreateHullShape(body, shapeDefinition, hull);

                EntityBody created = new EntityBody(entity, body, localBox);
                entityBodies.put(id, created);
                entitiesByBody.put(PropPhysManager.BodyKey.of(body), created);
                return;
            } finally {
                b3DestroyHull(hull);
            }
        }

        existing.entity = entity;

        b3Body_SetTransform(
                existing.body,
                PropPhysGeometry.toBoxPos(boxStartPosition),
                PropPhysGeometry.identityQuat()
        );
        b3Body_SetLinearVelocity(existing.body, PropPhysGeometry.toBoxVec(boxVelocity));
    }

    void destroyEntityBody(UUID id) {
        EntityBody removed = entityBodies.remove(id);
        if (removed == null)
            return;
        entitiesByBody.remove(PropPhysManager.BodyKey.of(removed.body));
        b3DestroyBody(removed.body);
    }

    void clear() {
        for (UUID id : new ArrayList<>(entityBodies.keySet()))
            destroyEntityBody(id);
    }


    static final class EntityBody {
        Entity entity;
        final b3BodyId body;
        final AABB localBox;

        EntityBody(Entity entity, b3BodyId body, AABB localBox) {
            this.entity = entity;
            this.body = body;
            this.localBox = localBox;
        }
    }
}
