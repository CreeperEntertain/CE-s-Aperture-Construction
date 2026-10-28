package net.centertain.ceac.material.materials.concrete;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class SmallDirtyConcreteFloorTilesMaterial extends Material {
    public SmallDirtyConcreteFloorTilesMaterial() {
        super(
                "Small Dirty Concrete Floor Tiles",
                SoundType.STONE,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/concrete/small_dirty_concrete_floor_tiles/main"))
                )
        );
    }
}
