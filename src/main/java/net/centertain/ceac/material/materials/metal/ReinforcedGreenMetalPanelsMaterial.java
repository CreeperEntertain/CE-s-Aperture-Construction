package net.centertain.ceac.material.materials.metal;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class ReinforcedGreenMetalPanelsMaterial extends Material {
    public ReinforcedGreenMetalPanelsMaterial() {
        super(
                "Reinforced Green Metal Panels",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/reinforced_green_metal_panels/top_left")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/reinforced_green_metal_panels/top_right")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/reinforced_green_metal_panels/bottom_left")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/reinforced_green_metal_panels/bottom_right"))
                )
        );
    }
}
