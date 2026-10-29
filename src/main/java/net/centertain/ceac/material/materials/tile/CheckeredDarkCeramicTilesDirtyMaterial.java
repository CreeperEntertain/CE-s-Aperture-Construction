package net.centertain.ceac.material.materials.tile;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class CheckeredDarkCeramicTilesDirtyMaterial extends Material {
    public CheckeredDarkCeramicTilesDirtyMaterial() {
        super(
                "Checkered Dark Ceramic Tiles (Dirty)",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/tile/checkered_dark_ceramic_tiles_dirty/main"))
                )
        );
    }
}
