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
import net.centertain.ceac.prop.network.SyncPropTransformPacket;
import net.centertain.ceac.prop.server.PropCapabilities;
import net.centertain.ceac.prop.server.PropManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    private static final double ENTITY_CONTACT_SKIN = 0.01;
    private static final double PUSH_CONTACT_EPSILON = 0.025;
    private static final double PLAYER_PUSH_MASS = 80.0;

    private static final double TERRAIN_BOUNDS_EPSILON = 1.0e-4;
    private static final int TERRAIN_VALIDATION_INTERVAL = 20;
    private static final int TERRAIN_VALIDATION_BUDGET_PER_TICK = 128;
    private static final int PROP_SPATIAL_CELL_SIZE = 8;

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

    private final Map<LevelChunk, List<SyncPropTransformPacket.Transform>> pendingTransformUpdates = new LinkedHashMap<>();

    private final List<Prop> activeProps = new ArrayList<>();
    private final Set<UUID> seenPropIds = new HashSet<>();
    private final Set<UUID> coveredPropIds = new HashSet<>();
    private final Map<UUID, Entity> wantedEntities = new HashMap<>();
    private final Set<ImpactKey> handledEntityImpacts = new HashSet<>();

    private long playerPushImpulseTick = Long.MIN_VALUE;
    private final Set<ImpactKey> playerPushesThisTick = new HashSet<>();
    private final Map<ImpactKey, PendingPlayerPush> pendingPlayerPushes = new LinkedHashMap<>();

    private final Map<UUID, TerrainCoverage> terrainCoverageByProp = new HashMap<>();
    private final Map<Long, Integer> terrainReferenceCounts = new HashMap<>();
    private final Map<Long, Set<Long>> terrainKeysByChunk = new HashMap<>();

    private final Set<Long> dirtyTerrainKeys = new HashSet<>();
    private final ArrayDeque<Long> terrainValidationQueue = new ArrayDeque<>();

    private final Map<UUID, PropSpatialEntry> solidPropSpatialEntries = new HashMap<>();
    private final Map<Long, Set<UUID>> propSpatialCells = new HashMap<>();
    private final Set<UUID> spatialSeenProps = new HashSet<>();

    private int terrainValidationTicks;
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
            manager.markTerrainDirtyAround(event.getPos());
    }

    private void markTerrainDirtyAround(BlockPos position) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                for (int dz = -1; dz <= 1; dz++) {
                    long key = position.offset(dx, dy, dz).asLong();
                    if (terrainReferenceCounts.containsKey(key))
                        dirtyTerrainKeys.add(key);
                }
    }


    private void onChunkLoaded(LevelChunk chunk) {
        loadedChunks.put(chunk.getPos().toLong(), chunk);
        Set<Long> keys = terrainKeysByChunk.get(chunk.getPos().toLong());
        if (keys != null)
            dirtyTerrainKeys.addAll(keys);
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

        for (PropBody body : propBodies.values()) {
            b3Vec3 velocity = b3Body_GetLinearVelocity(body.body);
            body.velocityBeforeStep = new Vec3(
                    velocity.x(),
                    velocity.y(),
                    velocity.z()
            );
        }

        b3World_Step(world, FIXED_STEP, SUB_STEPS);

        processHitEvents();
        applyPendingPlayerPushes();
        synchronizePropTransforms();
    }

    private void synchronizeProps() {
        seenPropIds.clear();
        activeProps.clear();

        for (LevelChunk chunk : loadedChunks.values()) {
            PropManager manager = PropCapabilities.get(chunk);
            for (Prop prop : manager.getProps().values()) {
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
            propsByBody.remove(BodyKey.of(body.body));
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
        boolean rebuild = existing == null
                || !existing.definitionId.equals(prop.getDefinitionId())
                || existing.collisionMode != prop.getCollisionMode()
                || existing.motionMode != prop.getMotionMode();
        if (!rebuild && existing.lastObservedTransformRevision != transformRevision)
            rebuild = !sameScale(existing.scale, prop.getScale());
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
            return;
        }

        existing.prop = prop;
        existing.ownerChunk = ownerChunk;
        if (existing.lastObservedTransformRevision == transformRevision)
            return;

        Matrix4f currentTransform = prop.getTransform();

        b3Body_SetTransform(
                existing.body,
                toBoxPos(prop.getPosition()),
                toBoxQuat(prop.getRotationQuat())
        );

        existing.lastSyncedTransform.set(currentTransform);
        existing.lastObservedTransformRevision = transformRevision;
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

            double propMass = Math.max(1.0e-3, (double) density * hull.volume());

            boolean sensor = prop.getCollisionMode() == PropCollisionMode.NONE;
            long category = categoryFor(prop.getCollisionMode());
            long mask = maskFor(prop.getCollisionMode());

            b3ShapeDef shapeDefinition = makeShapeDefinition(
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

    private void destroyPropBody(UUID id) {
        PropBody removed = propBodies.remove(id);
        if (removed == null)
            return;
        propsByBody.remove(BodyKey.of(removed.body));
        b3DestroyBody(removed.body);
    }

    private void synchronizePropTransforms() {
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

    private void writeBackTransform(PropBody body, boolean sendUpdate) {
        Matrix4f before = body.transformBeforeScratch;
        Matrix4f after = body.transformAfterScratch;

        body.prop.copyTransformTo(before);

        b3Pos position = b3Body_GetPosition(body.body);
        b3Quat rotation = b3Body_GetRotation(body.body);
        Quaternionf quaternion = fromBoxQuat(rotation);

        body.prop.setPosition(new Vec3(
                position.x(),
                position.y(),
                position.z()
        ));
        body.prop.setRotation(quaternion);
        body.lastObservedTransformRevision = body.prop.getTransformRevision();

        body.prop.copyTransformTo(after);
        body.lastSyncedTransform.set(after);
        if (sameTransform(before, after))
            return;

        updatePropSpatialEntry(body.prop);
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

    private void synchronizeTerrain() {
        coveredPropIds.clear();

        for (Prop prop : activeProps) {
            if (prop.getCollisionMode() == PropCollisionMode.NONE)
                continue;

            PropDefinition definition = PropDefinitions.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            GeometryData geometry = getGeometry(definition.getModelId());
            if (geometry == null)
                continue;

            UUID propId = prop.getId();
            long transformRevision = prop.getTransformRevision();
            TerrainCoverage previous = terrainCoverageByProp.get(propId);
            if (
                    previous != null &&
                    previous.definitionId().equals(prop.getDefinitionId()) &&
                    previous.transformRevision() == transformRevision
            ) {
                coveredPropIds.add(propId);
                continue;
            }

            AABB bounds = worldBounds(geometry, prop.getTransform());
            if (
                    !Double.isFinite(bounds.minX)
                    || !Double.isFinite(bounds.minY)
                    || !Double.isFinite(bounds.minZ)
                    || !Double.isFinite(bounds.maxX)
                    || !Double.isFinite(bounds.maxY)
                    || !Double.isFinite(bounds.maxZ)
                    || bounds.minX > bounds.maxX
                    || bounds.minY > bounds.maxY
                    || bounds.minZ > bounds.maxZ
            )
                continue;

            int minX = floor(bounds.minX - TERRAIN_BOUNDS_EPSILON);
            int minY = Math.max(level.getMinBuildHeight(), floor(bounds.minY - TERRAIN_BOUNDS_EPSILON));
            int minZ = floor(bounds.minZ - TERRAIN_BOUNDS_EPSILON);
            int maxX = floor(bounds.maxX + TERRAIN_BOUNDS_EPSILON);
            int maxY = Math.min(level.getMaxBuildHeight() - 1, floor(bounds.maxY + TERRAIN_BOUNDS_EPSILON));
            int maxZ = floor(bounds.maxZ + TERRAIN_BOUNDS_EPSILON);
            if (minY > maxY)
                continue;

            coveredPropIds.add(propId);
            if (previous != null && previous.matches(minX, minY, minZ, maxX, maxY, maxZ)) {
                terrainCoverageByProp.put(propId, new TerrainCoverage(
                        minX, minY, minZ,
                        maxX, maxY, maxZ,
                        prop.getDefinitionId(),
                        transformRevision,
                        previous.keys()
                ));
                continue;
            }

            Set<Long> nextKeys = new HashSet<>();
            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    for (int z = minZ; z <= maxZ; z++)
                        nextKeys.add(new BlockPos(x, y, z).asLong());
            if (previous != null)
                for (Long key : previous.keys())
                    if (!nextKeys.contains(key))
                        releaseTerrainKey(key);
            for (Long key : nextKeys)
                if (previous == null || !previous.keys().contains(key))
                    retainTerrainKey(key);

            terrainCoverageByProp.put(propId, new TerrainCoverage(
                    minX, minY, minZ,
                    maxX, maxY, maxZ,
                    prop.getDefinitionId(),
                    transformRevision,
                    nextKeys
            ));
        }

        Iterator<Map.Entry<UUID, TerrainCoverage>> coverageIterator = terrainCoverageByProp.entrySet().iterator();

        while (coverageIterator.hasNext()) {
            Map.Entry<UUID, TerrainCoverage> entry = coverageIterator.next();
            if (coveredPropIds.contains(entry.getKey()))
                continue;
            for (Long key : entry.getValue().keys())
                releaseTerrainKey(key);
            coverageIterator.remove();
        }

        validateTerrainBodiesIncrementally();

        Iterator<Long> dirtyIterator = dirtyTerrainKeys.iterator();
        while (dirtyIterator.hasNext()) {
            long key = dirtyIterator.next();
            if (terrainReferenceCounts.containsKey(key))
                refreshTerrainBody(key);
            else
                destroyTerrainBody(key);
            dirtyIterator.remove();
        }
    }

    private void validateTerrainBodiesIncrementally() {
        if (terrainValidationQueue.isEmpty()) {
            if (++terrainValidationTicks < TERRAIN_VALIDATION_INTERVAL)
                return;
            terrainValidationTicks = 0;
            terrainValidationQueue.addAll(terrainBodies.keySet());
        }

        int processed = 0;

        while (processed < TERRAIN_VALIDATION_BUDGET_PER_TICK && !terrainValidationQueue.isEmpty()) {
            long key = terrainValidationQueue.removeFirst();
            processed++;
            TerrainBody cached = terrainBodies.get(key);
            if (cached == null)
                continue;

            BlockPos position = BlockPos.of(key);
            BlockState currentState = level.getBlockState(position);
            if (!cached.state.equals(currentState))
                dirtyTerrainKeys.add(key);
        }
    }

    private void retainTerrainKey(long key) {
        int references = terrainReferenceCounts.getOrDefault(key, 0);
        terrainReferenceCounts.put(key, references + 1);
        if (references != 0)
            return;

        BlockPos position = BlockPos.of(key);
        long chunkKey = new ChunkPos(position).toLong();
        terrainKeysByChunk.computeIfAbsent(chunkKey, ignored -> new HashSet<>()).add(key);
        dirtyTerrainKeys.add(key);
    }

    private void releaseTerrainKey(long key) {
        Integer references = terrainReferenceCounts.get(key);
        if (references == null)
            return;
        if (references > 1) {
            terrainReferenceCounts.put(key, references - 1);
            return;
        }

        terrainReferenceCounts.remove(key);
        dirtyTerrainKeys.remove(key);
        destroyTerrainBody(key);

        BlockPos position = BlockPos.of(key);
        long chunkKey = new ChunkPos(position).toLong();

        Set<Long> chunkKeys = terrainKeysByChunk.get(chunkKey);
        if (chunkKeys != null) {
            chunkKeys.remove(key);
            if (chunkKeys.isEmpty())
                terrainKeysByChunk.remove(chunkKey);
        }
    }

    private void refreshTerrainBody(long key) {
        destroyTerrainBody(key);
        if (terrainReferenceCounts.containsKey(key))
            ensureTerrainBody(key);
    }

    @SuppressWarnings("deprecation")
    private void ensureTerrainBody(long key) {
        if (terrainBodies.containsKey(key))
            return;

        BlockPos position = BlockPos.of(key);
        if (!level.hasChunkAt(position))
            return;

        BlockState state = level.getBlockState(position);
        TerrainBody created = createTerrainBody(position, state);
        terrainBodies.put(key, created);
        if (created.body != null)
            terrainByBody.put(BodyKey.of(created.body), created);
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

    public static Vec3 clipEntityMovement(Entity entity, Vec3 movement) {
        return clipEntityMovement(entity, movement, movement);
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
        if (movement.lengthSqr() < 1.0e-12)
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
        List<PropBody> candidateProps = new ArrayList<>();

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
                        PropBody body = propBodies.get(id);
                        if (
                                body != null &&
                                body.motionMode == PropMotionMode.DYNAMIC &&
                                body.prop.getCollisionMode() == PropCollisionMode.SOLID
                        )
                            candidateProps.add(body);
                    }
                }

        Vec3 clippedMovement = PropStepManager.clipMovement(entity, movement, obstacles);

        applyPlayerPushImpulse(
                entity,
                requestedMovement,
                movement,
                clippedMovement,
                candidateProps
        );

        return clippedMovement;
    }

    private void applyPlayerPushImpulse(
            Entity entity,
            Vec3 requestedMovement,
            Vec3 vanillaMovement,
            Vec3 clippedMovement,
            List<PropBody> candidates
    ) {
        if (!(entity instanceof Player) || entity.noPhysics || entity.isSpectator())
            return;

        Vec3 blockedHorizontal = new Vec3(
                vanillaMovement.x - clippedMovement.x,
                0.0,
                vanillaMovement.z - clippedMovement.z
        );

        if (blockedHorizontal.lengthSqr() >= 1.0e-8) {
            Vec3 direction = blockedHorizontal.normalize();

            PropBody sideProp = findSidePushTarget(
                    entity.getBoundingBox(),
                    vanillaMovement,
                    direction,
                    candidates
            );

            if (sideProp != null) {
                double intendedSpeed = requestedMovement.x * direction.x + requestedMovement.z * direction.z;
                intendedSpeed /= FIXED_STEP;
                if (intendedSpeed > 1.0e-4)
                    applyPushTowardSpeed(
                            entity,
                            sideProp,
                            direction,
                            intendedSpeed,
                            0.75,
                            1.5
                    );

                return;
            }
        }

        Vec3 horizontalIntent = new Vec3(requestedMovement.x, 0.0, requestedMovement.z);
        if (horizontalIntent.lengthSqr() < 1.0e-8)
            return;

        PropBody support = findSupportingProp(entity.getBoundingBox(), candidates);
        if (support == null)
            return;

        Vec3 direction = horizontalIntent.normalize();
        double intendedSpeed = horizontalIntent.length() / FIXED_STEP;
        applyPushTowardSpeed(
                entity,
                support,
                direction,
                intendedSpeed,
                0.06,
                0.16
        );
    }

    private PropBody findSidePushTarget(
            AABB playerBox,
            Vec3 movement,
            Vec3 pushDirection,
            List<PropBody> candidates
    ) {
        AABB sweptPlayerBox = playerBox.expandTowards(movement).inflate(PUSH_CONTACT_EPSILON);

        double playerCenterX = (playerBox.minX + playerBox.maxX) * 0.5;
        double playerCenterZ = (playerBox.minZ + playerBox.maxZ) * 0.5;

        PropBody selected = null;
        double nearestAlignment = Double.POSITIVE_INFINITY;

        for (PropBody candidate : candidates) {
            PropSpatialEntry entry = solidPropSpatialEntries.get(candidate.prop.getId());
            if (entry == null || !entry.worldBounds.inflate(PUSH_CONTACT_EPSILON).intersects(sweptPlayerBox))
                continue;

            double propCenterX = (entry.worldBounds.minX + entry.worldBounds.maxX) * 0.5;
            double propCenterZ = (entry.worldBounds.minZ + entry.worldBounds.maxZ) * 0.5;
            double alignment =
                    (propCenterX - playerCenterX) * pushDirection.x +
                    (propCenterZ - playerCenterZ) * pushDirection.z;
            if (alignment < -PUSH_CONTACT_EPSILON || alignment >= nearestAlignment)
                continue;

            nearestAlignment = alignment;
            selected = candidate;
        }

        return selected;
    }

    private PropBody findSupportingProp(
            AABB playerBox,
            List<PropBody> candidates
    ) {
        PropBody selected = null;
        double bestOverlapArea = 0.0;

        for (PropBody candidate : candidates) {
            PropSpatialEntry entry = solidPropSpatialEntries.get(candidate.prop.getId());
            if (entry == null)
                continue;

            AABB bounds = entry.worldBounds;
            double feetGap = playerBox.minY - bounds.maxY;
            if (Math.abs(feetGap) > PUSH_CONTACT_EPSILON)
                continue;

            double overlapX = Math.min(playerBox.maxX, bounds.maxX) - Math.max(playerBox.minX, bounds.minX);
            double overlapZ = Math.min(playerBox.maxZ, bounds.maxZ) - Math.max(playerBox.minZ, bounds.minZ);
            if (overlapX <= 0.0 || overlapZ <= 0.0)
                continue;

            double overlapArea = overlapX * overlapZ;
            if (overlapArea <= bestOverlapArea)
                continue;

            bestOverlapArea = overlapArea;
            selected = candidate;
        }

        return selected;
    }

    private void applyPushTowardSpeed(
            Entity entity,
            PropBody prop,
            Vec3 direction,
            double intendedSpeed,
            double response,
            double maxDeltaVelocity
    ) {
        if (intendedSpeed <= 1.0e-4 || prop.mass <= 1.0e-6)
            return;

        long gameTime = level.getGameTime();
        if (playerPushImpulseTick != gameTime) {
            playerPushImpulseTick = gameTime;
            playerPushesThisTick.clear();
        }

        ImpactKey key = new ImpactKey(prop.prop.getId(), entity.getUUID());
        if (playerPushesThisTick.contains(key))
            return;

        b3Vec3 current = b3Body_GetLinearVelocity(prop.body);
        double currentSpeed =
                current.x() * direction.x +
                current.y() * direction.y +
                current.z() * direction.z;
        if (intendedSpeed - currentSpeed <= 1.0e-4)
            return;

        playerPushesThisTick.add(key);
        pendingPlayerPushes.put(key, new PendingPlayerPush(
                prop.prop.getId(),
                direction,
                intendedSpeed,
                response,
                maxDeltaVelocity
        ));
    }

    private void applyPendingPlayerPushes() {
        try {
            for (PendingPlayerPush push : pendingPlayerPushes.values()) {
                PropBody prop = propBodies.get(push.propId());
                if (
                        prop == null ||
                        prop.motionMode != PropMotionMode.DYNAMIC ||
                        prop.mass <= 1.0e-6
                )
                    continue;

                b3Vec3 current = b3Body_GetLinearVelocity(prop.body);
                double currentSpeed =
                        current.x() * push.direction().x +
                        current.y() * push.direction().y +
                        current.z() * push.direction().z;
                double speedDifference = push.intendedSpeed() - currentSpeed;
                if (speedDifference <= 1.0e-4)
                    continue;

                double reducedMass = PLAYER_PUSH_MASS * prop.mass / (PLAYER_PUSH_MASS + prop.mass);
                double impulse = reducedMass * speedDifference * push.response();
                double deltaVelocity = Math.min(push.maxDeltaVelocity(), impulse / prop.mass);
                if (deltaVelocity <= 1.0e-5)
                    continue;

                b3Body_SetLinearVelocity(prop.body, new b3Vec3()
                        .x((float) (current.x() + push.direction().x * deltaVelocity))
                        .y((float) (current.y() + push.direction().y * deltaVelocity))
                        .z((float) (current.z() + push.direction().z * deltaVelocity))
                );
            }
        } finally {
            pendingPlayerPushes.clear();
        }
    }

    private void updatePropSpatialIndex() {
        spatialSeenProps.clear();

        for (Prop prop : activeProps)
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

    private boolean updatePropSpatialEntry(Prop prop) {
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
        if (tickDisplacement.lengthSqr() > 4.0)
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
        for (PropBody body : propBodies.values()) {
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
                    0.0);

        double propImpactSpeed = incomingPropVelocity.dot(normal);
        if (propImpactSpeed <= 0.01)
            return;

        double entityMass = effectiveEntityMass(entity);
        double inversePropMass = prop.motionMode == PropMotionMode.DYNAMIC ? 1.0 / prop.mass : 0.0;
        double impulse = (1.0 + prop.restitution) * propImpactSpeed / (inversePropMass + 1.0 / entityMass);
        Vec3 entityVelocityChange = normal.scale(impulse / entityMass * FIXED_STEP);
        entity.push(
                entityVelocityChange.x,
                entityVelocityChange.y,
                entityVelocityChange.z
        );
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

        float intoObstacle = velocity.x() * ox + velocity.y() * oy + velocity.z() * oz;
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

    @SuppressWarnings("SameParameterValue")
    private static b3ShapeDef makeShapeDefinition(
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
        seenPropIds.clear();
        coveredPropIds.clear();
        wantedEntities.clear();
        handledEntityImpacts.clear();

        terrainCoverageByProp.clear();
        terrainReferenceCounts.clear();
        terrainKeysByChunk.clear();
        dirtyTerrainKeys.clear();
        terrainValidationQueue.clear();

        solidPropSpatialEntries.clear();
        propSpatialCells.clear();
        spatialSeenProps.clear();
    }


    private static final class PropBody {
        private Prop prop;
        private LevelChunk ownerChunk;
        private final b3BodyId body;
        private final ResourceLocation definitionId;
        private final PropCollisionMode collisionMode;
        private final PropMotionMode motionMode;
        private final Vec3 scale;
        private final double mass;
        private final double restitution;
        private final double gravityScale;
        private final Matrix4f lastSyncedTransform;
        private final Matrix4f lastNetworkedTransform;

        private Vec3 velocityBeforeStep = Vec3.ZERO;
        private long lastObservedTransformRevision;

        private final Matrix4f transformBeforeScratch = new Matrix4f();
        private final Matrix4f transformAfterScratch = new Matrix4f();

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

    private record TerrainBody(BlockPos position, BlockState state, b3BodyId body) {
        private TerrainBody {
            position = position.immutable();
        }
    }
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
    private record TerrainCoverage(
            int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ,
            ResourceLocation definitionId,
            long transformRevision,
            Set<Long> keys
    ) {
        private boolean matches(
                int otherMinX, int otherMinY, int otherMinZ,
                int otherMaxX, int otherMaxY, int otherMaxZ
        ) {
            return
                    minX == otherMinX &&
                    minY == otherMinY &&
                    minZ == otherMinZ &&
                    maxX == otherMaxX &&
                    maxY == otherMaxY &&
                    maxZ == otherMaxZ;
        }
    }
    private record PendingPlayerPush(
            UUID propId,
            Vec3 direction,
            double intendedSpeed,
            double response,
            double maxDeltaVelocity
    ) {}
}
