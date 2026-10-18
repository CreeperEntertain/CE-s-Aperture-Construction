package net.centertain.ceac.prop.physics;

import net.centertain.ceac.prop.Prop;
import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.PropDefinitions;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import java.util.*;

public final class PropSpatialIndex {
    static final int PROP_SPATIAL_CELL_SIZE = 8;

    private final PropPhysManager manager;

    private final Map<UUID, PropSpatialEntry> solidPropSpatialEntries = new HashMap<>();
    private final Map<Long, Set<UUID>> propSpatialCells = new HashMap<>();
    private final Set<UUID> spatialSeenProps = new HashSet<>();

    private long spatialQueryStamp;
    private long collisionQueryStamp;

    PropSpatialIndex(PropPhysManager manager) {
        this.manager = manager;
    }

    Map<Long, Set<UUID>> cells() {
        return propSpatialCells;
    }
    PropSpatialEntry entry(UUID id) {
        return solidPropSpatialEntries.get(id);
    }
    long nextSpatialQueryStamp() {
        return ++spatialQueryStamp;
    }
    long nextCollisionQueryStamp() {
        return ++collisionQueryStamp;
    }


    void updatePropSpatialIndex() {
        spatialSeenProps.clear();

        for (Prop prop : manager.activeProps())
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

        PropPhysManager.GeometryData geometry = manager.getGeometry(definition.getModelId());
        if (geometry == null)
            return false;

        UUID id = prop.getId();
        long transformRevision = prop.getTransformRevision();
        PropSpatialEntry existing = solidPropSpatialEntries.get(id);
        if (
                existing != null &&
                existing.definitionId.equals(prop.getDefinitionId()) &&
                existing.transformRevision == transformRevision
        )
            return true;
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

    static int spatialCell(double coordinate) {
        return PropPhysManager.floor(coordinate / PROP_SPATIAL_CELL_SIZE);
    }

    static long spatialCellKey(int x, int y, int z) {
        return new BlockPos(x, y, z).asLong();
    }

    void clear() {
        solidPropSpatialEntries.clear();
        propSpatialCells.clear();
        spatialSeenProps.clear();
    }


    static final class PropSpatialEntry {
        final ResourceLocation definitionId;
        final AABB interactionBounds;
        final Set<Long> cellKeys;
        final long transformRevision;
        final AABB worldBounds;
        long lastCollisionQueryStamp;
        final List<PropGeometry.Triangle> triangles;

        long lastQueryStamp;

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
}
