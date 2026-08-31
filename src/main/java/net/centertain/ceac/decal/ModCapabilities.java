package net.centertain.ceac.decal;

import net.centertain.ceac.decal.server.DecalManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class ModCapabilities {
    public static final Capability<DecalManager> DECALS =
            CapabilityManager.get(new CapabilityToken<>() {});

    private ModCapabilities() {}
}