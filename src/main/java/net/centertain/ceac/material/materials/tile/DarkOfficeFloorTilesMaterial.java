package net.centertain.ceac.material.materials.tile;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class DarkOfficeFloorTilesMaterial extends Material {
    public DarkOfficeFloorTilesMaterial() {
        super(
                "Dark Office Floor Tiles",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/tile/dark_office_floor_tiles/main"))
                )
        );
    }
}
