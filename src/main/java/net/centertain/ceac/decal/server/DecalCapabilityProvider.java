package net.centertain.ceac.decal.server;

import net.centertain.ceac.decal.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecalCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final DecalManager manager;
    private final LazyOptional<DecalManager> optional;

    public DecalCapabilityProvider(LevelChunk chunk) {
        this.manager = new DecalManager(chunk);
        this.optional = LazyOptional.of(() -> manager);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
            @NotNull Capability<T> capability,
            @Nullable Direction side
    ) {
        if (capability == ModCapabilities.DECALS) {
            return optional.cast();
        }

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