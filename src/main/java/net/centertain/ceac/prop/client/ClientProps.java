package net.centertain.ceac.prop.client;

import net.centertain.ceac.prop.Prop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class ClientProps {
    private static final Map<Long, Map<UUID, Prop>> PROPS_BY_CHUNK = new HashMap<>();
    private static final Map<UUID, Long> CHUNK_BY_ID = new HashMap<>();

    private static ClientLevel currentLevel;

    private ClientProps() {}

    public static void put(Prop prop) {
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

        PROPS_BY_CHUNK.computeIfAbsent(newChunkKey, ignored -> new HashMap<>()).put(prop.getId(), prop);
    }

    public static Collection<Prop> getInChunk(int chunkX, int chunkZ) {
        Map<UUID, Prop> props = PROPS_BY_CHUNK.get(ChunkPos.asLong(chunkX, chunkZ));
        return props == null ? List.of() : props.values();
    }

    public static void replaceChunk(
            int chunkX,
            int chunkZ,
            Collection<Prop> props
    ) {
        removeChunk(chunkX, chunkZ);
        props.forEach(ClientProps::put);
    }

    public static void removeChunk(int chunkX, int chunkZ) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        Map<UUID, Prop> removed = PROPS_BY_CHUNK.remove(key);
        if (removed != null)
            removed.keySet().forEach(id -> CHUNK_BY_ID.remove(id, key));
    }

    public static void clear() {
        PROPS_BY_CHUNK.clear();
        CHUNK_BY_ID.clear();
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ClientLevel))
            return;
        ChunkPos pos = event.getChunk().getPos();
        removeChunk(pos.x, pos.z);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level != currentLevel) {
            clear();
            currentLevel = level;
        }
    }
}
