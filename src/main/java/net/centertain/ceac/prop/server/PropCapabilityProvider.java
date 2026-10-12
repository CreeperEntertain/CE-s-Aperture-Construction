package net.centertain.ceac.prop.server;

import net.centertain.ceac.prop.ModPropCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PropCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final PropManager manager;
    private final LazyOptional<PropManager> optional;

    public PropCapabilityProvider(LevelChunk chunk) {
        manager = new PropManager(chunk);
        optional = LazyOptional.of(() -> manager);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
            @NotNull Capability<T> capability,
            @Nullable Direction side
    ) {
        if (capability == ModPropCapabilities.PROPS)
            return optional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return manager.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        manager.deserializeNBT(nbt);
    }

    public void invalidate() {
        optional.invalidate();
    }
}
