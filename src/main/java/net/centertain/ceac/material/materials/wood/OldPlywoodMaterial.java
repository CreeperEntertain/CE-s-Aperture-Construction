package net.centertain.ceac.material.materials.wood;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class OldPlywoodMaterial extends Material {
    public OldPlywoodMaterial() {
        super(
                "Old Plywood",
                SoundType.WOOD,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/wood/old_plywood/main"))
                )
        );
    }
}
