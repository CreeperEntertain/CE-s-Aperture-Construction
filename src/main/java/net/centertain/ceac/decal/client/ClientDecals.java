package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientDecals {
    private static final Map<UUID, Decal> DECALS = new ConcurrentHashMap<>();

    private ClientDecals() {}

    public static void put(Decal decal) {
        DECALS.put(decal.getId(), decal);
        TranslucentKBuffer.markSpatialIndexDirty();
    }
    public static boolean remove(UUID id) {
        boolean status = DECALS.remove(id) != null;
        TranslucentKBuffer.markSpatialIndexDirty();
        return status;
    }
    public static Map<UUID, Decal> getAll() {
        return DECALS;
    }
    public static void clear() {
        DECALS.clear();
    }

    public static List<Decal> getByRenderOrder() {
        List<Decal> decals = new ArrayList<>(DECALS.values());
        decals.sort(Comparator.comparingInt(Decal::getRenderingOrder).reversed());
        return decals;
    }
}
