package net.centertain.ceac.decal.client.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
    @Accessor("COLOR_WRITE")
    static RenderStateShard.WriteMaskStateShard ceac$getColorWrite() {
        throw new AssertionError();
    }
}
