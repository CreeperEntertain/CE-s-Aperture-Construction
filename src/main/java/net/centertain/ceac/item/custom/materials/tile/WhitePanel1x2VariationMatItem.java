package net.centertain.ceac.item.custom.materials.tile;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class WhitePanel1x2VariationMatItem extends MatItem {
    public WhitePanel1x2VariationMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.TILE,
                ModMaterials.WHITE_PANEL_1X2_VARIATION
        );
    }
}
