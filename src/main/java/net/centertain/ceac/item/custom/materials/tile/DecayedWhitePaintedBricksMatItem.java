package net.centertain.ceac.item.custom.materials.tile;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DecayedWhitePaintedBricksMatItem extends MatItem {
    public DecayedWhitePaintedBricksMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.TILE,
                ModMaterials.DECAYED_WHITE_PAINTED_BRICKS
        );
    }
}
