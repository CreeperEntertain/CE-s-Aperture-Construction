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
            Matrix4f viewProjection,
            Vec3 cameraPosition
    ) {
        Vec3 origin = decal.getOrigin();

        double width = decal.getPixelWidth() / 16.0;
        double height = decal.getPixelHeight() / 16.0;
        double depth = decal.getBlockDepth();

        double radius = 0.5 * Math.sqrt(
                width * width +
                height * height +
                depth * depth
        );

        float x = (float) (origin.x - cameraPosition.x);
        float y = (float) (origin.y - cameraPosition.y);
        float z = (float) (origin.z - cameraPosition.z);

        float[][] planes = {
                {
                        viewProjection.m03() + viewProjection.m00(),
                        viewProjection.m13() + viewProjection.m10(),
                        viewProjection.m23() + viewProjection.m20(),
                        viewProjection.m33() + viewProjection.m30()
                },
                {
                        viewProjection.m03() - viewProjection.m00(),
                        viewProjection.m13() - viewProjection.m10(),
                        viewProjection.m23() - viewProjection.m20(),
                        viewProjection.m33() - viewProjection.m30()
                },
                {
                        viewProjection.m03() + viewProjection.m01(),
                        viewProjection.m13() + viewProjection.m11(),
                        viewProjection.m23() + viewProjection.m21(),
                        viewProjection.m33() + viewProjection.m31()
                },
                {
                        viewProjection.m03() - viewProjection.m01(),
                        viewProjection.m13() - viewProjection.m11(),
                        viewProjection.m23() - viewProjection.m21(),
                        viewProjection.m33() - viewProjection.m31()
                },
                {
                        viewProjection.m03() + viewProjection.m02(),
                        viewProjection.m13() + viewProjection.m12(),
                        viewProjection.m23() + viewProjection.m22(),
                        viewProjection.m33() + viewProjection.m32()
                },
                {
                        viewProjection.m03() - viewProjection.m02(),
                        viewProjection.m13() - viewProjection.m12(),
                        viewProjection.m23() - viewProjection.m22(),
                        viewProjection.m33() - viewProjection.m32()
                }
        };

        for (float[] plane : planes) {
            double normalLength = Math.sqrt(
                    plane[0] * plane[0] +
                    plane[1] * plane[1] +
                    plane[2] * plane[2]
            );
            double distance =
                    plane[0] * x +
                    plane[1] * y +
                    plane[2] * z +
                    plane[3];
            if (distance < -radius * normalLength)
                return false;
        }

        return true;
    }


    public static List<Decal> getOcclusionCulledList(
            List<Decal> decals,
            Matrix4f viewProjection,
            Vec3 cameraPosition
    ) {
        if (decals.isEmpty())
            return decals;

        TranslucentKBuffer.uploadDecals(decals);

        DecalDepthPyramid.build(
                DecalRenderer.getOpaqueDepthTexture(),
                TranslucentKBuffer.getWidth(),
                TranslucentKBuffer.getHeight()
        );

        boolean[] visible = DecalShaders.runDecalOcclusion(
                decals.size(),
                viewProjection,
                TranslucentKBuffer.getDecalBuffer(),
                DecalDepthPyramid.getTexture(),
                DecalDepthPyramid.getLevels(),
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
