package net.centertain.ceac.prop;

import net.centertain.ceac.prop.physics.PropPhysDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.ToolActions;

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
                true,
                ToolActions.AXE_DIG,
                -1,
                null,
                PropPhysDefinition.builder()
                        .density(80.0f)
                        .build()
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
