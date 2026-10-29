package net.centertain.ceac.item.custom.materials.tile;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class WhitePinHoleCeilingTilesMatItem extends MatItem {
    public WhitePinHoleCeilingTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.TILE,
                ModMaterials.WHITE_PIN_HOLE_CEILING_TILES
        );
    }
}
