package net.centertain.ceac.decal.client.render;

import net.centertain.ceac.decal.Decal;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.*;

public final class DecalCuller {
    private DecalCuller() {}

    public static Map<UUID, Decal> getFrustumCulledMap(
            Map<UUID, Decal> decals,
            Matrix4f viewProjection,
            Vec3 cameraPosition
    ) {
        Map<UUID, Decal> result = new HashMap<>();
        for (Map.Entry<UUID, Decal> entry : decals.entrySet()) {
            if (isInsideFrustum(entry.getValue(), viewProjection, cameraPosition))
                result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static List<Decal> getFrustumCulledList(
            List<Decal> decals,
            Matrix4f viewProjection,
            Vec3 cameraPosition
    ) {
        List<Decal> result = new ArrayList<>();
        for (Decal decal : decals) {
            if (isInsideFrustum(decal, viewProjection, cameraPosition))
                result.add(decal);
        }
        return result;
    }

    private static boolean isInsideFrustum(
            Decal decal,
            Matrix4f projectionMatrix,
            Vec3 cameraPosition
    ) {
        Vec3 origin = decal.getOrigin();

        double width = decal.getPixelWidth() / 16.0;
        double height = decal.getPixelHeight() / 16.0;
        double depth = decal.getBlockDepth();

        double radius = 0.5 * Math.sqrt(width * width + height * height + depth * depth);

        double x = origin.x - cameraPosition.x;
        double y = origin.y - cameraPosition.y;
        double z = origin.z - cameraPosition.z;

        Vector4f position = new Vector4f(
                (float) x,
                (float) y,
                (float) z,
                1.0f
        );

        projectionMatrix.transform(position);

        double left = position.x + radius;
        double right = -position.x + radius;
        double bottom = position.y + radius;
        double top = -position.y + radius;
        double near = position.z + radius;
        double far = -position.z + radius;

        return
                left >= -position.w &&
                right >= -position.w &&
                bottom >= -position.w &&
                top >= -position.w &&
                near >= -position.w &&
                far >= -position.w;
    }


    public static Map<UUID, Decal> getOcclusionCulledMap(
            Map<UUID, Decal> decals,
            Matrix4f viewProjection,
            Vec3 cameraPosition
    ) {
        if (decals.isEmpty())
            return decals;

        List<Decal> decalList = new ArrayList<>(decals.values());

        TranslucentKBuffer.uploadDecals(decalList);

        boolean[] visible = DecalShaders.runDecalOcclusion(
                decalList.size(),
                viewProjection,
                TranslucentKBuffer.getDecalBuffer(),
                DecalRenderer.getOpaqueDepthTexture(),
                1,
                TranslucentKBuffer.getWidth(),
                TranslucentKBuffer.getHeight(),
                cameraPosition
        );

        Map<UUID, Decal> result = new HashMap<>();

        for (int i = 0; i < decalList.size(); ++i) {
            if (visible[i])
                result.put(decalList.get(i).getId(), decalList.get(i));
        }

        return result;
    }

    public static List<Decal> getOcclusionCulledList(
            List<Decal> decals,
            Matrix4f viewProjection,
            Vec3 cameraPosition
    ) {
        if (decals.isEmpty())
            return decals;

        TranslucentKBuffer.uploadDecals(decals);

        boolean[] visible = DecalShaders.runDecalOcclusion(
                decals.size(),
                viewProjection,
                TranslucentKBuffer.getDecalBuffer(),
                DecalRenderer.getOpaqueDepthTexture(),
                1,
                TranslucentKBuffer.getWidth(),
                TranslucentKBuffer.getHeight(),
                cameraPosition
        );

        List<Decal> result = new ArrayList<>();

        for (int i = 0; i < decals.size(); ++i) {
            if (visible[i])
                result.add(decals.get(i));
        }

        return result;
    }
}
