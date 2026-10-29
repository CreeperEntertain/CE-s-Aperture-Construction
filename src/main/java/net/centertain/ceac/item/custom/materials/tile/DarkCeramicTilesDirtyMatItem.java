package net.centertain.ceac.item.custom.materials.tile;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DarkCeramicTilesDirtyMatItem extends MatItem {
    public DarkCeramicTilesDirtyMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.TILE,
                ModMaterials.DARK_CERAMIC_TILES_DIRTY
        );
    }
}
