package net.centertain.ceac.material.materials.metal;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class FactoryContainerSideMaterial extends Material {
    public FactoryContainerSideMaterial() {
        super(
                "Factory Container (Side)",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/factory_container_side/top_left")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/factory_container_side/top_right")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/factory_container_side/bottom_left")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/metal/factory_container_side/bottom_right"))
                )
        );
    }
}
