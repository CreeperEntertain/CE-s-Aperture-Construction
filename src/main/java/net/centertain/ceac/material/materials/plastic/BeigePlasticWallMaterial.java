package net.centertain.ceac.material.materials.plastic;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class BeigePlasticWallMaterial extends Material {
    public BeigePlasticWallMaterial() {
        super(
                "Beige Plastic Wall",
                SoundType.BAMBOO_WOOD,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plastic/beige_plastic_wall/top")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plastic/beige_plastic_wall/bottom"))
                )
        );
    }
}
