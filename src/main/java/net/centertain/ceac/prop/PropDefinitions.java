package net.centertain.ceac.prop;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PropDefinitions {
    private static final Map<ResourceLocation, PropDefinition> DEFINITIONS = new HashMap<>();

    private PropDefinitions() {}

    public static void register(
            ResourceLocation id,
            PropDefinition definition
    ) {
        if (DEFINITIONS.putIfAbsent(id, definition) != null)
            throw new IllegalArgumentException("Duplicate prop definition: " + id);
    }

    public static Optional<PropDefinition> get(ResourceLocation id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }
}
