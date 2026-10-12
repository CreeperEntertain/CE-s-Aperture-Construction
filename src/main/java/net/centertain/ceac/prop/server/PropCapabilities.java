package net.centertain.ceac.prop.server;

import net.centertain.ceac.prop.ModPropCapabilities;
import net.minecraft.world.level.chunk.LevelChunk;

public final class PropCapabilities {
    public static PropManager get(LevelChunk chunk) {
        return chunk.getCapability(ModPropCapabilities.PROPS).orElseThrow(
                () -> new IllegalStateException("Prop capability missing from chunk [" + chunk.getPos() + "]")
        );
    }
    private PropCapabilities() {}
}
