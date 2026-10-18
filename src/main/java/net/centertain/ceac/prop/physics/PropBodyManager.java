package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import com.mojang.logging.LogUtils;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.centertain.ceac.prop.enums.PropMotionMode;
import net.centertain.ceac.prop.network.SyncPropPacket;
import net.centertain.ceac.prop.network.SyncPropTransformPacket;
import net.centertain.ceac.prop.server.PropCapabilities;
import net.centertain.ceac.prop.server.PropManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.slf4j.Logger;

import java.util.*;

import static cc.modlabs.box3d.global.Box3D.*;

public final class PropBodyManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final PropPhysManager manager;

    private final Map<UUID, PropBody> propBodies = new HashMap<>();
    private final Map<PropPhysManager.BodyKey, PropBody> propsByBody = new HashMap<>();

    private final Map<LevelChunk, List<SyncPropTransformPacket.Transform>> pendingTransformUpdates = new LinkedHashMap<>();

    private final List<Prop> activeProps = new ArrayList<>();
    private final Set<UUID> seenPropIds = new HashSet<>();

    PropBodyManager(PropPhysManager manager) {
        this.manager = manager;
    }

    Map<UUID, PropBody> propBodies() {
        return propBodies;
    }
    Map<PropPhysManager.BodyKey, PropBody> propsByBody() {
        return propsByBody;
    }
    List<Prop> activeProps() {
        return activeProps;
    }


    void synchronizeProps() {
        seenPropIds.clear();
        activeProps.clear();

        for (LevelChunk chunk : manager.loadedChunks.values()) {
            PropManager propManager = PropCapabilities.get(chunk);
            for (Prop prop : propManager.getProps().values()) {
                if (!seenPropIds.add(prop.getId()))
                    continue;
                activeProps.add(prop);
                synchronizeProp(prop, chunk);
            }
        }

        Iterator<Map.Entry<UUID, PropBody>> iterator = propBodies.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PropBody> entry = iterator.next();
            if (seenPropIds.contains(entry.getKey()))
                continue;
            PropBody body = entry.getValue();
            propsByBody.remove(PropPhysManager.BodyKey.of(body.body));
            b3DestroyBody(body.body);
            iterator.remove();
        }
    }

    private void synchronizeProp(Prop prop, LevelChunk ownerChunk) {
        PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
        if (definition == null) {
            destroyPropBody(prop.getId());
            return;
        }

        PropBody existing = propBodies.get(prop.getId());
        long transformRevision = prop.getTransformRevision();
        boolean rebuild = existing == null ||
                !existing.definitionId.equals(prop.getDefinitionId()) ||
                existing.collisionMode != prop.getCollisionMode() ||
                existing.motionMode != prop.getMotionMode();
        if (!rebuild && existing.lastObservedTransformRevision != transformRevision)
            rebuild = !PropPhysGeometry.sameScale(existing.scale, prop.getScale());
        if (rebuild) {
            destroyPropBody(prop.getId());

            PropPhysGeometry.GeometryData geometry = manager.getGeometry(definition.getModelId());
            if (geometry == null)
                return;

            PropBody created = createPropBody(prop, ownerChunk, definition, geometry);
            if (created == null)
                return;

            propBodies.put(prop.getId(), created);
            propsByBody.put(PropPhysManager.BodyKey.of(created.body), created);
            return;
        }

        existing.prop = prop;
        existing.ownerChunk = ownerChunk;
        if (existing.lastObservedTransformRevision == transformRevision)
            return;

        Matrix4f currentTransform = prop.getTransform();

        b3Body_SetTransform(
                existing.body,
                PropPhysGeometry.toBoxPos(prop.getPosition()),
                PropPhysGeometry.toBoxQuat(prop.getRotationQuat())
        );

        existing.lastSyncedTransform.set(currentTransform);
        existing.lastObservedTransformRevision = transformRevision;
    }

    private PropBody createPropBody(
            Prop prop,
            LevelChunk ownerChunk,
            PropDefinition definition,
            PropPhysGeometry.GeometryData geometry
    ) {
        List<PropPhysGeometry.FloatPoint> vertices = PropPhysGeometry.scaledUniqueVertices(geometry.geometry, prop.getScale());
        if (vertices.size() < 4) {
            LOGGER.warn("Prop {} has fewer than four unique collision vertices", prop.getDefinitionId());
            return null;
        }

        b3HullData hull = PropPhysGeometry.createHull(vertices, PropPhysGeometry.MAX_PROP_HULL_VERTICES);
        if (hull == null || hull.isNull()) {
            LOGGER.warn("Could not create Box3D convex hull for prop {}", prop.getDefinitionId());
            return null;
        }

        b3BodyId body = null;

        try {
            PropPhysDefinition physics = definition.getPhysDefinition();

            b3BodyDef bodyDefinition = b3DefaultBodyDef();
            bodyDefinition.type(PropPhysGeometry.toBodyType(prop.getMotionMode()));
            bodyDefinition._position(PropPhysGeometry.toBoxPos(prop.getPosition()));
            bodyDefinition.rotation(PropPhysGeometry.toBoxQuat(prop.getRotationQuat()));
            bodyDefinition.linearDamping(physics.getLinearDamping());
            bodyDefinition.angularDamping(physics.getAngularDamping());
            bodyDefinition.gravityScale(physics.getGravityScale());
            bodyDefinition.sleepThreshold(physics.getSleepThreshold());
            bodyDefinition.enableSleep(physics.isSleepEnabled());
            bodyDefinition.isBullet(physics.isBullet());

            body = b3CreateBody(manager.world, bodyDefinition);

            float density = physics.getDensity();
            Float massOverride = physics.getMassOverride();
            if (massOverride != null && hull.volume() > 1.0e-6f)
                density = massOverride / hull.volume();

            double propMass = Math.max(1.0e-3, (double) density * hull.volume());

            boolean sensor = prop.getCollisionMode() == PropCollisionMode.NONE;
            long category = PropPhysGeometry.categoryFor(prop.getCollisionMode());
            long mask = PropPhysGeometry.maskFor(prop.getCollisionMode());

            b3ShapeDef shapeDefinition = PropPhysGeometry.makeShapeDefinition(
                    category,
                    mask,
                    sensor,
                    false,
                    prop.getMotionMode() != PropMotionMode.STATIC,
                    density
            );

            b3SurfaceMaterial material = b3DefaultSurfaceMaterial();
            material.friction(physics.getFriction());
            material.restitution(physics.getRestitution());
            shapeDefinition.baseMaterial(material);

            b3CreateHullShape(body, shapeDefinition, hull);

            return new PropBody(
                    prop,
                    ownerChunk,
                    body,
                    prop.getDefinitionId(),
                    prop.getCollisionMode(),
                    prop.getMotionMode(),
                    prop.getScale(),
                    propMass,
                    physics.getRestitution(),
                    physics.getGravityScale(),
                    prop.getTransform()
            );
        } catch (RuntimeException exception) {
            if (body != null)
                b3DestroyBody(body);
            LOGGER.error("Failed to create physics body for prop {}", prop.getId(), exception);
            return null;
        } finally {
            b3DestroyHull(hull);
        }
    }

    void destroyPropBody(UUID id) {
        PropBody removed = propBodies.remove(id);
        if (removed == null)
            return;
        propsByBody.remove(PropPhysManager.BodyKey.of(removed.body));
        b3DestroyBody(removed.body);
    }

    void synchronizePropTransforms() {
        pendingTransformUpdates.clear();

        for (PropBody body : propBodies.values()) {
            if (body.motionMode == PropMotionMode.STATIC)
                continue;
            writeBackTransform(body, true);
        }

        for (Map.Entry<LevelChunk, List<SyncPropTransformPacket.Transform>> entry : pendingTransformUpdates.entrySet()) {
            LevelChunk chunk = entry.getKey();
            List<SyncPropTransformPacket.Transform> updates = entry.getValue();

            for (int start = 0; start < updates.size(); start += SyncPropTransformPacket.MAX_TRANSFORMS_PER_PACKET) {
                int end = Math.min(start + SyncPropTransformPacket.MAX_TRANSFORMS_PER_PACKET, updates.size());
                SyncPropTransformPacket packet = new SyncPropTransformPacket(updates.subList(start, end));

                ModNetworking.CHANNEL.send(
                        PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                        packet
                );
            }
        }

        pendingTransformUpdates.clear();
    }

    @SuppressWarnings("SameParameterValue")
    void writeBackTransform(PropBody body, boolean sendUpdate) {
        Matrix4f before = body.transformBeforeScratch;
        Matrix4f after = body.transformAfterScratch;

        body.prop.copyTransformTo(before);

        b3Pos position = b3Body_GetPosition(body.body);
        b3Quat rotation = b3Body_GetRotation(body.body);
        Quaternionf quaternion = PropPhysGeometry.fromBoxQuat(rotation);

        body.prop.setPosition(new Vec3(
                position.x(),
                position.y(),
                position.z()
        ));
        body.prop.setRotation(quaternion);
        body.lastObservedTransformRevision = body.prop.getTransformRevision();

        body.prop.copyTransformTo(after);
        body.lastSyncedTransform.set(after);
        if (PropPhysGeometry.sameTransform(before, after))
            return;

        manager.propSpatialIndex.updatePropSpatialEntry(body.prop);
        body.ownerChunk.setUnsaved(true);

        boolean migrated = movePropToCurrentChunk(body);
        if (migrated) {
            body.lastNetworkedTransform.set(after);
            return;
        }
        if (!sendUpdate)
            return;
        if (!transformChangedEnough(after, body.lastNetworkedTransform))
            return;

        LevelChunk owner = body.ownerChunk;

        pendingTransformUpdates.computeIfAbsent(owner, ignored -> new ArrayList<>())
                .add(new SyncPropTransformPacket.Transform(
                        body.prop.getId(),
                        (float) position.x(),
                        (float) position.y(),
                        (float) position.z(),
                        quaternion.x,
                        quaternion.y,
                        quaternion.z,
                        quaternion.w
                ));

        body.lastNetworkedTransform.set(after);
    }

    private static boolean transformChangedEnough(Matrix4f current, Matrix4f previous) {
        float dx = current.m30() - previous.m30();
        float dy = current.m31() - previous.m31();
        float dz = current.m32() - previous.m32();
        if (dx * dx + dy * dy + dz * dz >= 1.0e-4f)
            return true;

        final float rotationEpsilon = 1.0e-3f;

        return
                Math.abs(current.m00() - previous.m00()) >= rotationEpsilon ||
                Math.abs(current.m01() - previous.m01()) >= rotationEpsilon ||
                Math.abs(current.m02() - previous.m02()) >= rotationEpsilon ||
                Math.abs(current.m10() - previous.m10()) >= rotationEpsilon ||
                Math.abs(current.m11() - previous.m11()) >= rotationEpsilon ||
                Math.abs(current.m12() - previous.m12()) >= rotationEpsilon ||
                Math.abs(current.m20() - previous.m20()) >= rotationEpsilon ||
                Math.abs(current.m21() - previous.m21()) >= rotationEpsilon ||
                Math.abs(current.m22() - previous.m22()) >= rotationEpsilon;
    }

    @SuppressWarnings("deprecation")
    private boolean movePropToCurrentChunk(PropBody body) {
        BlockPos position = body.prop.getBlockPosition();
        ChunkPos destinationPos = new ChunkPos(position);
        if (destinationPos.equals(body.ownerChunk.getPos()))
            return false;
        if (!manager.level.hasChunkAt(position))
            return false;

        LevelChunk destination = manager.level.getChunkAt(position);
        PropManager destinationManager = PropCapabilities.get(destination);
        if (destinationManager.getProp(body.prop.getId()) != null)
            return false;

        LevelChunk previousOwner = body.ownerChunk;
        PropManager previousManager = PropCapabilities.get(previousOwner);
        if (previousManager.getProp(body.prop.getId()) == null)
            return false;

        destinationManager.addProp(body.prop);
        previousManager.removeProp(body.prop.getId());
        body.ownerChunk = destination;

        ModNetworking.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> previousOwner),
                new SyncPropPacket(body.prop.getId(), new BlockPos(
                        previousOwner.getPos().x * 16,
                        0,
                        previousOwner.getPos().z * 16
                ))
        );
        ModNetworking.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> destination),
                new SyncPropPacket(body.prop)
        );

        return true;
    }


    static final class PropBody {
        Prop prop;
        LevelChunk ownerChunk;
        final b3BodyId body;
        final ResourceLocation definitionId;
        final PropCollisionMode collisionMode;
        final PropMotionMode motionMode;
        final Vec3 scale;
        final double mass;
        final double restitution;
        final double gravityScale;
        final Matrix4f lastSyncedTransform;
        final Matrix4f lastNetworkedTransform;

        Vec3 velocityBeforeStep = Vec3.ZERO;
        long lastObservedTransformRevision;

        final Matrix4f transformBeforeScratch = new Matrix4f();
        final Matrix4f transformAfterScratch = new Matrix4f();

        private PropBody(
                Prop prop,
                LevelChunk ownerChunk,
                b3BodyId body,
                ResourceLocation definitionId,
                PropCollisionMode collisionMode,
                PropMotionMode motionMode,
                Vec3 scale,
                double mass,
                double restitution,
                double gravityScale,
                Matrix4f lastSyncedTransform
        ) {
            this.prop = prop;
            this.ownerChunk = ownerChunk;
            this.body = body;
            this.definitionId = definitionId;
            this.collisionMode = collisionMode;
            this.motionMode = motionMode;
            this.scale = scale;
            this.mass = mass;
            this.restitution = restitution;
            this.gravityScale = gravityScale;
            this.lastSyncedTransform = new Matrix4f(lastSyncedTransform);
            this.lastNetworkedTransform = new Matrix4f(lastSyncedTransform);
            this.lastObservedTransformRevision = prop.getTransformRevision();
        }
    }
}
