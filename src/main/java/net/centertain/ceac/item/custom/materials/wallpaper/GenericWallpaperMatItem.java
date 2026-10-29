package net.centertain.ceac.item.custom.materials.wallpaper;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class GenericWallpaperMatItem extends MatItem {
    public GenericWallpaperMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.WALLPAPER,
                ModMaterials.GENERIC_WALLPAPER
        );
    }
}
