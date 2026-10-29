package net.centertain.ceac.item.custom.materials.wood;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class BarbedPlywoodMatItem extends MatItem {
    public BarbedPlywoodMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.WOOD,
                ModMaterials.BARBED_PLYWOOD
        );
    }
}
