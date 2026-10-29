package net.centertain.ceac.material.materials.plaster;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class BarePlasterMaterial extends Material {
    public BarePlasterMaterial() {
        super(
                "Bare Plaster",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plaster/bare_plaster/top")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/plaster/bare_plaster/bottom"))
                )
        );
    }
}
