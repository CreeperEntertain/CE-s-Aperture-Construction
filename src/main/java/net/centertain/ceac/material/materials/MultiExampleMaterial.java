package net.centertain.ceac.material.materials;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class MultiExampleMaterial extends Material {
    public MultiExampleMaterial() {
        super(
                "Multi Example",
                SoundType.METAL,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/0_0")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/0_1")),
                        Map.entry(new Vector2i(0,2), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/0_2")),
                        Map.entry(new Vector2i(0,3), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/0_3")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/1_0")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/1_1")),
                        Map.entry(new Vector2i(1,2), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/1_2")),
                        Map.entry(new Vector2i(1,3), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/1_3")),
                        Map.entry(new Vector2i(2,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/2_0")),
                        Map.entry(new Vector2i(2,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/2_1")),
                        Map.entry(new Vector2i(2,2), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/2_2")),
                        Map.entry(new Vector2i(2,3), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/2_3")),
                        Map.entry(new Vector2i(3,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/3_0")),
                        Map.entry(new Vector2i(3,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/3_1")),
                        Map.entry(new Vector2i(3,2), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/3_2")),
                        Map.entry(new Vector2i(3,3), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/multi_example/3_3"))
                )
        );
    }
}
