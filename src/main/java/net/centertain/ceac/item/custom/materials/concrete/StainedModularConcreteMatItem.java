package net.centertain.ceac.item.custom.materials.concrete;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class StainedModularConcreteMatItem extends MatItem {
    public StainedModularConcreteMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.CONCRETE,
                ModMaterials.STAINED_MODULAR_CONCRETE
        );
    }
}
