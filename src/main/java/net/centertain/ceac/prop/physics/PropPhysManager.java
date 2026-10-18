package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.centertain.ceac.prop.enums.PropMotionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.*;

import static cc.modlabs.box3d.global.Box3D.*;
import static net.centertain.ceac.CeacMod.MOD_ID;

@SuppressWarnings("resource")
@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PropPhysManager implements AutoCloseable {
    private static final float FIXED_STEP = 1.0f / 20.0f;
    private static final int SUB_STEPS = 4;

    private static final double ENTITY_CONTACT_SKIN = 0.01;
    private static final double PUSH_CONTACT_EPSILON = 0.025;
    private static final double MAX_PLAYER_PUSH_SPEED = 8.0;
    private static final double PLAYER_IMPACT_MASS = 5.0;

    private static final int PROP_SPATIAL_CELL_SIZE = 8;

    static final int MAX_PROP_HULL_VERTICES = 32;

    private static final long CATEGORY_WORLD = 1L;
    private static final long CATEGORY_SOLID_PROP = 1L << 1;
    private static final long CATEGORY_PASSIVE_PROP = 1L << 2;
    private static final long CATEGORY_ENTITY = 1L << 3;

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

    final ServerLevel level;
    final b3WorldId world;

    final Map<Long, LevelChunk> loadedChunks = new LinkedHashMap<>();

    private final PropBodyManager propBodyManager = new PropBodyManager(this);
    private final TerrainCollisionManager terrainCollisionManager = new TerrainCollisionManager(this);

    private final Map<UUID, EntityBody> entityBodies = new HashMap<>();
    private final Map<BodyKey, EntityBody> entitiesByBody = new HashMap<>();

    private final Map<ResourceLocation, Optional<GeometryData>> geometryCache = new HashMap<>();

    private final Set<UUID> coveredPropIds = new HashSet<>();
    private final Map<UUID, Entity> wantedEntities = new HashMap<>();
    private final Set<ImpactKey> handledEntityImpacts = new HashSet<>();

    private final Map<UUID, PropSpatialEntry> solidPropSpatialEntries = new HashMap<>();
    private final Map<Long, Set<UUID>> propSpatialCells = new HashMap<>();
    private final Set<UUID> spatialSeenProps = new HashSet<>();

    private long spatialQueryStamp;
    private long collisionQueryStamp;

    private boolean closed;

    private PropPhysManager(ServerLevel level) {
        this.level = level;

        b3WorldDef worldDefinition = b3DefaultWorldDef();
        worldDefinition.gravity(new b3Vec3().x(0.0f).y(-9.81f).z(0.0f));
        worldDefinition.hitEventThreshold(0.2f);

        this.world = b3CreateWorld(worldDefinition);
    }


    ServerLevel level() {
        return level;
    }
    b3WorldId world() {
        return world;
    }
    Map<Long, LevelChunk> loadedChunks() {
        return loadedChunks;
    }
    List<Prop> activeProps() {
        return propBodyManager.activeProps();
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

    @SubscribeEvent
    public static void onBlockChanged(BlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;
        PropPhysManager manager = INSTANCES.get(serverLevel);
        if (manager != null && !manager.closed)
            manager.terrainCollisionManager.markTerrainDirtyAround(event.getPos());
    }


    private void onChunkLoaded(LevelChunk chunk) {
        loadedChunks.put(chunk.getPos().toLong(), chunk);
        terrainCollisionManager.onChunkLoaded();
    }

    private void onChunkUnloaded(LevelChunk chunk) {
        long chunkKey = chunk.getPos().toLong();
        loadedChunks.remove(chunkKey);

        for (PropBodyManager.PropBody body : new ArrayList<>(propBodyManager.propBodies().values())) {
            if (body.ownerChunk.getPos().toLong() != chunkKey)
                continue;
            propBodyManager.writeBackTransform(body, false);
            propBodyManager.destroyPropBody(body.prop.getId());
        }

        terrainCollisionManager.onChunkUnloaded();
    }

    private void tick() {
        propBodyManager.synchronizeProps();
        terrainCollisionManager.synchronizeTerrain();
        synchronizeEntities();

        applyMovingBodyGravity();

        for (PropBodyManager.PropBody body : propBodyManager.propBodies().values()) {
            b3Vec3 velocity = b3Body_GetLinearVelocity(body.body);
            body.velocityBeforeStep = new Vec3(
                    velocity.x(),
                    velocity.y(),
                    velocity.z()
            );
        }

        b3World_Step(world, FIXED_STEP, SUB_STEPS);

        processHitEvents();
        propBodyManager.synchronizePropTransforms();
    }

    private void synchronizeEntities() {
        updatePropSpatialIndex();
        wantedEntities.clear();

        for (Entity entity : level.getAllEntities()) {
            if (entity.isRemoved() || entity.isSpectator())
                continue;

            AABB entityBounds = entity.getBoundingBox();
            AABB searchBounds = entityBounds.inflate(1.0);

            int minX = spatialCell(searchBounds.minX);
            int minY = spatialCell(searchBounds.minY);
            int minZ = spatialCell(searchBounds.minZ);
            int maxX = spatialCell(searchBounds.maxX);
            int maxY = spatialCell(searchBounds.maxY);
            int maxZ = spatialCell(searchBounds.maxZ);

            long queryStamp = ++spatialQueryStamp;
            boolean found = false;

            for (int x = minX; x <= maxX && !found; x++) {
                for (int y = minY; y <= maxY && !found; y++) {
                    for (int z = minZ; z <= maxZ && !found; z++) {
                        Set<UUID> candidates = propSpatialCells.get(spatialCellKey(x, y, z));
                        if (candidates == null)
                            continue;

                        for (UUID propId : candidates) {
                            PropSpatialEntry candidate = solidPropSpatialEntries.get(propId);
                            if (candidate == null || candidate.lastQueryStamp == queryStamp)
                                continue;

                            candidate.lastQueryStamp = queryStamp;
                            if (!candidate.interactionBounds.intersects(entityBounds))
                                continue;

                            wantedEntities.put(entity.getUUID(), entity);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }

        Iterator<Map.Entry<UUID, EntityBody>> iterator = entityBodies.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, EntityBody> entry = iterator.next();
            if (wantedEntities.containsKey(entry.getKey()))
                continue;

            EntityBody body = entry.getValue();

            entitiesByBody.remove(BodyKey.of(body.body));
            b3DestroyBody(body.body);
            iterator.remove();
        }
        for (Entity entity : wantedEntities.values())
            synchronizeEntityBody(entity);
    }

    public static Vec3 clipEntityMovement(
            Entity entity,
            Vec3 movement,
            Vec3 requestedMovement
    ) {
        if (!(entity.level() instanceof ServerLevel serverLevel))
            return movement;
        PropPhysManager manager = INSTANCES.get(serverLevel);
        if (manager == null || manager.closed)
            return movement;
        return manager.clipEntityMovementInterval(
                entity,
                movement,
                requestedMovement
        );
    }

    private Vec3 clipEntityMovementInterval(
            Entity entity,
            Vec3 movement,
            Vec3 requestedMovement
    ) {
        if (movement.lengthSqr() < 1.0e-12 && requestedMovement.lengthSqr() < 1.0e-12)
            return movement;

        AABB sweptBounds = PropStepManager.collisionQueryBounds(entity, movement).inflate(PUSH_CONTACT_EPSILON);

        int minX = spatialCell(sweptBounds.minX);
        int minY = spatialCell(sweptBounds.minY);
        int minZ = spatialCell(sweptBounds.minZ);
        int maxX = spatialCell(sweptBounds.maxX);
        int maxY = spatialCell(sweptBounds.maxY);
        int maxZ = spatialCell(sweptBounds.maxZ);

        long stamp = ++collisionQueryStamp;
        List<PropGeometry.Triangle> obstacles = new ArrayList<>(64);

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {
                    Set<UUID> ids = propSpatialCells.get(spatialCellKey(x, y, z));
                    if (ids == null)
                        continue;

                    for (UUID id : ids) {
                        PropSpatialEntry entry = solidPropSpatialEntries.get(id);
                        if (entry == null || entry.lastCollisionQueryStamp == stamp)
                            continue;

                        entry.lastCollisionQueryStamp = stamp;
                        if (!entry.worldBounds.inflate(PUSH_CONTACT_EPSILON).intersects(sweptBounds))
                            continue;

                        obstacles.addAll(entry.triangles);
                    }
                }

        PropStepManager.MovementResult movementResult = PropStepManager.clipMovementWithResult(entity, movement, obstacles);

        return movementResult.movement();
    }

    private void updatePropSpatialIndex() {
        spatialSeenProps.clear();

        for (Prop prop : propBodyManager.activeProps())
            if (updatePropSpatialEntry(prop))
                spatialSeenProps.add(prop.getId());

        Iterator<Map.Entry<UUID, PropSpatialEntry>> iterator = solidPropSpatialEntries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PropSpatialEntry> entry = iterator.next();
            if (spatialSeenProps.contains(entry.getKey()))
                continue;
            removePropSpatialEntry(entry.getKey(), entry.getValue());
            iterator.remove();
        }
    }

    boolean updatePropSpatialEntry(Prop prop) {
        if (prop.getCollisionMode() != PropCollisionMode.SOLID)
            return false;

        PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
        if (definition == null)
            return false;

        GeometryData geometry = getGeometry(definition.getModelId());
        if (geometry == null)
            return false;

        UUID id = prop.getId();
        long transformRevision = prop.getTransformRevision();
        PropSpatialEntry existing = solidPropSpatialEntries.get(id);
        if (existing != null
                && existing.definitionId.equals(prop.getDefinitionId())
                && existing.transformRevision == transformRevision) {
            return true;
        }
        Matrix4f transform = prop.getTransform();
        if (existing != null)
            removePropSpatialEntry(id, existing);
        List<PropGeometry.Triangle> worldTriangles = PropEntityCollision.transformTriangles(geometry.geometry, transform);
        if (worldTriangles.isEmpty())
            return false;

        AABB bounds = PropEntityCollision.boundsOf(worldTriangles);
        AABB interactionBounds = bounds.inflate(1.0);

        int minX = spatialCell(bounds.minX);
        int minY = spatialCell(bounds.minY);
        int minZ = spatialCell(bounds.minZ);
        int maxX = spatialCell(bounds.maxX);
        int maxY = spatialCell(bounds.maxY);
        int maxZ = spatialCell(bounds.maxZ);

        Set<Long> cellKeys = new HashSet<>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {
                    long key = spatialCellKey(x, y, z);
                    cellKeys.add(key);
                    propSpatialCells.computeIfAbsent(key, ignored -> new HashSet<>()).add(id);
                }

        PropSpatialEntry updated = new PropSpatialEntry(
                prop.getDefinitionId(),
                transformRevision,
                interactionBounds,
                bounds,
                cellKeys,
                worldTriangles
        );

        solidPropSpatialEntries.put(id, updated);
        return true;
    }

    private void removePropSpatialEntry(UUID id, PropSpatialEntry entry) {
        for (long key : entry.cellKeys) {
            Set<UUID> bucket = propSpatialCells.get(key);
            if (bucket == null)
                continue;

            bucket.remove(id);

            if (bucket.isEmpty())
                propSpatialCells.remove(key);
        }
    }

    private static int spatialCell(double coordinate) {
        return floor(coordinate / PROP_SPATIAL_CELL_SIZE);
    }

    private static long spatialCellKey(int x, int y, int z) {
        return new BlockPos(x, y, z).asLong();
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

        Vec3 boxVelocity = tickDisplacement.scale(1.0 / FIXED_STEP);
        Vec3 boxStartPosition = currentPosition.subtract(tickDisplacement);
        if (existing == null) {
            AABB hullBox = localBox.inflate(ENTITY_CONTACT_SKIN);
            List<FloatPoint> points = boxPoints(
                    hullBox.minX, hullBox.minY, hullBox.minZ,
                    hullBox.maxX, hullBox.maxY, hullBox.maxZ
            );
            b3HullData hull = createHull(points, 8);
            if (hull == null || hull.isNull())
                return;

            try {
                b3BodyDef bodyDefinition = b3DefaultBodyDef();
                bodyDefinition.type(b3_kinematicBody);
                bodyDefinition.gravityScale(0.0f);
                bodyDefinition.enableSleep(false);
                bodyDefinition._position(toBoxPos(boxStartPosition));
                bodyDefinition.rotation(identityQuat());
                bodyDefinition.linearVelocity(toBoxVec(boxVelocity));

                b3BodyId body = b3CreateBody(world, bodyDefinition);
                b3ShapeDef shapeDefinition = makeShapeDefinition(
                        CATEGORY_ENTITY,
                        MASK_ENTITY,
                        false,
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

        b3Body_SetTransform(
                existing.body,
                toBoxPos(boxStartPosition),
                identityQuat()
        );
        b3Body_SetLinearVelocity(existing.body, toBoxVec(boxVelocity));
    }

    private void destroyEntityBody(UUID id) {
        EntityBody removed = entityBodies.remove(id);
        if (removed == null)
            return;
        entitiesByBody.remove(BodyKey.of(removed.body));
        b3DestroyBody(removed.body);
    }

    private void applyMovingBodyGravity() {
        for (PropBodyManager.PropBody body : propBodyManager.propBodies().values()) {
            if (body.motionMode != PropMotionMode.MOVING)
                continue;
            b3Vec3 velocity = b3Body_GetLinearVelocity(body.body);
            b3Body_SetLinearVelocity(body.body, new b3Vec3()
                    .x(velocity.x())
                    .y(velocity.y() - 9.81f * FIXED_STEP)
                    .z(velocity.z())
            );
        }
    }

    private void processHitEvents() {
        handledEntityImpacts.clear();

        b3ContactEvents events = b3World_GetContactEvents(world);
        if (events.hitCount() <= 0)
            return;

        b3ContactHitEvent hits = events.hitEvents();
        if (hits == null || hits.isNull())
            return;

        for (int i = 0; i < events.hitCount(); i++) {
            b3ContactHitEvent hit = hits.position(i);

            b3BodyId bodyA = b3Shape_GetBody(hit.shapeIdA());
            b3BodyId bodyB = b3Shape_GetBody(hit.shapeIdB());

            BodyKey keyA = BodyKey.of(bodyA);
            BodyKey keyB = BodyKey.of(bodyB);

            PropBodyManager.PropBody propA = propBodyManager.propsByBody().get(keyA);
            PropBodyManager.PropBody propB = propBodyManager.propsByBody().get(keyB);
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
                    -9.81 * FIXED_STEP * prop.gravityScale,
                    0.0
            );

        double propImpactSpeed = incomingPropVelocity.dot(normal);
        double entityImpactSpeed = entity.getDeltaMovement().scale(1.0 / FIXED_STEP).dot(normal);
        double relativeImpactSpeed = propImpactSpeed - entityImpactSpeed;
        if (relativeImpactSpeed <= 0.01)
            return;

        double entityMass = entity instanceof Player ? PLAYER_IMPACT_MASS : effectiveEntityMass(entity);
        double inversePropMass = prop.motionMode == PropMotionMode.DYNAMIC ? 1.0 / Math.max(1.0e-3, prop.mass) : 0.0;
        double inverseEntityMass = 1.0 / entityMass;
        double impulse = (1.0 + prop.restitution) * relativeImpactSpeed / (inversePropMass + inverseEntityMass);
        Vec3 entityVelocityChange = normal.scale(impulse * inverseEntityMass * FIXED_STEP);
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

    public static void applyPlayerPush(
            Player player,
            UUID propId,
            Vec3 incomingVelocity
    ) {
        if (!(player.level() instanceof ServerLevel serverLevel))
            return;

        PropPhysManager manager = INSTANCES.get(serverLevel);
        if (manager == null || manager.closed)
            return;

        manager.applyPlayerPushImpulse(player, propId, incomingVelocity);
    }

    private void applyPlayerPushImpulse(
            Player player,
            UUID propId,
            Vec3 incomingVelocity
    ) {
        if (player.level() != level || player.isRemoved() || player.isSpectator() || player.noPhysics)
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

        PropSpatialEntry entry = solidPropSpatialEntries.get(propId);
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
                incomingVelocity.scale(FIXED_STEP),
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
                    toBoxVec(impulse),
                    toBoxPos(contactPoint),
                    true
            );

            return;
        }
    }


    GeometryData getGeometry(ResourceLocation modelId) {
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

    static AABB worldBounds(GeometryData geometry, Matrix4f transform) {
        double cx = (geometry.minX + geometry.maxX) * 0.5;
        double cy = (geometry.minY + geometry.maxY) * 0.5;
        double cz = (geometry.minZ + geometry.maxZ) * 0.5;

        double ex = (geometry.maxX - geometry.minX) * 0.5;
        double ey = (geometry.maxY - geometry.minY) * 0.5;
        double ez = (geometry.maxZ - geometry.minZ) * 0.5;

        double worldX = transform.m00() * cx + transform.m10() * cy + transform.m20() * cz + transform.m30();
        double worldY = transform.m01() * cx + transform.m11() * cy + transform.m21() * cz + transform.m31();
        double worldZ = transform.m02() * cx + transform.m12() * cy + transform.m22() * cz + transform.m32();

        double extentX = Math.abs(transform.m00()) * ex + Math.abs(transform.m10()) * ey + Math.abs(transform.m20()) * ez;
        double extentY = Math.abs(transform.m01()) * ex + Math.abs(transform.m11()) * ey + Math.abs(transform.m21()) * ez;
        double extentZ = Math.abs(transform.m02()) * ex + Math.abs(transform.m12()) * ey + Math.abs(transform.m22()) * ez;

        return new AABB(
                worldX - extentX,
                worldY - extentY,
                worldZ - extentZ,
                worldX + extentX,
                worldY + extentY,
                worldZ + extentZ
        );
    }

    static List<FloatPoint> scaledUniqueVertices(PropGeometry geometry, Vec3 scale) {
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

    static b3HullData createHull(
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

    @SuppressWarnings("SameParameterValue")
    static b3ShapeDef makeShapeDefinition(
            long category,
            long mask,
            boolean sensor,
            boolean contactEvents,
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
        definition.enableContactEvents(contactEvents);
        definition.enableHitEvents(hitEvents);

        return definition;
    }



    static long categoryFor(PropCollisionMode mode) {
        return switch (mode) {
            case NONE -> 0L;
            case PASSIVE -> CATEGORY_PASSIVE_PROP;
            case SOLID -> CATEGORY_SOLID_PROP;
        };
    }

    static long maskFor(PropCollisionMode mode) {
        return switch (mode) {
            case NONE -> 0L;
            case PASSIVE -> MASK_PASSIVE_PROP;
            case SOLID -> MASK_SOLID_PROP;
        };
    }

    static int toBodyType(PropMotionMode mode) {
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

    static b3Pos toBoxPos(Vec3 vector) {
        return new b3Pos()
                .x((float) vector.x)
                .y((float) vector.y)
                .z((float) vector.z);
    }

    static b3Quat toBoxQuat(Quaternionf rotation) {
        Quaternionf q = new Quaternionf(rotation).normalize();
        return new b3Quat()
                .v(new b3Vec3().x(q.x).y(q.y).z(q.z))
                .s(q.w);
    }

    static Quaternionf fromBoxQuat(b3Quat rotation) {
        b3Vec3 vector = rotation.v();
        return new Quaternionf(
                vector.x(),
                vector.y(),
                vector.z(),
                rotation.s()
        ).normalize();
    }

    static b3Quat identityQuat() {
        return new b3Quat()
                .v(new b3Vec3().x(0.0f).y(0.0f).z(0.0f))
                .s(1.0f);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean sameScale(Vec3 a, Vec3 b) {
        return
                Double.compare(a.x, b.x) == 0 &&
                Double.compare(a.y, b.y) == 0 &&
                Double.compare(a.z, b.z) == 0;
    }

    static boolean sameTransform(Matrix4f a, Matrix4f b) {
        return
                Float.compare(a.m00(), b.m00()) == 0 &&
                Float.compare(a.m01(), b.m01()) == 0 &&
                Float.compare(a.m02(), b.m02()) == 0 &&
                Float.compare(a.m03(), b.m03()) == 0 &&
                Float.compare(a.m10(), b.m10()) == 0 &&
                Float.compare(a.m11(), b.m11()) == 0 &&
                Float.compare(a.m12(), b.m12()) == 0 &&
                Float.compare(a.m13(), b.m13()) == 0 &&
                Float.compare(a.m20(), b.m20()) == 0 &&
                Float.compare(a.m21(), b.m21()) == 0 &&
                Float.compare(a.m22(), b.m22()) == 0 &&
                Float.compare(a.m23(), b.m23()) == 0 &&
                Float.compare(a.m30(), b.m30()) == 0 &&
                Float.compare(a.m31(), b.m31()) == 0 &&
                Float.compare(a.m32(), b.m32()) == 0 &&
                Float.compare(a.m33(), b.m33()) == 0;
    }

    static boolean sameBox(AABB a, AABB b) {
        return
                Double.compare(a.minX, b.minX) == 0 &&
                Double.compare(a.minY, b.minY) == 0 &&
                Double.compare(a.minZ, b.minZ) == 0 &&
                Double.compare(a.maxX, b.maxX) == 0 &&
                Double.compare(a.maxY, b.maxY) == 0 &&
                Double.compare(a.maxZ, b.maxZ) == 0;
    }

    static int floor(double value) {
        return (int) Math.floor(value);
    }

    @Override
    public void close() {
        if (closed)
            return;
        closed = true;

        for (PropBodyManager.PropBody body : new ArrayList<>(propBodyManager.propBodies().values())) {
            propBodyManager.writeBackTransform(body, false);
            propBodyManager.destroyPropBody(body.prop.getId());
        }
        for (UUID id : new ArrayList<>(entityBodies.keySet()))
            destroyEntityBody(id);
        terrainCollisionManager.clear();

        b3DestroyWorld(world);

        loadedChunks.clear();
        geometryCache.clear();

        propBodyManager.activeProps().clear();
        coveredPropIds.clear();
        wantedEntities.clear();
        handledEntityImpacts.clear();

        solidPropSpatialEntries.clear();
        propSpatialCells.clear();
        spatialSeenProps.clear();
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

    static final class GeometryData {
        final PropGeometry geometry;
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

            for (PropGeometry.Triangle triangle : geometry.getTriangles()) {
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

            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }
    }

    private static final class PropSpatialEntry {
        private final ResourceLocation definitionId;
        private final AABB interactionBounds;
        private final Set<Long> cellKeys;
        private final long transformRevision;
        private final AABB worldBounds;
        private long lastCollisionQueryStamp;
        private final List<PropGeometry.Triangle> triangles;

        private long lastQueryStamp;

        private PropSpatialEntry(
                ResourceLocation definitionId,
                long transformRevision,
                AABB interactionBounds,
                AABB worldBounds,
                Set<Long> cellKeys,
                List<PropGeometry.Triangle> triangles
        ) {
            this.definitionId = definitionId;
            this.transformRevision = transformRevision;
            this.interactionBounds = interactionBounds;
            this.worldBounds = worldBounds;
            this.cellKeys = cellKeys;
            this.triangles = List.copyOf(triangles);
        }
    }

    record FloatPoint(float x, float y, float z) {}
    private record PointKey(int x, int y, int z) {}
    private record ImpactKey(UUID propId, UUID entityId) {}
    record BodyKey(int index1, int world0, int generation) {
        static BodyKey of(b3BodyId id) {
            return new BodyKey(
                    id.index1(),
                    id.world0(),
                    id.generation()
            );
        }
    }
}
