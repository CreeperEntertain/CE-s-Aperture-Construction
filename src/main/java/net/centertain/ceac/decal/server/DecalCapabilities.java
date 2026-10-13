package net.centertain.ceac.decal.server;

import net.centertain.ceac.decal.ModDecalCapabilities;
import net.minecraft.world.level.chunk.LevelChunk;

public class DecalCapabilities {
    public static DecalManager get(LevelChunk chunk) {
        return chunk.getCapability(ModDecalCapabilities.DECALS)
                .orElseThrow(() -> new IllegalStateException(
                        "Decal capability missing from chunk [" + chunk.getPos() + "]."
                ));
    }

    private DecalCapabilities() {}
}
