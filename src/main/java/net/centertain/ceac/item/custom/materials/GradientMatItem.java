package net.centertain.ceac.item.custom.materials;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class GradientMatItem extends MatItem {
    public GradientMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.MISC,
                ModMaterials.GRADIENT
        );
    }
}
