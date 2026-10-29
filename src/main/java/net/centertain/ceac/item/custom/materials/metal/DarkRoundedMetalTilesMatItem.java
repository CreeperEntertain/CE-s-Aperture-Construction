package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DarkRoundedMetalTilesMatItem extends MatItem {
    public DarkRoundedMetalTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.DARK_ROUNDED_METAL_TILES
        );
    }
}
