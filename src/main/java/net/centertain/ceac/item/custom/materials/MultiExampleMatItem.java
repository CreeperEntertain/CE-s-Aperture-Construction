package net.centertain.ceac.item.custom.materials;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class MultiExampleMatItem extends MatItem {
    public MultiExampleMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.MISC,
                ModMaterials.MULTI_EXAMPLE
        );
    }
}
