package net.centertain.ceac.item.custom.materials.wood;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class SmallPaintedWoodenTilesMatItem extends MatItem {
    public SmallPaintedWoodenTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.WOOD,
                ModMaterials.SMALL_PAINTED_WOODEN_TILES
        );
    }
}
