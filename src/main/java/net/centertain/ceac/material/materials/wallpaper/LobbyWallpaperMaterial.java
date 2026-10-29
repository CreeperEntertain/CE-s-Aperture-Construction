package net.centertain.ceac.material.materials.wallpaper;

import net.centertain.ceac.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector2i;

import java.util.Map;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class LobbyWallpaperMaterial extends Material {
    public LobbyWallpaperMaterial() {
        super(
                "Lobby Wallpaper",
                SoundType.BAMBOO_WOOD,
                Map.ofEntries(
                        Map.entry(new Vector2i(0,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/wallpaper/lobby_wallpaper/top_left")),
                        Map.entry(new Vector2i(1,0), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/wallpaper/lobby_wallpaper/top_right")),
                        Map.entry(new Vector2i(0,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/wallpaper/lobby_wallpaper/bottom_left")),
                        Map.entry(new Vector2i(1,1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "material/wallpaper/lobby_wallpaper/bottom_right"))
                )
        );
    }
}
