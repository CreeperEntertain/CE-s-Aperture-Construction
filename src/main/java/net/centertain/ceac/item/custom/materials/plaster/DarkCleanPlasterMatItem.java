package net.centertain.ceac.item.custom.materials.plaster;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DarkCleanPlasterMatItem extends MatItem {
    public DarkCleanPlasterMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.PLASTER,
                ModMaterials.DARK_CLEAN_PLASTER
        );
    }
}
