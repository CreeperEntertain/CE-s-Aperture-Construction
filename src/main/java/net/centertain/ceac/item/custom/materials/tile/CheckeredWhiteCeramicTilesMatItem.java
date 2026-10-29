package net.centertain.ceac.item.custom.materials.tile;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class CheckeredWhiteCeramicTilesMatItem extends MatItem {
    public CheckeredWhiteCeramicTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.TILE,
                ModMaterials.CHECKERED_WHITE_CERAMIC_TILES
        );
    }
}
