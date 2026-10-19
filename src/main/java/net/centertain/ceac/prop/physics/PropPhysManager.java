package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropMotionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

import java.util.*;

import static cc.modlabs.box3d.global.Box3D.*;
import static net.centertain.ceac.CeacMod.MOD_ID;

@SuppressWarnings("resource")
@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PropPhysManager implements AutoCloseable {
    static final float FIXED_STEP = 1.0f / 20.0f;
    private static final int SUB_STEPS = 4;

    static final double ENTITY_CONTACT_SKIN = 0.01;
    private static final double PUSH_CONTACT_EPSILON = 0.025;

    static final long CATEGORY_ENTITY = 1L << 3;
    static final long MASK_ENTITY = 1L | (1L << 1) | CATEGORY_ENTITY;

    private static final Map<ServerLevel, PropPhysManager> INSTANCES = new IdentityHashMap<>();

    final ServerLevel level;
    final b3WorldId world;

    final Map<Long, LevelChunk> loadedChunks = new LinkedHashMap<>();

    private final Map<ResourceLocation, Optional<PropPhysGeometry.GeometryData>> geometryCache = new HashMap<>();
    private final Map<ResourceLocation, Optional<b3MeshData>> collisionMeshCache = new HashMap<>();

    private final Map<UUID, Entity> wantedEntities = new HashMap<>();

    private final PropBodyManager propBodyManager = new PropBodyManager(this);
    private final TerrainCollisionManager terrainCollisionManager = new TerrainCollisionManager(this);
    private final EntityBodyManager entityBodyManager = new EntityBodyManager(this);
    final PropSpatialIndex propSpatialIndex = new PropSpatialIndex(this);
    private final PropCollisionHandler collisionHandler = new PropCollisionHandler(
            this, propBodyManager, terrainCollisionManager, entityBodyManager, propSpatialIndex
    );

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
        terrainCollisionManager.onChunkLoaded(chunk.getPos());
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

        terrainCollisionManager.onChunkUnloaded(chunk.getPos());
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

        collisionHandler.processHitEvents();
        propBodyManager.synchronizePropTransforms();
    }

    private void synchronizeEntities() {
        propSpatialIndex.updatePropSpatialIndex();
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

            long queryStamp = propSpatialIndex.nextSpatialQueryStamp();
            boolean found = false;

            for (int x = minX; x <= maxX && !found; x++) {
                for (int y = minY; y <= maxY && !found; y++) {
                    for (int z = minZ; z <= maxZ && !found; z++) {
                        Set<UUID> candidates = propSpatialIndex.cells().get(spatialCellKey(x, y, z));
                        if (candidates == null)
                            continue;

                        for (UUID propId : candidates) {
                            PropSpatialIndex.PropSpatialEntry candidate = propSpatialIndex.entry(propId);
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

        entityBodyManager.synchronizeEntities(wantedEntities.values());
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

        long stamp = propSpatialIndex.nextCollisionQueryStamp();
        List<PropGeometry.Triangle> obstacles = new ArrayList<>(64);

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {
                    Set<UUID> ids = propSpatialIndex.cells().get(spatialCellKey(x, y, z));
                    if (ids == null)
                        continue;

                    for (UUID id : ids) {
                        PropSpatialIndex.PropSpatialEntry entry = propSpatialIndex.entry(id);
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

    private static int spatialCell(double coordinate) {
        return PropPhysGeometry.floor(coordinate / PropSpatialIndex.PROP_SPATIAL_CELL_SIZE);
    }

    private static long spatialCellKey(int x, int y, int z) {
        return new BlockPos(x, y, z).asLong();
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

        manager.collisionHandler.applyPlayerPushImpulse(player, propId, incomingVelocity);
    }


    PropPhysGeometry.GeometryData getGeometry(ResourceLocation modelId) {
        Optional<PropPhysGeometry.GeometryData> cached = geometryCache.get(modelId);
        if (cached != null)
            return cached.orElse(null);

        PropGeometry geometry = PropGeometry.get(level.getServer().getResourceManager(), modelId);
        if (geometry == null) {
            geometryCache.put(modelId, Optional.empty());
            return null;
        }

        PropPhysGeometry.GeometryData data = new PropPhysGeometry.GeometryData(geometry);
        geometryCache.put(modelId, Optional.of(data));
        return data;
    }

    b3MeshData getCollisionMesh(
            ResourceLocation modelId,
            PropPhysGeometry.GeometryData geometry
    ) {
        Optional<b3MeshData> cached = collisionMeshCache.get(modelId);
        if (cached != null)
            return cached.orElse(null);

        b3MeshData mesh = PropPhysGeometry.createCollisionMesh(geometry.geometry);
        if (mesh != null && mesh.isNull())
            mesh = null;

        collisionMeshCache.put(modelId, Optional.ofNullable(mesh));

        return mesh;
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
        entityBodyManager.clear();
        terrainCollisionManager.clear();

        b3DestroyWorld(world);

        for (Optional<b3MeshData> cached : collisionMeshCache.values()) {
            if (cached.isEmpty())
                continue;
            b3MeshData mesh = cached.get();
            if (!mesh.isNull())
                b3DestroyMesh(mesh);
        }
        collisionMeshCache.clear();

        loadedChunks.clear();
        geometryCache.clear();

        propBodyManager.activeProps().clear();
        wantedEntities.clear();
        collisionHandler.clear();

        propSpatialIndex.clear();
    }


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
