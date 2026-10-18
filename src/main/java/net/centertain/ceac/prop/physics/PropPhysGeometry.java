package net.centertain.ceac.prop.physics;

import cc.modlabs.box3d.*;
import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.enums.PropCollisionMode;
import net.centertain.ceac.prop.enums.PropMotionMode;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cc.modlabs.box3d.global.Box3D.*;

@SuppressWarnings("resource")
public final class PropPhysGeometry {
    static final int MAX_PROP_HULL_VERTICES = 32;

    private static final long CATEGORY_WORLD = 1L;
    private static final long CATEGORY_SOLID_PROP = 1L << 1;
    private static final long CATEGORY_PASSIVE_PROP = 1L << 2;

    private static final long MASK_SOLID_PROP =
            CATEGORY_WORLD |
            CATEGORY_SOLID_PROP |
            PropPhysManager.CATEGORY_ENTITY;

    private static final long MASK_PASSIVE_PROP = CATEGORY_WORLD;

    private PropPhysGeometry() {}


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


    static final class GeometryData {
        final PropGeometry geometry;
        private final double minX;
        private final double minY;
        private final double minZ;
        private final double maxX;
        private final double maxY;
        private final double maxZ;

        GeometryData(PropGeometry geometry) {
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
}
