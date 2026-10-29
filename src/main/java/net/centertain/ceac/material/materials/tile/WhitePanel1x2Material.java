package net.centertain.ceac.material.materials.tile;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class WhitePanel1x2Material extends Material {
    public WhitePanel1x2Material() {
        super(
                "White Panel (1x2)",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/tile/white_panel_1x2/top")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/tile/white_panel_1x2/bottom"))
                )
        );
    }
}
