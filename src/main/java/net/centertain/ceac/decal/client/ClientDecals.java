package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.Decal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientDecals {
    private static final Map<UUID, Decal> DECALS = new HashMap<>();

    private ClientDecals() {}

    public static void put(Decal decal) {
        DECALS.put(decal.getId(), decal);
    }
    public static boolean remove(UUID id) {
        return DECALS.remove(id) != null;
    }
    public static Map<UUID, Decal> getAll() {
        return DECALS;
    }
    public static void clear() {
        DECALS.clear();
    }
}
