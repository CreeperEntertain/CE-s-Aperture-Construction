package net.centertain.ceac.prop.props;

import net.centertain.ceac.prop.PropDefinition;
import net.centertain.ceac.prop.ModPropModels;
import net.centertain.ceac.prop.physics.PropPhysDefinition;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.ToolActions;

public final class TestProp extends PropDefinition {
    public TestProp() {
        super(
                ModPropModels.TEST_PROP,
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
        );
    }
}
