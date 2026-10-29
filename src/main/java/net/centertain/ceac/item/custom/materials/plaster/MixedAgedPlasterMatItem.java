package net.centertain.ceac.item.custom.materials.plaster;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class MixedAgedPlasterMatItem extends MatItem {
    public MixedAgedPlasterMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.PLASTER,
                ModMaterials.MIXED_AGED_PLASTER
        );
    }
}
