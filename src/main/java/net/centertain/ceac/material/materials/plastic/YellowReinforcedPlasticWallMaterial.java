package net.centertain.ceac.material.materials.plastic;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class YellowReinforcedPlasticWallMaterial extends Material {
    public YellowReinforcedPlasticWallMaterial() {
        super(
                "Yellow Reinforced Plastic Wall",
                SoundType.BAMBOO_WOOD,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plastic/yellow_reinforced_plastic_wall/top_left")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plastic/yellow_reinforced_plastic_wall/top_right")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plastic/yellow_reinforced_plastic_wall/bottom_left")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plastic/yellow_reinforced_plastic_wall/bottom_right"))
                )
        );
    }
}
