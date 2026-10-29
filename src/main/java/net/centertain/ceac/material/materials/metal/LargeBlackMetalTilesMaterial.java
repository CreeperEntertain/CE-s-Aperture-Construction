package net.centertain.ceac.material.materials.metal;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class LargeBlackMetalTilesMaterial extends Material {
    public LargeBlackMetalTilesMaterial() {
        super(
                "Large Black Metal Tiles",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/large_black_metal_tiles/top_left")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/large_black_metal_tiles/top_right")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/large_black_metal_tiles/bottom_left")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/large_black_metal_tiles/bottom_right"))
                )
        );
    }
}
