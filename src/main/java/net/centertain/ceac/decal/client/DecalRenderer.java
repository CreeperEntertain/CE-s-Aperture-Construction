package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.DecalCapabilities;
import net.centertain.ceac.decal.DecalManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public final class DecalRenderer {
    private DecalRenderer() {}

    public static void render(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;

        Camera camera = event.getCamera();
        BlockPos cameraPos = BlockPos.containing(camera.getPosition());
        LevelChunk chunk = minecraft.level.getChunkAt(cameraPos);
        DecalManager manager = DecalCapabilities.get(chunk);

        if (!manager.getDecals().isEmpty())
            System.out.println("Client sees " + manager.getDecals().size() + " decals.");
    }
}
