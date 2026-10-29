package net.centertain.ceac.material.materials.metal;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class BlackPanel05x1DirtyMaterial extends Material {
    public BlackPanel05x1DirtyMaterial() {
        super(
                "Black Metal Panel (0.5x1, Dirty)",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/black_metal_panel_05x1_dirty/main"))
                )
        );
    }
}
