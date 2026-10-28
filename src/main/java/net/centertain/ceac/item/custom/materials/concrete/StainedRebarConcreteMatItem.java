package net.centertain.ceac.item.custom.materials.concrete;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class StainedRebarConcreteMatItem extends MatItem {
    public StainedRebarConcreteMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.CONCRETE,
                ModMaterials.STAINED_REBAR_CONCRETE
        );
    }
}
