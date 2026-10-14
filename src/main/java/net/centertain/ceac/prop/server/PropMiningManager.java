package net.centertain.ceac.prop.server;

import net.centertain.ceac.block.custom.material_shapes.basic_shapes.Block;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.prop.*;
import net.centertain.ceac.prop.network.PropBreakParticlesPacket;
import net.centertain.ceac.prop.network.PropMiningPacket;
import net.centertain.ceac.prop.network.PropMiningProgressPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PropMiningManager {
    private static final long HEARTBEAT_TIMEOUT_TICKS = 5L;

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    private PropMiningManager() {}

    public static void handle(
            ServerPlayer player,
            PropMiningPacket packet
    ) {
        if (!player.isAlive() || player.isSpectator()) {
            cancel(player.getUUID(), null);
            return;
        }

        switch (packet.getAction()) {
            case START -> start(player, packet.getPropId());
            case CONTINUE -> continueMining(player, packet.getPropId());
            case ABORT -> cancel(player.getUUID(), packet.getPropId());
        }
    }

    private static void start(
            ServerPlayer player,
            UUID requestedPropId
    ) {
        cancel(player.getUUID(), null);
        if (!(player.level() instanceof ServerLevel level))
            return;
        if (!player.getAbilities().mayBuild)
            return;

        PropRaycast.Target target = PropRaycast.findTarget(player, getReach(player));
        if (target == null)
            return;

        Prop prop = target.prop();
        if (!prop.getId().equals(requestedPropId))
            return;

        PropDefinition definition = target.definition();
        if (player.getAbilities().instabuild) {
            destroyProp(player, prop, definition);
            return;
        }
        if (!definition.getMinable())
            return;

        SESSIONS.put(player.getUUID(), new Session(
                level,
                prop.getId(),
                prop.getBlockPosition(),
                level.getGameTime()
        ));
    }

    private static void continueMining(
            ServerPlayer player,
            UUID requestedPropId
    ) {
        Session session = SESSIONS.get(player.getUUID());
        if (session == null) {
            start(player, requestedPropId);
            return;
        }
        if (!session.propId.equals(requestedPropId))
            return;
        if (player.level() != session.level) {
            cancel(player.getUUID(), null);
            return;
        }

        PropRaycast.Target target = PropRaycast.findTarget(player, getReach(player));
        if (target == null || !target.prop().getId().equals(session.propId)) {
            cancel(player.getUUID(), null);
            return;
        }

        session.lastHeartbeatTime = session.level.getGameTime();
        session.blockPos = target.prop().getBlockPosition();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!(event.player instanceof ServerPlayer player))
            return;

        Session session = SESSIONS.get(player.getUUID());
        if (session == null)
            return;
        if (!player.isAlive()
            || player.isSpectator()
            || !player.getAbilities().mayBuild
            || player.level() != session.level) {
            cancel(player.getUUID(), null);
            return;
        }

        long gameTime = session.level.getGameTime();
        if (gameTime - session.lastHeartbeatTime > HEARTBEAT_TIMEOUT_TICKS) {
            cancel(player.getUUID(), null);
            return;
        }

        PropRaycast.Target target = PropRaycast.findTarget(player, getReach(player));
        if (target == null || !target.prop().getId().equals(session.propId)) {
            cancel(player.getUUID(), null);
            return;
        }

        Prop prop = target.prop();
        PropDefinition definition = target.definition();
        session.blockPos = prop.getBlockPosition();
        if (player.getAbilities().instabuild) {
            destroyProp(player, prop, definition);
            return;
        }
        if (!definition.getMinable()) {
            cancel(player.getUUID(), null);
            return;
        }

        float progress = PropMiningMath.getDestroyProgress(player, definition, prop.getBlockPosition());
        session.progress += progress;
        if (session.progress >= 1.0f) {
            destroyProp(player, prop, definition);
            return;
        }

        int stage = Math.min(9, (int) (session.progress * 10.0f));
        if (stage != session.stage) {
            session.stage = stage;
            sendStage(
                    session.level,
                    session.blockPos,
                    session.propId,
                    stage
            );
        }
        if (gameTime % 4L == 0L) {
            var soundType = definition.getSoundType();
            session.level.playSound(
                    null,
                    session.blockPos,
                    soundType.getHitSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 8.0F,
                    soundType.getPitch() * 0.5F
            );
        }
    }

    private static void destroyProp(
            ServerPlayer player,
            Prop prop,
            PropDefinition definition
    ) {
        if (!(player.level() instanceof ServerLevel level))
            return;
        if (!Prop.removeFromWorld(prop, level))
            return;

        sendBreakParticles(level, prop, definition);

        if (
                !player.getAbilities().instabuild &&
                definition.getDropItem() != null &&
                PropMiningMath.hasRequiredTool(player, definition)
        )
            Block.popResource(
                    level,
                    prop.getBlockPosition(),
                    new ItemStack(definition.getDropItem())
            );
        if (!player.getAbilities().instabuild) {
            ItemStack tool = player.getMainHandItem();
            if (!tool.isEmpty())
                tool.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        var soundType = definition.getSoundType();
        level.playSound(
                null,
                prop.getBlockPosition(),
                soundType.getBreakSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );
    }

    private static double getReach(Player player) {
        return player.getAbilities().instabuild ? 5.0d : 4.5d;
    }

    private static void cancel(
            UUID playerId,
            @Nullable UUID expectedPropId
    ) {
        Session session = SESSIONS.get(playerId);
        if (session == null)
            return;
        if (expectedPropId != null && !session.propId.equals(expectedPropId))
            return;

        SESSIONS.remove(playerId);

        if (session.stage >= 0)
            sendStage(
                    session.level,
                    session.blockPos,
                    session.propId,
                    -1
            );
    }

    private static void sendStage(
            ServerLevel level,
            BlockPos position,
            UUID propId,
            int stage
    ) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(
                position.getX() >> 4,
                position.getZ() >> 4
        );
        if (chunk == null)
            return;

        ModNetworking.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                new PropMiningProgressPacket(propId, stage)
        );
    }

    private static void sendBreakParticles(
            ServerLevel level,
            Prop prop,
            PropDefinition definition
    ) {
        PropGeometry geometry = PropGeometry.get(level.getServer().getResourceManager(), definition.getModelId());
        if (geometry == null)
            return;

        Matrix4f transform = prop.getTransform();
        double volume = geometry.getVolume() * Math.abs(new Matrix3f(transform).determinant());
        if (!Double.isFinite(volume) || volume <= 1.0e-12)
            return;

        int count = (int) Math.min(1000.0, Math.max(1.0, Math.ceil(volume * 64.0)));
        LevelChunk chunk = level.getChunkSource().getChunkNow(
                prop.getBlockPosition().getX() >> 4,
                prop.getBlockPosition().getZ() >> 4
        );
        if (chunk == null)
            return;

        ModNetworking.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
                new PropBreakParticlesPacket(
                        definition.getModelId(),
                        transform,
                        count,
                        RandomSource.create().nextLong()
                )
        );
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        cancel(event.getEntity().getUUID(), null);
    }

    private static final class Session {
        private final ServerLevel level;
        private final UUID propId;
        private BlockPos blockPos;

        private long lastHeartbeatTime;
        private float progress;
        private int stage = -1;

        private Session(
                ServerLevel level,
                UUID propId,
                BlockPos blockPos,
                long gameTime
        ) {
            this.level = level;
            this.propId = propId;
            this.blockPos = blockPos;
            this.lastHeartbeatTime = gameTime;
        }
    }
}
