package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropCollisionMode;
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
    static final float FIXED_STEP = 1.0f / 20.0f;
    private static final int SUB_STEPS = 4;

    static final double ENTITY_CONTACT_SKIN = 0.01;
    private static final double PUSH_CONTACT_EPSILON = 0.025;

    static final int MAX_PROP_HULL_VERTICES = 32;

    private static final long CATEGORY_WORLD = 1L;
    private static final long CATEGORY_SOLID_PROP = 1L << 1;
    private static final long CATEGORY_PASSIVE_PROP = 1L << 2;
    static final long CATEGORY_ENTITY = 1L << 3;

    private static final long MASK_SOLID_PROP =
            CATEGORY_WORLD |
            CATEGORY_SOLID_PROP |
            CATEGORY_ENTITY;

    private static final long MASK_PASSIVE_PROP = CATEGORY_WORLD;

    static final long MASK_ENTITY =
            CATEGORY_WORLD |
            CATEGORY_SOLID_PROP |
            CATEGORY_ENTITY;

    private static final Map<ServerLevel, PropPhysManager> INSTANCES = new IdentityHashMap<>();

    final ServerLevel level;
    final b3WorldId world;

    final Map<Long, LevelChunk> loadedChunks = new LinkedHashMap<>();

    private final PropBodyManager propBodyManager = new PropBodyManager(this);
    private final TerrainCollisionManager terrainCollisionManager = new TerrainCollisionManager(this);
    private final EntityBodyManager entityBodyManager = new EntityBodyManager(this);
    final PropSpatialIndex propSpatialIndex = new PropSpatialIndex(this);
    private final PropCollisionHandler collisionHandler = new PropCollisionHandler(
            this, propBodyManager, terrainCollisionManager, entityBodyManager, propSpatialIndex
    );

    private final Map<ResourceLocation, Optional<GeometryData>> geometryCache = new HashMap<>();

    private final Map<UUID, Entity> wantedEntities = new HashMap<>();

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
        return floor(coordinate / PropSpatialIndex.PROP_SPATIAL_CELL_SIZE);
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

    static List<FloatPoint> boxPoints(
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

    static b3Vec3 toBoxVec(Vec3 vector) {
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
        entityBodyManager.clear();
        terrainCollisionManager.clear();

        b3DestroyWorld(world);

        loadedChunks.clear();
        geometryCache.clear();

        propBodyManager.activeProps().clear();
        wantedEntities.clear();
        collisionHandler.clear();

        propSpatialIndex.clear();
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

    record FloatPoint(float x, float y, float z) {}
    private record PointKey(int x, int y, int z) {}
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
