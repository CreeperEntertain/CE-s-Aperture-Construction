package net.centertain.ceac.material.materials.tile;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class WhitePanel1x1WitheredMaterial extends Material {
    public WhitePanel1x1WitheredMaterial() {
        super(
                "White Panel (1x1, Withered)",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/tile/white_panel_1x1_withered/main"))
                )
        );
    }
}
