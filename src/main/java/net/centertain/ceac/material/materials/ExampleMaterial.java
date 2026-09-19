package net.centertain.ceac.material.materials;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class ExampleMaterial extends Material {
    public ExampleMaterial() {
        super(
                "Example",
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/example"))
                )
        );
    }
}
