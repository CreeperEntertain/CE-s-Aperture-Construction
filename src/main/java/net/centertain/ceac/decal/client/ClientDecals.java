package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.Decal;
import net.centertain.ceac.decal.client.render.DecalCuller;
import net.centertain.ceac.decal.client.render.TranslucentKBuffer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientDecals {
    private static final Map<UUID, Decal> DECALS = new ConcurrentHashMap<>();

    private static int lastCulledCount = -1;

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
    public static void clear() {
        DECALS.clear();
        TranslucentKBuffer.markSpatialIndexDirty();
        lastCulledCount = -1;
    }

    public static Map<UUID, Decal> getAll() {
        return DECALS;
    }
    public static List<Decal> getByRenderOrder() {
        List<Decal> decals = new ArrayList<>(DECALS.values());
        decals.sort(Comparator.comparingInt(Decal::getRenderingOrder).reversed());
        return decals;
    }

    public static Map<UUID, Decal> getAllCulled(Matrix4f viewProjection, Vec3 cameraPosition) { // Should only be used for early rendering returns
        return DecalCuller.getFrustumCulledMap(getAll(), viewProjection, cameraPosition);
    }
    public static List<Decal> getByRenderOrderCulled(Matrix4f viewProjection, Vec3 cameraPosition) { // Should be used for actual rendering purposes
        List<Decal> decals = DecalCuller.getOcclusionCulledList(
                DecalCuller.getFrustumCulledList(getByRenderOrder(), viewProjection, cameraPosition),
                viewProjection,
                cameraPosition
        );
        if (decals.size() != lastCulledCount) {
            lastCulledCount = decals.size();
            TranslucentKBuffer.markSpatialIndexDirty();
        }
        System.out.println(decals.size());
        return decals;
    }
}
