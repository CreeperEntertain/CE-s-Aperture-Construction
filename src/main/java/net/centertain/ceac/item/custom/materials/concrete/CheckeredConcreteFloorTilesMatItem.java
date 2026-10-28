package net.centertain.ceac.item.custom.materials.concrete;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class CheckeredConcreteFloorTilesMatItem extends MatItem {
    public CheckeredConcreteFloorTilesMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.CONCRETE,
                ModMaterials.CHECKERED_CONCRETE_FLOOR_TILES
        );
    }
}
