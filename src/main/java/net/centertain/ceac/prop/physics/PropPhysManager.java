package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import com.mojang.logging.LogUtils;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.centertain.ceac.prop.enums.PropMotionMode;
import net.centertain.ceac.prop.network.SyncPropPacket;
import net.centertain.ceac.prop.server.PropCapabilities;
import net.centertain.ceac.prop.server.PropManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.*;

import static cc.modlabs.box3d.global.Box3D.*;
import static net.centertain.ceac.CeacMod.MOD_ID;

@SuppressWarnings("resource")
@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PropPhysManager implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final float FIXED_STEP = 1.0f / 20.0f;
    private static final int SUB_STEPS = 4;

    private static final int MAX_PROP_HULL_VERTICES = 32;

    private static final long CATEGORY_WORLD = 1L;
    private static final long CATEGORY_SOLID_PROP = 1L << 1;
    private static final long CATEGORY_PASSIVE_PROP = 1L << 2;
    private static final long CATEGORY_ENTITY = 1L << 3;

    private static final long MASK_WORLD =
            CATEGORY_SOLID_PROP |
            CATEGORY_PASSIVE_PROP |
            CATEGORY_ENTITY;
    private static final long MASK_SOLID_PROP =
            CATEGORY_WORLD |
            CATEGORY_SOLID_PROP |
            CATEGORY_ENTITY;

    private static final long MASK_PASSIVE_PROP = CATEGORY_WORLD;

    private static final long MASK_ENTITY =
            CATEGORY_WORLD |
            CATEGORY_SOLID_PROP |
            CATEGORY_ENTITY;

    private static final Map<ServerLevel, PropPhysManager> INSTANCES = new IdentityHashMap<>();

    private final ServerLevel level;
    private final b3WorldId world;

    private final Map<Long, LevelChunk> loadedChunks = new LinkedHashMap<>();

    private final Map<UUID, PropBody> propBodies = new HashMap<>();
    private final Map<BodyKey, PropBody> propsByBody = new HashMap<>();

    private final Map<UUID, EntityBody> entityBodies = new HashMap<>();
    private final Map<BodyKey, EntityBody> entitiesByBody = new HashMap<>();

    private final Map<Long, TerrainBody> terrainBodies = new HashMap<>();
    private final Map<BodyKey, TerrainBody> terrainByBody = new HashMap<>();

    private final Map<ResourceLocation, Optional<GeometryData>> geometryCache = new HashMap<>();

    private List<Prop> activeProps = List.of();
    private boolean closed;

    private PropPhysManager(ServerLevel level) {
        this.level = level;

        b3WorldDef worldDefinition = b3DefaultWorldDef();
        worldDefinition.gravity(new b3Vec3().x(0.0f).y(-9.81f).z(0.0f));
        worldDefinition.hitEventThreshold(0.1f);

        this.world = b3CreateWorld(worldDefinition);
    }


    private static PropPhysManager getOrCreate(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level, PropPhysManager::new);
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;
        if (!(event.getChunk() instanceof LevelChunk chunk))
            return;
        PropPhysManager manager = getOrCreate(serverLevel);
        manager.onChunkLoaded(chunk);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;
        if (!(event.getChunk() instanceof LevelChunk chunk))
            return;
        PropPhysManager manager = INSTANCES.get(serverLevel);
        if (manager != null)
            manager.onChunkUnloaded(chunk);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!(event.level instanceof ServerLevel serverLevel))
            return;
        PropPhysManager manager = INSTANCES.get(serverLevel);
        if (manager != null && !manager.closed)
            manager.tick();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        Iterator<Map.Entry<ServerLevel, PropPhysManager>> iterator = INSTANCES.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ServerLevel, PropPhysManager> entry = iterator.next();
            if (entry.getKey().getServer() != event.getServer())
                continue;
            entry.getValue().close();
            iterator.remove();
        }
    }


    private void onChunkLoaded(LevelChunk chunk) {
        loadedChunks.put(chunk.getPos().toLong(), chunk);
    }

    private void onChunkUnloaded(LevelChunk chunk) {
        long chunkKey = chunk.getPos().toLong();
        loadedChunks.remove(chunkKey);

        for (PropBody body : new ArrayList<>(propBodies.values())) {
            if (body.ownerChunk.getPos().toLong() != chunkKey)
                continue;
            writeBackTransform(body, false);
            destroyPropBody(body.prop.getId());
        }
        for (Map.Entry<Long, TerrainBody> entry : new ArrayList<>(terrainBodies.entrySet())) {
            TerrainBody terrain = entry.getValue();
            if (!new ChunkPos(terrain.position).equals(chunk.getPos()))
                continue;
            destroyTerrainBody(entry.getKey());
        }
    }

    private void tick() {
        synchronizeProps();
        synchronizeTerrain();
        synchronizeEntities();

        applyMovingBodyGravity();

        b3World_Step(world, FIXED_STEP, SUB_STEPS);

        processHitEvents();
        synchronizePropTransforms();
    }

    private void synchronizeProps() {
        Map<UUID, PropOwner> discovered = new HashMap<>();
        List<Prop> props = new ArrayList<>();

        for (LevelChunk chunk : loadedChunks.values()) {
            PropManager manager = PropCapabilities.get(chunk);
            for (Prop prop : manager.getProps().values()) {
                discovered.put(prop.getId(), new PropOwner(prop, chunk));
                props.add(prop);
            }
        }
        for (PropOwner owner : discovered.values())
            synchronizeProp(owner.prop, owner.chunk);
        for (UUID id : new ArrayList<>(propBodies.keySet()))
            if (!discovered.containsKey(id))
                destroyPropBody(id);

        activeProps = List.copyOf(props);
    }

    private void synchronizeProp(Prop prop, LevelChunk ownerChunk) {
        PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
        if (definition == null) {
            destroyPropBody(prop.getId());
            return;
        }

        Vec3 scale = prop.getScale();
        PropBody existing = propBodies.get(prop.getId());
        boolean rebuild = existing == null
                || !existing.definitionId.equals(prop.getDefinitionId())
                || existing.collisionMode != prop.getCollisionMode()
                || existing.motionMode != prop.getMotionMode()
                || !sameScale(existing.scale, scale);

        if (rebuild) {
            destroyPropBody(prop.getId());
            GeometryData geometry = getGeometry(definition.getModelId());
            if (geometry == null)
                return;

            PropBody created = createPropBody(prop, ownerChunk, definition, geometry);
            if (created == null)
                return;

            propBodies.put(prop.getId(), created);
            propsByBody.put(BodyKey.of(created.body), created);
            existing = created;
        } else {
            existing.prop = prop;
            existing.ownerChunk = ownerChunk;
        }

        Matrix4f currentTransform = prop.getTransform();

        if (!sameTransform(currentTransform, existing.lastSyncedTransform)) {
            b3Body_SetTransform(
                    existing.body,
                    toBoxPos(prop.getPosition()),
                    toBoxQuat(prop.getRotationQuat())
            );
            existing.lastSyncedTransform = new Matrix4f(currentTransform);
        }
    }

    private PropBody createPropBody(
            Prop prop,
            LevelChunk ownerChunk,
            PropDefinition definition,
            GeometryData geometry
    ) {
        List<FloatPoint> vertices = scaledUniqueVertices(geometry.geometry, prop.getScale());
        if (vertices.size() < 4) {
            LOGGER.warn("Prop {} has fewer than four unique collision vertices", prop.getDefinitionId());
            return null;
        }

        b3HullData hull = createHull(vertices, MAX_PROP_HULL_VERTICES);
        if (hull == null || hull.isNull()) {
            LOGGER.warn("Could not create Box3D convex hull for prop {}", prop.getDefinitionId());
            return null;
        }

        b3BodyId body = null;

        try {
            PropPhysDefinition physics = definition.getPhysDefinition();

            b3BodyDef bodyDefinition = b3DefaultBodyDef();
            bodyDefinition.type(toBodyType(prop.getMotionMode()));
            bodyDefinition._position(toBoxPos(prop.getPosition()));
            bodyDefinition.rotation(toBoxQuat(prop.getRotationQuat()));
            bodyDefinition.linearDamping(physics.getLinearDamping());
            bodyDefinition.angularDamping(physics.getAngularDamping());
            bodyDefinition.gravityScale(physics.getGravityScale());
            bodyDefinition.sleepThreshold(physics.getSleepThreshold());
            bodyDefinition.enableSleep(physics.isSleepEnabled());
            bodyDefinition.isBullet(physics.isBullet());

            body = b3CreateBody(world, bodyDefinition);

            float density = physics.getDensity();
            Float massOverride = physics.getMassOverride();
            if (massOverride != null && hull.volume() > 1.0e-6f)
                density = massOverride / hull.volume();

            boolean sensor = prop.getCollisionMode() == PropCollisionMode.NONE;
            long category = categoryFor(prop.getCollisionMode());
            long mask = maskFor(prop.getCollisionMode());

            b3ShapeDef shapeDefinition = makeShapeDefinition(
                    category,
                    mask,
                    sensor,
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

    private void destroyPropBody(UUID id) {
        PropBody removed = propBodies.remove(id);
        if (removed == null)
            return;
        propsByBody.remove(BodyKey.of(removed.body));
        b3DestroyBody(removed.body);
    }

    private void synchronizePropTransforms() {
        for (PropBody body : new ArrayList<>(propBodies.values())) {
            if (body.motionMode == PropMotionMode.STATIC)
                continue;
            writeBackTransform(body, true);
        }
    }

    private void writeBackTransform(PropBody body, boolean sendUpdate) {
        Matrix4f before = body.prop.getTransform();

        b3Pos position = b3Body_GetPosition(body.body);
        b3Quat rotation = b3Body_GetRotation(body.body);

        body.prop.setPosition(new Vec3(position.x(), position.y(), position.z()));
        body.prop.setRotation(fromBoxQuat(rotation));

        Matrix4f after = body.prop.getTransform();
        body.lastSyncedTransform = new Matrix4f(after);
        if (sameTransform(before, after))
            return;

        body.ownerChunk.setUnsaved(true);

        boolean migrated = movePropToCurrentChunk(body);
        if (sendUpdate && !migrated) {
            LevelChunk owner = body.ownerChunk;
            ModNetworking.CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> owner),
                    new SyncPropPacket(body.prop)
            );
        }
    }

    @SuppressWarnings("deprecation")
    private boolean movePropToCurrentChunk(PropBody body) {
        BlockPos position = body.prop.getBlockPosition();
        ChunkPos destinationPos = new ChunkPos(position);
        if (destinationPos.equals(body.ownerChunk.getPos()))
            return false;
        if (!level.hasChunkAt(position))
            return false;

        LevelChunk destination = level.getChunkAt(position);
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

    @SuppressWarnings("deprecation")
    private void synchronizeTerrain() {
        Set<Long> wanted = new HashSet<>();

        for (Prop prop : activeProps) {
            if (prop.getCollisionMode() == PropCollisionMode.NONE)
                continue;

            PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            GeometryData geometry = getGeometry(definition.getModelId());
            if (geometry == null)
                continue;

            AABB bounds = worldBounds(geometry, prop.getTransform());

            int minX = floor(bounds.minX) - 1;
            int minY = Math.max(level.getMinBuildHeight(), floor(bounds.minY) - 1);
            int minZ = floor(bounds.minZ) - 1;
            int maxX = floor(bounds.maxX) + 1;
            int maxY = Math.min(level.getMaxBuildHeight() - 1, floor(bounds.maxY) + 1);
            int maxZ = floor(bounds.maxZ) + 1;

            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (!level.hasChunkAt(position))
                            continue;

                        long key = position.asLong();
                        wanted.add(key);

                        BlockState state = level.getBlockState(position);
                        TerrainBody cached = terrainBodies.get(key);
                        if (cached != null && cached.state.equals(state))
                            continue;

                        destroyTerrainBody(key);

                        TerrainBody created = createTerrainBody(position, state);
                        terrainBodies.put(key, created);
                        if (created.body != null)
                            terrainByBody.put(BodyKey.of(created.body), created);
                    }
        }

        for (Long key : new ArrayList<>(terrainBodies.keySet()))
            if (!wanted.contains(key))
                destroyTerrainBody(key);
    }

    private TerrainBody createTerrainBody(BlockPos position, BlockState state) {
        VoxelShape voxelShape = state.getCollisionShape(level, position);
        if (voxelShape.isEmpty())
            return new TerrainBody(position, state, null);

        b3BodyDef bodyDefinition = b3DefaultBodyDef();
        bodyDefinition.type(b3_staticBody);
        bodyDefinition._position(new b3Pos()
                .x(position.getX())
                .y(position.getY())
                .z(position.getZ())
        );
        bodyDefinition.rotation(identityQuat());

        b3BodyId body = b3CreateBody(world, bodyDefinition);
        int attachedShapes = 0;

        for (AABB box : voxelShape.toAabbs()) {
            List<FloatPoint> points = boxPoints(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
            b3HullData hull = createHull(points, 8);
            if (hull == null || hull.isNull())
                continue;

            try {
                b3ShapeDef shapeDefinition = makeShapeDefinition(
                        CATEGORY_WORLD,
                        MASK_WORLD,
                        false,
                        false,
                        0.0f
                );
                b3CreateHullShape(body, shapeDefinition, hull);
                attachedShapes++;
            } finally {
                b3DestroyHull(hull);
            }
        }

        if (attachedShapes == 0) {
            b3DestroyBody(body);
            return new TerrainBody(position, state, null);
        }

        return new TerrainBody(position, state, body);
    }

    private void destroyTerrainBody(long key) {
        TerrainBody body = terrainBodies.remove(key);
        if (body == null || body.body == null)
            return;
        terrainByBody.remove(BodyKey.of(body.body));
        b3DestroyBody(body.body);
    }

    private void synchronizeEntities() {
        Map<UUID, Entity> wanted = new HashMap<>();

        for (Prop prop : activeProps) {
            if (prop.getCollisionMode() != PropCollisionMode.SOLID)
                continue;

            PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            GeometryData geometry = getGeometry(definition.getModelId());
            if (geometry == null)
                continue;

            AABB bounds = worldBounds(geometry, prop.getTransform()).inflate(1.0);

            for (Entity entity : level.getEntities(null, bounds)) {
                if (entity.isRemoved() || entity.isSpectator())
                    continue;
                wanted.put(entity.getUUID(), entity);
            }
        }
        for (UUID id : new ArrayList<>(entityBodies.keySet()))
            if (!wanted.containsKey(id))
                destroyEntityBody(id);
        for (Entity entity : wanted.values())
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

        if (existing != null && !sameBox(existing.localBox, localBox)) {
            destroyEntityBody(id);
            existing = null;
        }
        if (existing == null) {
            List<FloatPoint> points = boxPoints(
                    localBox.minX, localBox.minY, localBox.minZ,
                    localBox.maxX, localBox.maxY, localBox.maxZ
            );
            b3HullData hull = createHull(points, 8);
            if (hull == null || hull.isNull())
                return;

            try {
                Vec3 velocity = entity.getDeltaMovement();

                b3BodyDef bodyDefinition = b3DefaultBodyDef();
                bodyDefinition.type(b3_kinematicBody);
                bodyDefinition.gravityScale(0.0f);
                bodyDefinition.enableSleep(false);
                bodyDefinition._position(toBoxPos(new Vec3(
                        entity.getX() - velocity.x * FIXED_STEP,
                        entity.getY() - velocity.y * FIXED_STEP,
                        entity.getZ() - velocity.z * FIXED_STEP
                )));
                bodyDefinition.rotation(identityQuat());
                bodyDefinition.linearVelocity(toBoxVec(velocity));

                b3BodyId body = b3CreateBody(world, bodyDefinition);
                b3ShapeDef shapeDefinition = makeShapeDefinition(
                        CATEGORY_ENTITY,
                        MASK_ENTITY,
                        false,
                        true,
                        0.0f
                );

                b3CreateHullShape(body, shapeDefinition, hull);

                EntityBody created = new EntityBody(entity, body, localBox);

                entityBodies.put(id, created);
                entitiesByBody.put(BodyKey.of(body), created);
                return;
            } finally {
                b3DestroyHull(hull);
            }
        }

        existing.entity = entity;

        Vec3 velocity = entity.getDeltaMovement();

        b3Body_SetTransform(
                existing.body,
                toBoxPos(new Vec3(
                        entity.getX() - velocity.x * FIXED_STEP,
                        entity.getY() - velocity.y * FIXED_STEP,
                        entity.getZ() - velocity.z * FIXED_STEP
                )),
                identityQuat()
        );
        b3Body_SetLinearVelocity(existing.body, toBoxVec(velocity));
    }

    private void destroyEntityBody(UUID id) {
        EntityBody removed = entityBodies.remove(id);
        if (removed == null)
            return;
        entitiesByBody.remove(BodyKey.of(removed.body));
        b3DestroyBody(removed.body);
    }

    private void applyMovingBodyGravity() {
        for (PropBody body : propBodies.values()) {
            if (body.motionMode != PropMotionMode.MOVING)
                continue;
            b3Vec3 velocity = b3Body_GetLinearVelocity(body.body);
            b3Body_SetLinearVelocity(body.body, new b3Vec3()
                    .x(velocity.x() * 0.99f)
                    .y((velocity.y() - 9.81f * FIXED_STEP) * 0.99f)
                    .z(velocity.z() * 0.99f)
            );
        }
    }

    private void processHitEvents() {
        b3ContactEvents events = b3World_GetContactEvents(world);
        if (events.hitCount() <= 0)
            return;

        b3ContactHitEvent hits = events.hitEvents();
        if (hits == null || hits.isNull())
            return;

        Set<ImpactKey> handledEntityImpacts = new HashSet<>();

        for (int i = 0; i < events.hitCount(); i++) {
            b3ContactHitEvent hit = hits.position(i);

            b3BodyId bodyA = b3Shape_GetBody(hit.shapeIdA());
            b3BodyId bodyB = b3Shape_GetBody(hit.shapeIdB());

            BodyKey keyA = BodyKey.of(bodyA);
            BodyKey keyB = BodyKey.of(bodyB);

            PropBody propA = propsByBody.get(keyA);
            PropBody propB = propsByBody.get(keyB);
            EntityBody entityA = entitiesByBody.get(keyA);
            EntityBody entityB = entitiesByBody.get(keyB);

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
            if (propA != null && propA.motionMode == PropMotionMode.MOVING && (
                    propB != null && propB.motionMode == PropMotionMode.STATIC ||
                    terrainByBody.containsKey(keyB)
            ))
                clipMovingVelocity(propA, true, nx, ny, nz);
            if (propB != null && propB.motionMode == PropMotionMode.MOVING && (
                    propA != null && propA.motionMode == PropMotionMode.STATIC ||
                    terrainByBody.containsKey(keyA)
            ))
                clipMovingVelocity(propB, false, nx, ny, nz);
        }
    }

    private void handlePropImpact(
            PropBody a,
            PropBody b,
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
            PropBody prop,
            EntityBody entityBody,
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

        float direction = propIsA ? 1.0f : -1.0f;
        double awayX = nx * direction;
        double awayY = ny * direction;
        double awayZ = nz * direction;

        double strength;

        if (prop.motionMode == PropMotionMode.DYNAMIC)
            strength = clamp(approachSpeed * 0.16, 0.1, 1.25);
        else {
            Vec3 entityVelocity = entityBody.entity.getDeltaMovement();
            double movingIntoProp =
                    entityVelocity.x * awayX +
                    entityVelocity.y * awayY +
                    entityVelocity.z * awayZ;
            if (movingIntoProp >= -0.02)
                return;
            strength = clamp(approachSpeed * 0.08, 0.05, 0.45);
        }

        entityBody.entity.push(
                awayX * strength,
                awayY * strength,
                awayZ * strength
        );
    }

    private void addVelocity(
            PropBody body,
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
            PropBody body,
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

        float intoObstacle =
                velocity.x() * ox *
                velocity.y() * oy *
                velocity.z() * oz;
        if (intoObstacle <= 0.0f)
            return;

        b3Body_SetLinearVelocity(body.body, new b3Vec3()
                .x(velocity.x() - ox * intoObstacle)
                .y(velocity.y() - oy * intoObstacle)
                .z(velocity.z() - oz * intoObstacle)
        );
    }


    private GeometryData getGeometry(ResourceLocation modelId) {
        Optional<GeometryData> cached = geometryCache.get(modelId);
        if (cached != null)
            return cached.orElse(null);

        PropGeometry geometry = PropGeometry.get(level.getServer().getResourceManager(), modelId);
        if (geometry == null) {
            geometryCache.put(modelId, Optional.empty());
            return null;
        }

        GeometryData data = new GeometryData(geometry);
        geometryCache.put(modelId, Optional.of(data));
        return data;
    }

    private static AABB worldBounds(GeometryData geometry, Matrix4f transform) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (int x = 0; x < 2; x++)
            for (int y = 0; y < 2; y++)
                for (int z = 0; z < 2; z++) {
                    Vector3f point = new Vector3f(
                            (float) (x == 0 ? geometry.minX : geometry.maxX),
                            (float) (y == 0 ? geometry.minY : geometry.maxY),
                            (float) (z == 0 ? geometry.minZ : geometry.maxZ)
                    ).normalize();

                    minX = Math.min(minX, point.x);
                    minY = Math.min(minY, point.y);
                    minZ = Math.min(minZ, point.z);
                    maxX = Math.max(maxX, point.x);
                    maxY = Math.max(maxY, point.y);
                    maxZ = Math.max(maxZ, point.z);
                }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static List<FloatPoint> scaledUniqueVertices(PropGeometry geometry, Vec3 scale) {
        Map<PointKey, FloatPoint> unique = new LinkedHashMap<>();
        for (PropGeometry.Triangle triangle : geometry.getTriangles()) {
            addScaledVertex(unique, triangle.a(), scale);
            addScaledVertex(unique, triangle.b(), scale);
            addScaledVertex(unique, triangle.c(), scale);
        }
        return new ArrayList<>(unique.values());
    }

    private static void addScaledVertex(
            Map<PointKey, FloatPoint> unique,
            Vec3 vertex,
            Vec3 scale
    ) {
        float x = (float) (vertex.x * scale.x);
        float y = (float) (vertex.y * scale.y);
        float z = (float) (vertex.z * scale.z);
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z))
            return;

        PointKey key = new PointKey(
                Float.floatToIntBits(x),
                Float.floatToIntBits(y),
                Float.floatToIntBits(z)
        );

        unique.putIfAbsent(key, new FloatPoint(x, y, z));
    }

    private static List<FloatPoint> boxPoints(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        return List.of(
                new FloatPoint((float) minX, (float) minY, (float) minZ),
                new FloatPoint((float) maxX, (float) minY, (float) minZ),
                new FloatPoint((float) minX, (float) maxY, (float) minZ),
                new FloatPoint((float) maxX, (float) maxY, (float) minZ),
                new FloatPoint((float) minX, (float) minY, (float) maxZ),
                new FloatPoint((float) maxX, (float) minY, (float) maxZ),
                new FloatPoint((float) minX, (float) maxY, (float) maxZ),
                new FloatPoint((float) maxX, (float) maxY, (float) maxZ)
        );
    }

    private static b3HullData createHull(
            List<FloatPoint> vertices,
            int maxVertices
    ) {
        if (vertices.size() < 4)
            return null;

        try (b3Vec3 points = new b3Vec3(vertices.size())) {
            for (int i = 0; i < vertices.size(); i++) {
                FloatPoint point = vertices.get(i);
                points.position(i)
                        .x(point.x)
                        .y(point.y)
                        .z(point.z);
            }

            points.position(0);

            return b3CreateHull(
                    points,
                    vertices.size(),
                    Math.min(maxVertices, vertices.size())
            );
        }
    }

    private static b3ShapeDef makeShapeDefinition(
            long category,
            long mask,
            boolean sensor,
            boolean hitEvents,
            float density
    ) {
        b3ShapeDef definition = b3DefaultShapeDef();

        b3Filter filter = new b3Filter();
        filter.categoryBits(category);
        filter.maskBits(mask);
        filter.groupIndex(0);

        definition.filter(filter);
        definition.density(density);
        definition.isSensor(sensor);
        definition.enableContactEvents(hitEvents);
        definition.enableHitEvents(hitEvents);

        return definition;
    }

    private static long categoryFor(PropCollisionMode mode) {
        return switch (mode) {
            case NONE -> 0L;
            case PASSIVE -> CATEGORY_PASSIVE_PROP;
            case SOLID -> CATEGORY_SOLID_PROP;
        };
    }

    private static long maskFor(PropCollisionMode mode) {
        return switch (mode) {
            case NONE -> 0L;
            case PASSIVE -> MASK_PASSIVE_PROP;
            case SOLID -> MASK_SOLID_PROP;
        };
    }

    private static int toBodyType(PropMotionMode mode) {
        return switch (mode) {
            case STATIC -> b3_staticBody;
            case MOVING -> b3_kinematicBody;
            case DYNAMIC -> b3_dynamicBody;
        };
    }

    private static b3Vec3 toBoxVec(Vec3 vector) {
        return new b3Vec3()
                .x((float) vector.x)
                .y((float) vector.y)
                .z((float) vector.z);
    }

    private static b3Pos toBoxPos(Vec3 vector) {
        return new b3Pos()
                .x((float) vector.x)
                .y((float) vector.y)
                .z((float) vector.z);
    }

    private static b3Quat toBoxQuat(Quaternionf rotation) {
        Quaternionf q = new Quaternionf(rotation).normalize();
        return new b3Quat()
                .v(new b3Vec3().x(q.x).y(q.y).z(q.z))
                .s(q.w);
    }

    private static Quaternionf fromBoxQuat(b3Quat rotation) {
        b3Vec3 vector = rotation.v();
        return new Quaternionf(
                vector.x(),
                vector.y(),
                vector.z(),
                rotation.s()
        ).normalize();
    }

    private static b3Quat identityQuat() {
        return new b3Quat()
                .v(new b3Vec3().x(0.0f).y(0.0f).z(0.0f))
                .s(1.0f);
    }

    private static boolean sameScale(Vec3 a, Vec3 b) {
        return
                Double.compare(a.x, b.x) == 0 &&
                Double.compare(a.y, b.y) == 0 &&
                Double.compare(a.z, b.z) == 0;
    }

    private static boolean sameTransform(Matrix4f a, Matrix4f b) {
        float[] left = new float[16];
        float[] right = new float[16];

        a.get(left);
        b.get(right);

        for (int i = 0; i < left.length; i++)
            if (Float.compare(left[i], right[i]) != 0)
                return false;

        return true;
    }

    private static boolean sameBox(AABB a, AABB b) {
        return
                Double.compare(a.minX, b.minX) == 0 &&
                Double.compare(a.minY, b.minY) == 0 &&
                Double.compare(a.minZ, b.minZ) == 0 &&
                Double.compare(a.maxX, b.maxX) == 0 &&
                Double.compare(a.maxY, b.maxY) == 0 &&
                Double.compare(a.maxZ, b.maxZ) == 0;
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void close() {
        if (closed)
            return;
        closed = true;

        for (PropBody body : new ArrayList<>(propBodies.values())) {
            writeBackTransform(body, false);
            destroyPropBody(body.prop.getId());
        }
        for (UUID id : new ArrayList<>(entityBodies.keySet()))
            destroyEntityBody(id);
        for (Long key : new ArrayList<>(terrainBodies.keySet()))
            destroyTerrainBody(key);

        b3DestroyWorld(world);

        loadedChunks.clear();
        geometryCache.clear();
        activeProps.clear();
    }


    private static final class PropBody {
        private Prop prop;
        private LevelChunk ownerChunk;
        private final b3BodyId body;
        private final ResourceLocation definitionId;
        private final PropCollisionMode collisionMode;
        private final PropMotionMode motionMode;
        private final Vec3 scale;
        private Matrix4f lastSyncedTransform;

        private PropBody(
                Prop prop,
                LevelChunk ownerChunk,
                b3BodyId body,
                ResourceLocation definitionId,
                PropCollisionMode collisionMode,
                PropMotionMode motionMode,
                Vec3 scale,
                Matrix4f lastSyncedTransform
        ) {
            this.prop = prop;
            this.ownerChunk = ownerChunk;
            this.body = body;
            this.definitionId = definitionId;
            this.collisionMode = collisionMode;
            this.motionMode = motionMode;
            this.scale = scale;
            this.lastSyncedTransform = new Matrix4f(lastSyncedTransform);
        }
    }

    private static final class EntityBody {
        private Entity entity;
        private final b3BodyId body;
        private final AABB localBox;

        private EntityBody(Entity entity, b3BodyId body, AABB localBox) {
            this.entity = entity;
            this.body = body;
            this.localBox = localBox;
        }
    }

    private static final class TerrainBody {
        private final BlockPos position;
        private final BlockState state;
        private final b3BodyId body;

        private TerrainBody(
                BlockPos position,
                BlockState state,
                b3BodyId body
        ) {
            this.position = position.immutable();
            this.state = state;
            this.body = body;
        }
    }

    private static final class GeometryData {
        private final PropGeometry geometry;
        private final double minX;
        private final double minY;
        private final double minZ;
        private final double maxX;
        private final double maxY;
        private final double maxZ;

        private GeometryData(PropGeometry geometry) {
            this.geometry = geometry;

            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;

            for (PropGeometry.Triangle triangle : geometry.getTriangles())
                for (Vec3 vertex : new Vec3[]{
                        triangle.a(),
                        triangle.b(),
                        triangle.c()
                }) {
                    minX = Math.min(minX, vertex.x);
                    minY = Math.min(minY, vertex.y);
                    minZ = Math.min(minZ, vertex.z);
                    maxX = Math.max(maxX, vertex.x);
                    maxY = Math.max(maxY, vertex.y);
                    maxZ = Math.max(maxZ, vertex.z);
                }

            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }
    }

    private record PropOwner(Prop prop, LevelChunk chunk) {}
    private record FloatPoint(float x, float y, float z) {}
    private record PointKey(int x, int y, int z) {}
    private record ImpactKey(UUID propId, UUID entityId) {}
    private record BodyKey(int index1, int world0, int generation) {
        private static BodyKey of(b3BodyId id) {
            return new BodyKey(
                    id.index1(),
                    id.world0(),
                    id.generation()
            );
        }
    }
}
