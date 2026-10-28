package net.centertain.ceac.material.materials.backpanel;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class BareBackpanelMaterial extends Material {
    public BareBackpanelMaterial() {
        super(
                "Bare Backpanel",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/backpanel/bare_backpanel/main"))
                )
        );
    }
}
