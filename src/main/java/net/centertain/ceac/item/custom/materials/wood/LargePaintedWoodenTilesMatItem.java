package net.centertain.ceac.item.custom.materials.wood;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class LargePaintedWoodenTilesMatItem extends MatItem {
    public LargePaintedWoodenTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.WOOD,
                ModMaterials.LARGE_PAINTED_WOODEN_TILES
        );
    }
}
