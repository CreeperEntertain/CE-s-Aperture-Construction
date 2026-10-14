package net.centertain.ceac.prop.client;

import net.centertain.ceac.prop.PropGeometry;
import net.centertain.ceac.prop.network.PropBreakParticlesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class PropBreakParticlesClient {
    private PropBreakParticlesClient() {}

    public static void spawn(PropBreakParticlesPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || packet.getCount() <= 0)
            return;

        ResourceLocation modelId = packet.getModelId();
        PropGeometry geometry = PropGeometry.get(minecraft.getResourceManager(), modelId);
        if (geometry == null || geometry.getVolume() <= 1.0e-12)
            return;

        BakedModel model = minecraft.getModelManager().getModel(modelId);
        TextureAtlasSprite sprite = model.getParticleIcon(ModelData.EMPTY);
        Matrix4f transform = packet.getTransform();
        Vec3 center = transformPoint(geometry.getBoundsCenter(), transform);
        RandomSource random = RandomSource.create(packet.getSeed());

        for (int i = 0; i < packet.getCount(); i++) {
            Vec3 localPoint = geometry.samplePointInside(random);
            if (localPoint == null)
                continue;

            Vec3 worldPoint = transformPoint(localPoint, transform);
            Vec3 direction  = worldPoint.subtract(center);

            if (direction.lengthSqr() < 1.0e-8)
                direction = new Vec3(
                        random.nextDouble() - 0.5,
                        random.nextDouble() - 0.5,
                        random.nextDouble() - 0.5
                ).normalize();
            else
                direction = direction.normalize();

            double normalWeight =
                    Math.abs(direction.x) +
                    Math.abs(direction.y) +
                    Math.abs(direction.z);
            float shade = (float) ((
                    Math.abs(direction.x) * 0.6
                    + Math.abs(direction.z) * 0.8
                    + (direction.y >= 0.0
                            ? direction.y
                            : -direction.y * 0.5)
            ) / normalWeight);

            double speed = 0.02 + random.nextDouble() * 0.09;
            double xd = direction.x * speed + (random.nextDouble() - 0.5) * 0.035;
            double yd = direction.y * speed + 0.025 + random.nextDouble() * 0.065;
            double zd = direction.z * speed + (random.nextDouble() - 0.5) * 0.035;

            minecraft.particleEngine.add(new PropBreakParticle(
                    level,
                    worldPoint.x,
                    worldPoint.y,
                    worldPoint.z,
                    xd,
                    yd,
                    zd,
                    sprite,
                    shade,
                    random
            ));
        }
    }

    private static Vec3 transformPoint(Vec3 point, Matrix4f transform) {
        Vector3f result = new Vector3f(
                (float) point.x,
                (float) point.y,
                (float) point.z
        );
        result.mulPosition(transform);
        return new Vec3(result.x, result.y, result.z);
    }
}
