package net.centertain.ceac.item.custom.materials.concrete;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class StainedHeavyConcreteCeilingMatItem extends MatItem {
    public StainedHeavyConcreteCeilingMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.CONCRETE,
                ModMaterials.STAINED_HEAVY_CONCRETE_CEILING
        );
    }
}
