package net.centertain.ceac.item.custom.materials;

import net.centertain.ceac.CategoryConstants;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class ExampleMatItem extends MatItem {
    public ExampleMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.MISC,
                ModMaterials.EXAMPLE
        );
    }
}
