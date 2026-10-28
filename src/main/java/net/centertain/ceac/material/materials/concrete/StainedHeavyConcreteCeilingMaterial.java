package net.centertain.ceac.material.materials.concrete;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class StainedHeavyConcreteCeilingMaterial extends Material {
    public StainedHeavyConcreteCeilingMaterial() {
        super(
                "Stained Heavy Concrete Ceiling",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/concrete/stained_heavy_concrete_ceiling/top_left")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/concrete/stained_heavy_concrete_ceiling/top_right")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/concrete/stained_heavy_concrete_ceiling/bottom_left")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/concrete/stained_heavy_concrete_ceiling/bottom_right"))
                )
        );
    }
}
