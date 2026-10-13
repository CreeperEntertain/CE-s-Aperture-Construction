package net.centertain.ceac.prop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PropDefinitions {
    private static final Map<ResourceLocation, PropDefinition> DEFINITIONS = new HashMap<>();

    static {
        register(PropTypes.TEST_PROP, new PropDefinition(
                PropModels.TEST_PROP,
                SoundType.WOOD,
                2.0f,
                true,
                true
        ));
    }

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
