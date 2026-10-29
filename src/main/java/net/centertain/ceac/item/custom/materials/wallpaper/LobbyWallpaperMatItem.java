package net.centertain.ceac.item.custom.materials.wallpaper;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class LobbyWallpaperMatItem extends MatItem {
    public LobbyWallpaperMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.WALLPAPER,
                ModMaterials.LOBBY_WALLPAPER
        );
    }
}
