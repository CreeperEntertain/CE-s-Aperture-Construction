package net.centertain.ceac.item.custom.materials.tile;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DarkPinHoleCeilingTilesMatItem extends MatItem {
    public DarkPinHoleCeilingTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.TILE,
                ModMaterials.DARK_PIN_HOLE_CEILING_TILES
        );
    }
}
