package net.centertain.ceac.material.materials.metal;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class WeatheredCorrugatedMetalMaterial extends Material {
    public WeatheredCorrugatedMetalMaterial() {
        super(
                "Weathered Corrugated Metal",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/weathered_corrugated_metal/top")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/weathered_corrugated_metal/bottom"))
                )
        );
    }
}
