package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class BlackMetalTilesMatItem extends MatItem {
    public BlackMetalTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.BLACK_METAL_TILES
        );
    }
}
