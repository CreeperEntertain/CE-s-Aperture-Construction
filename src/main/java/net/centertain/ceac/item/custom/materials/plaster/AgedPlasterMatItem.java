package net.centertain.ceac.item.custom.materials.plaster;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class AgedPlasterMatItem extends MatItem {
    public AgedPlasterMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.PLASTER,
                ModMaterials.AGED_PLASTER
        );
    }
}
