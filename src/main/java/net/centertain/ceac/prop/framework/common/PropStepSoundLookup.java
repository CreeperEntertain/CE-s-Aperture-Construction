package net.centertain.ceac.prop.framework.common;

import net.centertain.ceac.prop.ModProps;
import net.centertain.ceac.prop.framework.Prop;
import net.centertain.ceac.prop.framework.PropDefinition;
import net.centertain.ceac.prop.framework.PropGeometry;
import net.centertain.ceac.prop.framework.enums.PropCollisionMode;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public final class PropStepSoundLookup {
    private static final double RAY_START = 0.02;
    private static final double RAY_LENGTH = 0.1;
    private static final double MAX_BELOW_FEET = 0.08;
    private static final double MIN_WALKABLE_NORMAL_Y = Math.cos(Math.toRadians(45.0));

    private PropStepSoundLookup() {}


    public static boolean isSupportedByProp(
            Entity entity,
            Iterable<Prop> props,
            ResourceManager resources
    ) {
        if (entity.noPhysics || entity.isSpectator())
            return false;
        return findSupportingDefinition(entity, props, resources) != null;
    }

    public static SoundType findSoundType(
            Entity entity,
            Iterable<Prop> props,
            ResourceManager resources
    ) {
        if (entity.noPhysics || entity.isSpectator())
            return null;

        Prop prop = findSupportingProp(entity, entity.getBoundingBox(), props, resources);
        if (prop == null)
            return null;

        PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);

        return definition == null ? null : definition.getSoundType();
    }

    public static Prop findSupportingProp(
            Entity entity,
            AABB box,
            Iterable<Prop> props,
            ResourceManager resources
    ) {
        if (entity.noPhysics || entity.isSpectator())
            return null;

        double footY = box.minY;
        Vec3[] samplePoints = getSamplePoints(box, footY);
        Vec3 down = new Vec3(0.0, -1.0, 0.0);

        double closestDistance = Double.POSITIVE_INFINITY;
        Prop closestProp = null;
        Matrix4f transform = new Matrix4f();

        for (Prop prop : props) {
            if (prop.getCollisionMode() != PropCollisionMode.SOLID)
                continue;

            PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            PropGeometry geometry = PropGeometry.get(resources, definition.getModelId());
            if (geometry == null)
                continue;

            prop.copyTransformTo(transform);

            for (Vec3 sample : samplePoints) {
                PropGeometry.RayHit hit = geometry.raycast(sample, down, transform, RAY_LENGTH);
                if (hit == null || hit.distance() >= closestDistance)
                    continue;

                double normalLength = hit.normal().length();
                if (normalLength < 1.0e-8)
                    continue;

                double verticality = Math.abs(hit.normal().y) / normalLength;
                if (verticality < MIN_WALKABLE_NORMAL_Y)
                    continue;

                double surfaceY = hit.location().y;
                if (surfaceY < footY - MAX_BELOW_FEET)
                    continue;
                if (surfaceY > footY + RAY_START)
                    continue;

                closestDistance = hit.distance();
                closestProp = prop;
            }
        }

        return closestProp;
    }

    private static PropDefinition findSupportingDefinition(Entity entity, Iterable<Prop> props, ResourceManager resources) {
        AABB box = entity.getBoundingBox();
        double footY = box.minY;

        Vec3[] samplePoints = getSamplePoints(box, footY);
        Vec3 down = new Vec3(0.0, -1.0, 0.0);

        double closestDistance = Double.POSITIVE_INFINITY;
        PropDefinition closestDefinition = null;

        Matrix4f transform = new Matrix4f();

        for (Prop prop : props) {
            if (prop.getCollisionMode() != PropCollisionMode.SOLID)
                continue;

            PropDefinition definition = ModProps.get(prop.getDefinitionId()).orElse(null);
            if (definition == null)
                continue;

            PropGeometry geometry = PropGeometry.get(resources, definition.getModelId());
            if (geometry == null)
                continue;

            prop.copyTransformTo(transform);

            for (Vec3 sample : samplePoints) {
                PropGeometry.RayHit hit = geometry.raycast(sample, down, transform, RAY_LENGTH);
                if (hit == null || hit.distance() >= closestDistance)
                    continue;

                double normalLength = hit.normal().length();
                if (normalLength < 1.0e-8)
                    continue;

                double verticality = Math.abs(hit.normal().y) / normalLength;
                if (verticality < MIN_WALKABLE_NORMAL_Y)
                    continue;

                double surfaceY = hit.location().y;
                if (surfaceY < footY - MAX_BELOW_FEET)
                    continue;
                if (surfaceY > footY + RAY_START)
                    continue;

                closestDistance = hit.distance();
                closestDefinition = definition;
            }
        }

        return closestDefinition;
    }

    private static @NotNull Vec3[] getSamplePoints(AABB box, double footY) {
        double centerX = (box.minX + box.maxX) * 0.5;
        double centerZ = (box.minZ + box.maxZ) * 0.5;

        double insetX = Math.min(0.08, (box.maxX - box.minX) * 0.2);
        double insetZ = Math.min(0.08, (box.maxZ - box.minZ) * 0.2);

        double minX = box.minX + insetX;
        double maxX = box.maxX - insetX;
        double minZ = box.minZ + insetZ;
        double maxZ = box.maxZ - insetZ;

        return new Vec3[]{
                new Vec3(centerX, footY + RAY_START, centerZ),
                new Vec3(minX, footY + RAY_START, minZ),
                new Vec3(minX, footY + RAY_START, maxZ),
                new Vec3(maxX, footY + RAY_START, minZ),
                new Vec3(maxX, footY + RAY_START, maxZ)
        };
    }
}
