package net.centertain.ceac.prop.framework.common;

import net.centertain.ceac.prop.framework.server.PropManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class ModPropCapabilities {
    public static final Capability<PropManager> PROPS = CapabilityManager.get(new CapabilityToken<PropManager>() {});

    private ModPropCapabilities() {}
}
