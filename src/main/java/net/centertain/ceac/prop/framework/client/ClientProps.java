package net.centertain.ceac.prop.framework.client;

import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.PropDefinition;
import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.framework.PropGeometry;
import net.centertain.ceac.prop.framework.client.render.PropLightingSolver;
import net.centertain.ceac.prop.framework.enums.PropCollisionMode;
import net.centertain.ceac.prop.framework.enums.PropMotionMode;
import net.centertain.ceac.prop.framework.network.PropPushIntentPacket;
import net.centertain.ceac.prop.framework.physics.PropEntityCollision;
import net.centertain.ceac.prop.framework.physics.PropStepManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class ClientProps {
    private static final long INTERPOLATION_DURATION_NANOS = 50_000_000L;
    private static final float SNAP_DISTANCE_SQUARED = 64.0f;

    private static final Matrix4f COLLISION_TRANSFORM = new Matrix4f();
    private static final List<PropGeometry.Triangle> COLLISION_TRIANGLES_SCRATCH = new ArrayList<>(256);
    private static final List<PushCandidate> PUSH_CANDIDATES_SCRATCH = new ArrayList<>(16);
    private static final Map<UUID, PushIntent> PENDING_PUSH_INTENTS = new HashMap<>();

    private static final Map<Long, Map<UUID, Prop>> PROPS_BY_CHUNK = new HashMap<>();
    private static final Map<UUID, Long> CHUNK_BY_ID = new HashMap<>();
    private static final Map<UUID, TransformInterpolation> INTERPOLATIONS = new HashMap<>();

    private ClientProps() {}

    public static void put(@NotNull Prop prop) {
        indexProp(prop);
        INTERPOLATIONS.put(prop.getId(), new TransformInterpolation(prop));
    }

    private static void indexProp(Prop prop) {
        long newChunkKey = new ChunkPos(prop.getBlockPosition()).toLong();
        Long oldChunkKey = CHUNK_BY_ID.put(prop.getId(), newChunkKey);

        if (oldChunkKey != null && oldChunkKey != newChunkKey) {
            Map<UUID, Prop> oldChunk = PROPS_BY_CHUNK.get(oldChunkKey);
            if (oldChunk != null) {
                oldChunk.remove(prop.getId());
                if (oldChunk.isEmpty())
                    PROPS_BY_CHUNK.remove(oldChunkKey);
            }
        }

        PROPS_BY_CHUNK
                .computeIfAbsent(newChunkKey, ignored -> new HashMap<>())
                .put(prop.getId(), prop);
    }

    @SuppressWarnings("unused")
    private static void moveToChunk(Prop prop, long oldChunkKey, long newChunkKey) {
        Map<UUID, Prop> oldChunk = PROPS_BY_CHUNK.get(oldChunkKey);
        if (oldChunk != null) {
            oldChunk.remove(prop.getId());
            if (oldChunk.isEmpty())
                PROPS_BY_CHUNK.remove(oldChunkKey);
        }

        CHUNK_BY_ID.put(prop.getId(), newChunkKey);
        PROPS_BY_CHUNK
                .computeIfAbsent(newChunkKey, ignored -> new HashMap<>())
                .put(prop.getId(), prop);
    }

    public static boolean remove(UUID id) {
        PropLightingSolver.removePropCache(id);
        INTERPOLATIONS.remove(id);

        Long chunkKey = CHUNK_BY_ID.remove(id);
        if (chunkKey == null)
            return false;

        Map<UUID, Prop> chunkProps = PROPS_BY_CHUNK.get(chunkKey);
        if (chunkProps == null)
            return false;

        boolean removed = chunkProps.remove(id) != null;
        if (chunkProps.isEmpty())
            PROPS_BY_CHUNK.remove(chunkKey);

        return removed;
    }

    public static Collection<Prop> getInChunk(int chunkX, int chunkZ) {
        Map<UUID, Prop> props = PROPS_BY_CHUNK.get(ChunkPos.asLong(chunkX, chunkZ));
        return props == null ? List.of() : props.values();
    }

    public static void replaceChunk(
            int chunkX,
            int chunkZ,
            @NotNull Collection<Prop> props
    ) {
        removeChunk(chunkX, chunkZ);
        props.forEach(ClientProps::put);
    }

    public static void removeChunk(int chunkX, int chunkZ) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        Map<UUID, Prop> removed = PROPS_BY_CHUNK.remove(key);
        if (removed != null)
            for (UUID id : removed.keySet()) {
                PropLightingSolver.removePropCache(id);
                CHUNK_BY_ID.remove(id, key);
                INTERPOLATIONS.remove(id);
            }
    }

    public static void clear() {
        PROPS_BY_CHUNK.clear();
        CHUNK_BY_ID.clear();
        INTERPOLATIONS.clear();
        PENDING_PUSH_INTENTS.clear();
        PropLightingSolver.clearPropCaches();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean updateTransform(
            UUID id,
            float x,
            float y,
            float z,
            float qx,
            float qy,
            float qz,
            float qw
    ) {
        Long oldChunkKey = CHUNK_BY_ID.get(id);
        if (oldChunkKey == null)
            return false;

        Map<UUID, Prop> chunkProps = PROPS_BY_CHUNK.get(oldChunkKey);
        if (chunkProps == null)
            return false;

        Prop prop = chunkProps.get(id);
        if (prop == null)
            return false;

        long nowNanos = System.nanoTime();
        TransformInterpolation interpolation = INTERPOLATIONS.computeIfAbsent(
                id, ignored -> new TransformInterpolation(prop)
        );
        interpolation.setTarget(x, y, z, qx, qy, qz, qw, nowNanos);

        prop.setPosition(new Vec3(x, y, z));
        prop.setRotation(new Quaternionf(qx, qy, qz, qw));

        long newChunkKey = new ChunkPos(prop.getBlockPosition()).toLong();
        if (newChunkKey != oldChunkKey)
            put(prop);

        return true;
    }

    public static void copyRenderTransformTo(
            Prop prop,
            Matrix4f destination,
            long renderTimeNanos
    ) {
        prop.copyTransformTo(destination);
        TransformInterpolation interpolation = INTERPOLATIONS.get(prop.getId());
        if (interpolation == null)
            return;

        interpolation.sample(renderTimeNanos);
        destination.getScale(interpolation.scaleScratch);
        destination.translationRotateScale(
                interpolation.renderPosition,
                interpolation.renderRotation,
                interpolation.scaleScratch
        );
    }

    public static Vec3 clipLocalPlayerMovement(Entity entity, Vec3 movement) {
        if (!entity.level().isClientSide || entity.noPhysics || entity.isSpectator())
            return movement;
        if (movement.lengthSqr() < 1.0e-12)
            return movement;

        AABB sweptBounds = PropStepManager.collisionQueryBounds(entity, movement);
        AABB searchBounds = sweptBounds.inflate(16.0);

        int minChunkX = Mth.floor(searchBounds.minX) >> 4;
        int maxChunkX = Mth.floor(searchBounds.maxX) >> 4;
        int minChunkZ = Mth.floor(searchBounds.minZ) >> 4;
        int maxChunkZ = Mth.floor(searchBounds.maxZ) >> 4;

        long renderTimeNanos = System.nanoTime();

        COLLISION_TRIANGLES_SCRATCH.clear();
        PUSH_CANDIDATES_SCRATCH.clear();

        try {
            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++)
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++)
                    for (Prop prop : getInChunk(chunkX, chunkZ)) {
                        if (prop.getCollisionMode() != PropCollisionMode.SOLID)
                            continue;

                        PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);
                        if (definition == null)
                            continue;

                        PropGeometry geometry = PropGeometry.get(
                                Minecraft.getInstance().getResourceManager(),
                                definition.getModelId()
                        );
                        if (geometry == null)
                            continue;

                        copyRenderTransformTo(prop, COLLISION_TRANSFORM, renderTimeNanos);
                        List<PropGeometry.Triangle> triangles = PropEntityCollision.transformTriangles(
                                geometry, COLLISION_TRANSFORM
                        );
                        AABB bounds = PropEntityCollision.boundsOf(triangles);
                        if (!bounds.intersects(sweptBounds))
                            continue;

                        COLLISION_TRIANGLES_SCRATCH.addAll(triangles);
                        if (prop.getMotionMode() == PropMotionMode.DYNAMIC)
                            PUSH_CANDIDATES_SCRATCH.add(new PushCandidate(prop.getId(), triangles));
                    }

            PropStepManager.MovementResult steppedResult = PropStepManager.clipMovementWithResult(
                    entity,
                    movement,
                    COLLISION_TRIANGLES_SCRATCH
            );

            Vec3 clippedMovement = steppedResult.movement();
            if (steppedResult.steppedUp())
                return clippedMovement;

            Vec3 incomingVelocity = new Vec3(
                    movement.x * 20.0,
                    0.0,
                    movement.z * 20.0
            );
            if (incomingVelocity.lengthSqr() < 1.0e-6 || PUSH_CANDIDATES_SCRATCH.isEmpty())
                return clippedMovement;

            List<PropEntityCollision.PropContact> pushContacts = new ArrayList<>();

            for (PushCandidate candidate : PUSH_CANDIDATES_SCRATCH) {
                List<PropEntityCollision.PropContact> contacts =
                        PropEntityCollision.findHorizontalContacts(
                                candidate.propId(),
                                entity.getBoundingBox(),
                                movement,
                                candidate.triangles()
                        );

                for (PropEntityCollision.PropContact contact : contacts) {
                    double closingSpeed = -incomingVelocity.dot(contact.normal());
                    if (closingSpeed > 0.05)
                        pushContacts.add(contact);
                }
            }
            if (pushContacts.isEmpty())
                return clippedMovement;

            Vec3 ordinaryMovement = PropStepManager.clipMovementWithoutStep(
                    entity, movement, COLLISION_TRIANGLES_SCRATCH
            );
            double blockedX = movement.x - ordinaryMovement.x;
            double blockedZ = movement.z - ordinaryMovement.z;
            if (blockedX * blockedX + blockedZ * blockedZ <= 1.0e-8)
                return clippedMovement;

            for (PropEntityCollision.PropContact contact : pushContacts)
                queuePushIntent(contact, incomingVelocity);

            return ordinaryMovement;
        } finally {
            COLLISION_TRIANGLES_SCRATCH.clear();
            PUSH_CANDIDATES_SCRATCH.clear();
        }
    }

    private static void queuePushIntent(
            PropEntityCollision.PropContact contact,
            Vec3 incomingVelocity
    ) {
        double closingSpeed = -incomingVelocity.dot(contact.normal());
        if (!Double.isFinite(closingSpeed) || closingSpeed <= 0.05)
            return;

        PushIntent candidate = new PushIntent(
                contact.propId(),
                (float) incomingVelocity.x,
                (float) incomingVelocity.z,
                closingSpeed
        );

        PENDING_PUSH_INTENTS.merge(
                contact.propId(),
                candidate,
                (previous, next) ->
                        next.closingSpeed() > previous.closingSpeed()
                                ? next
                                : previous
        );
    }


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            PENDING_PUSH_INTENTS.clear();
            return;
        }

        for (PushIntent intent : PENDING_PUSH_INTENTS.values())
            ModNetworking.CHANNEL.sendToServer(new PropPushIntentPacket(
                    intent.propId(),
                    intent.velocityX(),
                    intent.velocityZ()
            ));

        PENDING_PUSH_INTENTS.clear();
    }

    @SubscribeEvent
    public static void onChunkUnload(@NotNull ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ClientLevel))
            return;
        ChunkPos pos = event.getChunk().getPos();
        removeChunk(pos.x, pos.z);
    }

    @SubscribeEvent
    public static void onLevelUnload(@NotNull LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ClientLevel))
            return;
        clear();
    }


    private record PushCandidate(
            UUID propId,
            List<PropGeometry.Triangle> triangles
    ) {}
    private record PushIntent(
            UUID propId,
            float velocityX,
            float velocityZ,
            double closingSpeed
    ) {}


    private static final class TransformInterpolation {
        private final Vector3f fromPosition = new Vector3f();
        private final Vector3f targetPosition = new Vector3f();
        private final Vector3f renderPosition = new Vector3f();
        private final Vector3f scaleScratch = new Vector3f();

        private final Quaternionf fromRotation = new Quaternionf();
        private final Quaternionf targetRotation = new Quaternionf();
        private final Quaternionf renderRotation = new Quaternionf();

        private long startNanos;
        private boolean active;

        private TransformInterpolation(Prop prop) {
            Matrix4f initial = prop.getTransform();

            initial.getTranslation(renderPosition);
            initial.getUnnormalizedRotation(renderRotation).normalize();

            fromPosition.set(renderPosition);
            targetPosition.set(renderPosition);
            fromRotation.set(renderRotation);
            targetRotation.set(renderRotation);
        }

        private void setTarget(
                float x,
                float y,
                float z,
                float qx,
                float qy,
                float qz,
                float qw,
                long nowNanos
        ) {
            sample(nowNanos);

            float dx = x - renderPosition.x;
            float dy = y - renderPosition.y;
            float dz = z - renderPosition.z;

            targetRotation.set(qx, qy, qz, qw).normalize();
            if (dx * dx + dy * dy + dz * dz > SNAP_DISTANCE_SQUARED) {
                fromPosition.set(x, y, z);
                targetPosition.set(x, y, z);
                renderPosition.set(x, y, z);

                fromRotation.set(targetRotation);
                renderRotation.set(targetRotation);

                active = false;
                return;
            }

            fromPosition.set(renderPosition);
            fromRotation.set(renderRotation);

            targetPosition.set(x, y, z);

            startNanos = nowNanos;
            active = true;
        }

        private void sample(long nowNanos) {
            if (!active) {
                renderPosition.set(targetPosition);
                renderRotation.set(targetRotation);
                return;
            }

            double elapsedNanos = Math.max(0L, nowNanos - startNanos);
            float alpha = (float) Math.min(1.0, elapsedNanos / (double) INTERPOLATION_DURATION_NANOS);

            renderPosition.set(
                    fromPosition.x + (targetPosition.x - fromPosition.x) * alpha,
                    fromPosition.y + (targetPosition.y - fromPosition.y) * alpha,
                    fromPosition.z + (targetPosition.z - fromPosition.z) * alpha
            );

            fromRotation.slerp(targetRotation, alpha, renderRotation).normalize();

            if (alpha >= 1.0f)
                active = false;
        }
    }
}
