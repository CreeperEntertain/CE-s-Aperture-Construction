package net.centertain.ceac.decal;

import net.minecraft.world.level.chunk.LevelChunk;

public class DecalCapabilities {
    public static DecalManager get(LevelChunk chunk) {
        return chunk.getCapability(ModCapabilities.DECALS)
                .orElseThrow(() -> new IllegalStateException(
                        "Decal capability missing from chunk [" + chunk.getPos() + "]."
                ));
    }

    private DecalCapabilities() {}
}
